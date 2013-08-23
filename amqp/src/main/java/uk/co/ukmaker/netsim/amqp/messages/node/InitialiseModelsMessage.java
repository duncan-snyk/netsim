package uk.co.ukmaker.netsim.amqp.messages.node;

import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class InitialiseModelsMessage implements Message {
	
	public static final String TYPE = "INIT";

	@Override
	public byte[] getBytes() {
		return null;
	}

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
	}
	
	public static InitialiseModelsMessage read(Map<String, Object> headers, byte[] bytes) {
		return new InitialiseModelsMessage();
	}
}
