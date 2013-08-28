package uk.co.ukmaker.netsim.amqp.messages.discovery;

import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class EnumeratedMessage implements Message {
	
	public static final String TYPE = "ENM";
	
	private String name;
	private long ramSize;
	
	public EnumeratedMessage() {
		
	}

	public EnumeratedMessage(String name, long ramSize) {
		super();
		this.name = name;
		this.ramSize = ramSize;
	}

	public String getName() {
		return name;
	}

	public long getRamSize() {
		return ramSize;
	}

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
	}
}
