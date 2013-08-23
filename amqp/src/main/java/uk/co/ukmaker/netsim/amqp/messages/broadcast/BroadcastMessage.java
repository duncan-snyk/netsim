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
		
		String type = headers.get(TYPE_HEADER).toString();
		
		return new BroadcastMessage(Type.valueOf(type));

	}

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, type.toString());
	}

	@Override
	public byte[] getBytes() {
		return null;
	}
}
