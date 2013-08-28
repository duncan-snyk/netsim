package uk.co.ukmaker.netsim.amqp.messages.nodereply;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class PropagatedNetDriversMessage implements Message {
	
	public static final String TYPE = "PND";
	
	private Map<String, Integer> netDrivers;
	
	public PropagatedNetDriversMessage() {
		
	}

	public PropagatedNetDriversMessage(Map<String, Integer> netDrivers) {
		super();
		this.netDrivers = netDrivers;
	}

	public Map<String, Integer> getNetDrivers() {
		return netDrivers;
	}

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
	}
}
