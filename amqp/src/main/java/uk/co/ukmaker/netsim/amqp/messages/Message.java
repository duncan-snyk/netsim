package uk.co.ukmaker.netsim.amqp.messages;

public interface Message {
	
	public String getType();

	public byte[] getBytes();
	
}
