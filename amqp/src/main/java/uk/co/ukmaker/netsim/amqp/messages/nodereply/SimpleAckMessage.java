package uk.co.ukmaker.netsim.amqp.messages.nodereply;

import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class SimpleAckMessage implements Message {
	
	public static final String TYPE = "ACK";

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
	}

	@Override
	public byte[] getBytes() {
		return null;
	}
	
	public static SimpleAckMessage read(Map<String, Object> headers, byte[] bytes) {
		return new SimpleAckMessage();
	}

}
