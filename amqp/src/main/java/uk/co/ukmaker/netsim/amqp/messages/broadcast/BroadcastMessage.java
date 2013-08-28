package uk.co.ukmaker.netsim.amqp.messages.broadcast;

import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class BroadcastMessage implements Message {
	
	private Type type;
	
	public BroadcastMessage() {
		
	}
	
	public BroadcastMessage(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public static enum Type {
    	
    	CLEAR,
    	ENUMERATE,
    	RESET,
    	CONNECT_NETS
    	;
	}

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, type.toString());
	}
}
