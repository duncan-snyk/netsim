package uk.co.ukmaker.netsim.amqp.responses;

import uk.co.ukmaker.netsim.amqp.ClusterNode;

public class ResponseFactory {
	
	public static Response decode(ClusterNode node, String type, byte[] data) {
		if("INITIALISE".equals(type)) {
			return InitialiseResponse.decode(node, data);
		}
		
		throw new RuntimeException("Unknown ClusterNode response type "+type);
	}

}
