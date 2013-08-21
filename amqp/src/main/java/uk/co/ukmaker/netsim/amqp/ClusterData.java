package uk.co.ukmaker.netsim.amqp;

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
}
