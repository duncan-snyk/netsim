package uk.co.ukmaker.netsim.amqp.messages.node;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.NetsimMessage;

public class PropagateInputsMessage implements NetsimMessage {
	
	public static final String TYPE="PI";
	
	private long moment;
	private Map<String, Integer> netDrivers;
	
	public PropagateInputsMessage() {
		
	}
	
	public PropagateInputsMessage(long moment, Map<String, Integer> netDrivers) {
		super();
		this.moment = moment;
		this.netDrivers = netDrivers;
	}
	
	public long getMoment() {
		return moment;
	}

	public Map<String, Integer> getNetDrivers() {
		return netDrivers;
	}
	

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		
		headers.put(TYPE_HEADER, TYPE);
		headers.put(MOMENT_HEADER, moment);
		
	}
}
