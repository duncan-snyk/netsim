package uk.co.ukmaker.netsim.amqp.master;

import static uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage.Type.CLEAR;
import static uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage.Type.RESET;
import uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage;
import uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage.Type;

abstract public class ClusterMaster {
	
	private ClusterData cluster;

	abstract public void initialize() throws Exception;
	
	abstract public void broadcast(BroadcastMessage m) throws Exception;
	
	abstract public ClusterData readDiscoveryQueue() throws Exception;
	
	public ClusterData discoverNodes() throws Exception {
		
		cluster = new ClusterData();
		cluster.setState(ClusterData.State.ENUMERATING);
		
		broadcast(new BroadcastMessage(BroadcastMessage.Type.ENUMERATE));
		
		cluster = readDiscoveryQueue();

		cluster.setState(ClusterData.State.ENUMERATED);
		
		return cluster;
	}
	
	public void clearAll() throws Exception {
		broadcast(new BroadcastMessage(CLEAR));
	}
	
	public void resetAll() throws Exception {
		broadcast(new BroadcastMessage(RESET));
	}
	
	public void connectNets() throws Exception {
		broadcast(new BroadcastMessage(Type.CONNECT_NETS));
	}
}
