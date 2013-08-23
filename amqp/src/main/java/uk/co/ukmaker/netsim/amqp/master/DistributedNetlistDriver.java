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
import uk.co.ukmaker.netsim.amqp.messages.node.PropagateOutputsMessage;
import uk.co.ukmaker.netsim.amqp.messages.nodereply.PropagatedNetDriversMessage;
import uk.co.ukmaker.netsim.amqp.messages.nodereply.UpdateEventQueueMessage;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.netlist.Net;
import uk.co.ukmaker.netsim.netlist.Netlist;
import uk.co.ukmaker.netsim.simulation.NetlistDriver;
import uk.co.ukmaker.netsim.simulation.SimulatorCallbackHandler;

public class DistributedNetlistDriver implements NetlistDriver {
	
	private Netlist netlist;
	private ClusterData cluster;
	
	// Keep track of which nodes have which nets
	private Map<String, Set<ClusterNode>> netNodeMap;
	
	@Override
	public void setNetlist(Netlist netlist) {
		this.netlist = netlist;
	}
	
	public void installModels(ClusterData cluster, Netlist netlist) throws IOException {
		
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
	
	public void installModel(Model m, ClusterNode n) throws IOException {
		n.installModel(m);
		for(Net net : m.getNets()) {
			if(!netNodeMap.containsKey(net.getId())) {
				netNodeMap.put(net.getId(),  new HashSet<ClusterNode>());
			}
			netNodeMap.get(net.getId()).add(n);
		}
	}

	@Override
	public void initialise(SimulatorCallbackHandler callbackHandler) throws Exception {
		// send an initialise message to each node
		// then wait for the responses
		List<Future<Message>> responses = new ArrayList<>();
		for(ClusterNode n : cluster.getNodes()) {
			responses.add(n.initialiseModels());
		}
		
		// Wait for the responses and callback to the simulator as we get them
		for(Future<Message> response : responses) {
			UpdateEventQueueMessage m = (UpdateEventQueueMessage)response.get();
			callbackHandler.updateEventQueue(m.getNetMoments());
		}
	}

	@Override
	public void propagateOutputs(long moment, Set<String> netIds,
			SimulatorCallbackHandler callbackHandler) throws Exception {
		
		// Broadcast the list of nets to all the nodes.
		
		// It might be more appropriate to
		// construct an appropriate message for each node in the cluster
		// just telling them to update the nets they know about
		
		PropagateOutputsMessage m = new PropagateOutputsMessage(moment, netIds);
		List<Future<Message>> responses = new ArrayList<>();
		for(ClusterNode n : cluster.getNodes()) {
			responses.add(n.propagateOutputs(m));
		}
		
		// Wait for the responses and callback to the simulator as we get them
		for(Future<Message> response : responses) {
			PropagatedNetDriversMessage r = (PropagatedNetDriversMessage)response.get();
			callbackHandler.propagatedNetDrivers(r.getNetDrivers());
		}
	}

	@Override
	public void propagateInputs(long moment, Map<String, Integer> netDrivers,
			SimulatorCallbackHandler callbackHandler) {
		
		
		
	}

	@Override
	public void updateModels(long moment, SimulatorCallbackHandler callbackHandler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scheduleNetValue(String netId, ScheduledValue value) {
		// TODO Auto-generated method stub
		
	}
}
