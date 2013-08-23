package uk.co.ukmaker.netsim.amqp.messages.broadcast;

import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class BroadcastMessage implements Message {
	
	private final Type type;
	
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
	
	public static BroadcastMessage read(Map<String, Object> headers, byte[] bytes) {
		
		String type = (String) headers.get(TYPE_HEADER);
		
		return new BroadcastMessage(Type.valueOf(type));

	}

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, type);
	}

	@Override
	public byte[] getBytes() {
		return null;
	}
}
