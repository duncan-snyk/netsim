package uk.co.ukmaker.netsim.amqp.messages.nodereply;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class NodeReplyMessageFactory {
	
	private static ObjectMapper mapper = new ObjectMapper();

	public static Message decode(Map<String, Object> headers,
			byte[] bytes)  throws Exception {
		
		String type = headers.get(Message.TYPE_HEADER).toString();
		
		if(PropagatedNetDriversMessage.TYPE.equals(type)) {
			
			return mapper.readValue(bytes, PropagatedNetDriversMessage.class);
			
			//return PropagatedNetDriversMessage.read(headers, bytes);
		}
		
		if(UpdateEventQueueMessage.TYPE.equals(type)) {
			
			return mapper.readValue(bytes,  UpdateEventQueueMessage.class);
			
			//return UpdateEventQueueMessage.read(headers, bytes);
		}
		
		if(SimpleAckMessage.TYPE.equals(type)) {
			
			return mapper.readValue(bytes, SimpleAckMessage.class)
					;
		//	return SimpleAckMessage.read(headers, bytes);
		}
		
		throw new Exception("Unknown NodeReply message type "+type);
	}

}
