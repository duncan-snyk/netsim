package uk.co.ukmaker.netsim.amqp.messages.nodereply;

import java.util.Map;

import uk.co.ukmaker.netsim.amqp.master.ClusterNode;
import uk.co.ukmaker.netsim.amqp.messages.Message;

public class NodeReplyMessageFactory {

	public static Message decode(ClusterNode node, Map<String, Object> headers,
			byte[] bytes)  throws Exception {
		
		String type = headers.get(Message.TYPE_HEADER).toString();
		
		if(PropagatedNetDriversMessage.TYPE.equals(type)) {
			return PropagatedNetDriversMessage.read(headers, bytes);
		}
		
		if(UpdateEventQueueMessage.TYPE.equals(type)) {
			return UpdateEventQueueMessage.read(headers, bytes);
		}
		
		if(SimpleAckMessage.TYPE.equals(type)) {
			return SimpleAckMessage.read(headers, bytes);
		}
		
		throw new Exception("Unknown NodeReply message type "+type);
	}

}
