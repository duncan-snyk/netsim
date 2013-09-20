package uk.co.ukmaker.netsim.amqp.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.amqp.messages.NetsimMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.PropagateInputsMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.PropagateOutputsMessage;
import uk.co.ukmaker.netsim.amqp.messages.nodereply.PropagatedNetDriversMessage;
import uk.co.ukmaker.netsim.amqp.messages.nodereply.UpdateEventQueueMessage;
import uk.co.ukmaker.netsim.amqp.node.RemoteNode;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.models.test.TestProbe;
import uk.co.ukmaker.netsim.netlist.Net;
import uk.co.ukmaker.netsim.netlist.Netlist;
import uk.co.ukmaker.netsim.simulation.NetlistDriver;
import uk.co.ukmaker.netsim.simulation.NetEventPropagator;

public class DistributedNetlistDriver implements NetlistDriver {
	
	private Netlist netlist;
	private ClusterData cluster;
	
	// Keep track of which nodes have which nets
	private Map<ClusterNode, Set<String>> netNodeMap;
	
	@Override
	public void setNetlist(Netlist netlist) {
		this.netlist = netlist;
	}
	
	/**
	 * Install models on nodes.
	 * If there is only a local node, install everything on that
	 * Otherwise only install test pins on the local node and install the  other models on the other nodes
	 * @param cluster
	 * @param localNode
	 * @throws Exception
	 */
	public void installModels(ClusterData cluster, RemoteNode localNode) throws Exception {
		
		ClusterNode localClusterNode = cluster.getNode(localNode.getNode().getName());
		
		this.cluster = cluster;
		netNodeMap = new HashMap<ClusterNode, Set<String>>();
		
		List<Model> models = new ArrayList<Model>();
		List<TestProbe> testProbes = netlist.getTestProbes();
		// Install the testprobes on the local node
		for(TestProbe probe : testProbes) {
			installModel(probe,  localClusterNode);
		}
		
		// build the list of non-probe models
		for(Model m : netlist.getModels()) {
			if(!(m instanceof TestProbe)) {
				models.add(m);
			}
		}
		
		// give each node a fair proportion of the models
		// we really ought to hae some way of weighting things
		// as a function of e.g. memory used, but hey
		
		List<ClusterNode> remoteNodes = new ArrayList<ClusterNode>();
		for(ClusterNode n : cluster.getNodes()) {
			if(n != localClusterNode) {
				remoteNodes.add(n);
			}
		}
		
		if(remoteNodes.size() == 0) {
			remoteNodes.add(localClusterNode);
		}
		
		int nonProbeModels = models.size();
		
		int modelsPerNode;
		int leftovers;
		
		modelsPerNode = nonProbeModels / remoteNodes.size();
		leftovers = nonProbeModels - (modelsPerNode * remoteNodes.size());
		
		Iterator<Model> modelsIterator = models.listIterator();
		
		for(ClusterNode node : remoteNodes) {
			for(int i=0; i<modelsPerNode; i++) {
				
				Model m = modelsIterator.next();
				installModel(m, node);				
			}
		}
		
		// put the leftovers all on the first remote node
		ClusterNode node = remoteNodes.get(0);
		
		for(int i=0; i<leftovers; i++) {
			Model m = modelsIterator.next();
			
			installModel(m, node);				
		}
	}
	
	public void installModel(Model m, ClusterNode n) throws Exception {
		Future<NetsimMessage> ack = n.installModel(m);
		for(Net net : m.getNets()) {
			if(!netNodeMap.containsKey(n)) {
				netNodeMap.put(n,  new HashSet<String>());
			}
			netNodeMap.get(n).add(net.getId());
		}
		
		ack.get();
	}

	@Override
	public Map<String, Set<Long>> initialiseModels() throws Exception {
		// send an initialise message to each node
		// then wait for the responses
		List<Future<NetsimMessage>> responses = new ArrayList<>();
		
		Map<String, Set<Long>> nextValues = new HashMap<String, Set<Long>>();
		
		for(ClusterNode n : cluster.getNodes()) {
			responses.add(n.initialiseModels());
		}
		
		// Wait for the responses and callback to the simulator as we get them
		for(Future<NetsimMessage> response : responses) {
			UpdateEventQueueMessage m = (UpdateEventQueueMessage)response.get();
			for(String netId : m.getNetMoments().keySet()) {
				if(!nextValues.containsKey(netId)) {
					nextValues.put(netId, new HashSet<Long>());
				}
				
				nextValues.get(netId).addAll(m.getNetMoments().get(netId));
			}
		}
		
		return nextValues;
	}

	@Override
	public Map<String, Integer> propagateOutputs(long moment, Set<String> netIds,
			NetEventPropagator propagator) throws Exception {
		
		List<Future<NetsimMessage>> responses = new ArrayList<>();
		for(ClusterNode n : cluster.getNodes()) {
			Set<String> nodeNets = new HashSet<String>();
			for(String netId : netIds) {
				if(netNodeMap.get(n).contains(netId)) {
					nodeNets.add(netId);
				}
			}
			
			if(nodeNets.size() > 0) {
				PropagateOutputsMessage m = new PropagateOutputsMessage(moment, nodeNets);
				responses.add(n.propagateOutputs(m));
			}
		}
		
		// gather up the results as they come in
		Map<String, Integer> netDrivers = new HashMap<String, Integer>();
		
		// Wait for the responses and callback to the simulator as we get them
		for(Future<NetsimMessage> response : responses) {
			PropagatedNetDriversMessage r = (PropagatedNetDriversMessage)response.get();
			
			for(String netId : r.getNetDrivers().keySet()) {
				if(!netDrivers.containsKey(netId)) {
					netDrivers.put(netId,  r.getNetDrivers().get(netId));
				} else {
					netDrivers.put(netId, netDrivers.get(netId) + r.getNetDrivers().get(netId));
				}
			}
		}
		
		return netDrivers;
	}

	@Override
	public void propagateInputs(long moment, Map<String, Integer> netDrivers) throws Exception {
		
		List<Future<NetsimMessage>> responses = new ArrayList<>();
		
		for(ClusterNode n : cluster.getNodes()) {
			
			Map<String, Integer> nodeNetDrivers = new HashMap<String, Integer>();
			for(String netId : netDrivers.keySet()) {
				if(netNodeMap.get(n).contains(netId)) {
					nodeNetDrivers.put(netId, netDrivers.get(netId));
				}
			}
			
			if(nodeNetDrivers.size() > 0) {
				PropagateInputsMessage m = new PropagateInputsMessage(moment, nodeNetDrivers);
				responses.add(n.propagateInputs(m));
			}
		}
		
		// wait for the acks
		for(Future<NetsimMessage> f : responses) {
			f.get();
		}
	}

	@Override
	public Map<String, Set<Long>> updateModels(long moment) throws Exception {
		// send an initialise message to each node
		// then wait for the responses
		List<Future<NetsimMessage>> responses = new ArrayList<>();
		
		Map<String, Set<Long>> nextValues = new HashMap<String, Set<Long>>();
		
		for(ClusterNode n : cluster.getNodes()) {
			responses.add(n.updateModels(moment));
		}
		
		// Wait for the responses and callback to the simulator as we get them
		for(Future<NetsimMessage> response : responses) {
			UpdateEventQueueMessage m = (UpdateEventQueueMessage)response.get();
			for(String netId : m.getNetMoments().keySet()) {
				if(!nextValues.containsKey(netId)) {
					nextValues.put(netId, new HashSet<Long>());
				}
				
				nextValues.get(netId).addAll(m.getNetMoments().get(netId));
			}
		}
		
		return nextValues;
		
	}

	@Override
	public void scheduleNetValue(String netId, ScheduledValue value) {
		throw new RuntimeException("Unexpected attempt to schedule a net value on the master");
	}
}
