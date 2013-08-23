package uk.co.ukmaker.netsim.amqp.messages.node;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class PropagateInputsMessage implements Message {
	
	public static final String TYPE="PI";
	
	private final long moment;
	private final Map<String, Integer> netDrivers;
	
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

	@Override
	public byte[] getBytes() {
		// Format is
		// netId:drivers,netId:drivers...
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for(String netId : netDrivers.keySet()) {
			
			if(!first) {
				sb.append(",");
			} else {
				first = false;
			}
			
			sb.append(netId);
			sb.append(":");
			sb.append(netDrivers.get(netId));
		}
		
		return sb.toString().getBytes();
	}
	
	public static PropagateInputsMessage read(Map<String, Object> headers, byte[] bytes) {
		Map<String, Integer> netDrivers = new HashMap<String, Integer>();
		String[] pairs = new String(bytes).split(",");
		for(String pair : pairs) {
			String[] bits = pair.split(":");
			netDrivers.put(bits[0],  Integer.parseInt(bits[1]));
		}
		
		return new PropagateInputsMessage(Long.parseLong((String)headers.get(MOMENT_HEADER)), netDrivers);
	}

}
