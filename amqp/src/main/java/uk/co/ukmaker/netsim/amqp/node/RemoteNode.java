package uk.co.ukmaker.netsim.amqp.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoteNode {
	
	@Autowired
	private BroadcastListener broadcastListener;
	
	@Autowired
	private NetsListener netsListener;
	
	@Autowired
	private NodeListener nodeListener;
	
	@Autowired
	private Node node;
	
	public void initialise() throws Exception {
		node.initialise();
		netsListener.initialise();
		nodeListener.initialise();
		broadcastListener.initialise();
	}

}
