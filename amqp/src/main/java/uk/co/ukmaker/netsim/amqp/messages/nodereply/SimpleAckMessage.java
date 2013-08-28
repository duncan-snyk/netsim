package uk.co.ukmaker.netsim.amqp.messages.nodereply;

import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class SimpleAckMessage implements Message {
	
	public static final String TYPE = "ACK";
	
	private String message;
	
	public SimpleAckMessage() {
		
	}
	
	public String getMessage() {
		return message;
	}

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
	}
}
