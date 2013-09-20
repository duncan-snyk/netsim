package uk.co.ukmaker.netsim.amqp.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoteNode {
	
	@Autowired
	private BroadcastListener broadcastListener;
	
	@Autowired
	private NodeListener nodeListener;
	
	@Autowired
	private Node node;
	
	private NetsListener netsListener;
	
	public void setNetsListener(NetsListener listener) {
		netsListener = listener;
	}
	
	public void initialise() throws Exception {
		node.initialise();
		netsListener.initialise();
		nodeListener.initialise();
		broadcastListener.initialise();
	}
	
	public Node getNode() {
		return node;
	}

}
