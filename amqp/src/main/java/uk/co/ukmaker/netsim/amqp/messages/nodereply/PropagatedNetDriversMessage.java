package uk.co.ukmaker.netsim.amqp.messages.nodereply;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class PropagatedNetDriversMessage implements Message {
	
	public static final String TYPE = "PND";
	
	private final Map<String, Integer> netDrivers;

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
	
	public static PropagatedNetDriversMessage read(Map<String, Object> headers, byte[] bytes) {
		Map<String, Integer> netIds = new HashMap<String, Integer>();
		String[] pairs = new String(bytes).split(",");
		for(String pair : pairs) {
			String[] bits = pair.split(":");
			netIds.put(bits[0],  Integer.parseInt(bits[1]));
		}
		
		return new PropagatedNetDriversMessage(netIds);
	}

}
