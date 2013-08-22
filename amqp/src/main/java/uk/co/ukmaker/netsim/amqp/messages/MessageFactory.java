package uk.co.ukmaker.netsim.amqp.messages;

import uk.co.ukmaker.netsim.amqp.ClusterNode;

public class MessageFactory {
	
	public static Message decode(ClusterNode node, String type, byte[] data) {
		if("UEQ".equals(type)) {
			return UpdateEventQueueMessage.readBytes(data);
		}
		
		throw new RuntimeException("Unknown ClusterNode response type "+type);
	}

}
