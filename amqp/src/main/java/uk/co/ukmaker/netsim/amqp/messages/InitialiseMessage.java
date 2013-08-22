package uk.co.ukmaker.netsim.amqp.messages;

public class InitialiseMessage implements Message {

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return "INITIALISE";
	}
}
