package uk.co.ukmaker.netsim.amqp.master;

import java.util.ArrayList;
import java.util.List;



public class ClusterData {
	
	public static enum State {
		UNINITIALIZED,
		ENUMERATING,
		ENUMERATED,
		INITIALIZED,
		RUNNING
	}
	
	private final List<ClusterNode> nodes = new ArrayList<ClusterNode>();
	
	private State state;
	

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public List<ClusterNode> getNodes() {
		return nodes;
	}
	
	public ClusterNode getNode(String name) {
		for(ClusterNode n : nodes) {
			if(name.equals(n.getName())) {
				return n;
			}
		}
		
		return null;
	}
}
