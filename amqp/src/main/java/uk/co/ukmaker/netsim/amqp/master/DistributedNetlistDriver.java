package uk.co.ukmaker.netsim.amqp.master;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.amqp.messages.Message;
import uk.co.ukmaker.netsim.amqp.messages.node.PropagateInputsMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.PropagateOutputsMessage;
import uk.co.ukmaker.netsim.amqp.messages.nodereply.PropagatedNetDriversMessage;
import uk.co.ukmaker.netsim.amqp.messages.nodereply.UpdateEventQueueMessage;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.netlist.Net;
import uk.co.ukmaker.netsim.netlist.Netlist;
import uk.co.ukmaker.netsim.simulation.NetlistDriver;
import uk.co.ukmaker.netsim.simulation.NetEventPropagator;

public class DistributedNetlistDriver implements NetlistDriver {
	
	private Netlist netlist;
	private ClusterData cluster;
	
	// Keep track of which nodes have which nets
	private Map<String, Set<ClusterNode>> netNodeMap;
	
	@Override
	public void setNetlist(Netlist netlist) {
		this.netlist = netlist;
	}
	
	public void installModels(ClusterData cluster) throws Exception {
		
		this.cluster = cluster;
		netNodeMap = new HashMap<String, Set<ClusterNode>>();
		
		List<Model> models = netlist.getModels();
		// give each node a fair proportion of the models
		// we really ought to hae some way of weighting things
		// as a function of e.g. memory used, but hey
		int modelsPerNode = models.size() / cluster.getNodes().size();
		int leftovers = models.size() - (modelsPerNode * cluster.getNodes().size());
		
		int installed = 0;
		
		for(ClusterNode node : cluster.getNodes()) {
			for(int i=0; i<modelsPerNode; i++) {
				Model m = models.get(installed++);
				installModel(m, node);				
			}
		}
		
		for(ClusterNode node : cluster.getNodes()) {
			for(int i=0; i<leftovers; i++) {
				Model m = models.get(installed++);
				installModel(m, node);				
			}
		}
	}
	
	public void installModel(Model m, ClusterNode n) throws Exception {
		Future<Message> ack = n.installModel(m);
		for(Net net : m.getNets()) {
			if(!netNodeMap.containsKey(net.getId())) {
				netNodeMap.put(net.getId(),  new HashSet<ClusterNode>());
			}
			netNodeMap.get(net.getId()).add(n);
		}
		
		ack.get();
	}

	@Override
	public Map<String, Set<Long>> initialiseModels() throws Exception {
		// send an initialise message to each node
		// then wait for the responses
		List<Future<Message>> responses = new ArrayList<>();
		
		Map<String, Set<Long>> nextValues = new HashMap<String, Set<Long>>();
		
		for(ClusterNode n : cluster.getNodes()) {
			responses.add(n.initialiseModels());
		}
		
		// Wait for the responses and callback to the simulator as we get them
		for(Future<Message> response : responses) {
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
		
		// Broadcast the list of nets to all the nodes.
		
		// It might be more appropriate to
		// construct an appropriate message for each node in the cluster
		// just telling them to update the nets they know about
		
		PropagateOutputsMessage m = new PropagateOutputsMessage(moment, netIds);
		List<Future<Message>> responses = new ArrayList<>();
		for(ClusterNode n : cluster.getNodes()) {
			responses.add(n.propagateOutputs(m));
		}
		
		// gather up the results as they come in
		Map<String, Integer> netDrivers = new HashMap<String, Integer>();
		
		// Wait for the responses and callback to the simulator as we get them
		for(Future<Message> response : responses) {
			PropagatedNetDriversMessage r = (PropagatedNetDriversMessage)response.get();
			
			for(String netId : r.getNetDrivers().keySet()) {
				if(netDrivers.containsKey(netId)) {
					netDrivers.put(netId, netDrivers.get(netId) + r.getNetDrivers().get(netId));
				}
			}
		}
		
		return netDrivers;
	}

	@Override
	public void propagateInputs(long moment, Map<String, Integer> netDrivers) throws Exception {
		// Again, we'll just send the list to all the nodes for the moment
		PropagateInputsMessage m = new PropagateInputsMessage(moment, netDrivers);
		List<Future<Message>> responses = new ArrayList<>();
		
		for(ClusterNode n : cluster.getNodes()) {
			responses.add(n.propagateInputs(m));
		}
		
		// wait for the acks
		for(Future<Message> f : responses) {
			f.get();
		}
	}

	@Override
	public Map<String, Set<Long>> updateModels(long moment) throws Exception {
		// send an initialise message to each node
		// then wait for the responses
		List<Future<Message>> responses = new ArrayList<>();
		
		Map<String, Set<Long>> nextValues = new HashMap<String, Set<Long>>();
		
		for(ClusterNode n : cluster.getNodes()) {
			responses.add(n.updateModels(moment));
		}
		
		// Wait for the responses and callback to the simulator as we get them
		for(Future<Message> response : responses) {
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
