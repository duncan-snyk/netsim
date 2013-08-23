package uk.co.ukmaker.netsim.amqp.messages.discovery;

import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class EnumeratedMessage implements Message {
	
	public static final String TYPE = "ENM";
	
	private final String name;
	private final long ramSize;

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
	public byte[] getBytes() {
		return (name+":"+ramSize).getBytes();
	}

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
	}
	
	public static EnumeratedMessage read(Map<String, Object> headers, byte[] bytes) {
		String[] bits = new String(bytes).split(":");
		return new EnumeratedMessage(bits[0], Long.parseLong(bits[1]));
	}
}
