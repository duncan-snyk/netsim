package uk.co.ukmaker.netsim.amqp.messages.node;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class PropagateOutputsMessage implements Message {
	
	public static final String TYPE = "PO";

	private final long moment;
	private final Set<String> netIds;
	
	public PropagateOutputsMessage(final long moment, final Set<String> netIds) {
		this.moment = moment;
		this.netIds = netIds;
	}
	
	
	public long getMoment() {
		return moment;
	}


	public Set<String> getNetIds() {
		return netIds;
	}



	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
		headers.put(MOMENT_HEADER, moment);
	}


	@Override
	public byte[] getBytes() {
		// netId:netId...
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(String netId : netIds) {
			
			if(!first) {
				sb.append(":");
			} else {
				first = false;
			}
			
			sb.append(netId);
		}
		
		return sb.toString().getBytes();
	}
	
	public static PropagateOutputsMessage read(Map<String, Object> headers, byte[] bytes) {
		Set<String> netIds = new HashSet<String>();
		String[] bits = new String(bytes).split(":");
		for(String bit : bits) {
			netIds.add(bit);
		}
		
		return new PropagateOutputsMessage(Long.parseLong((String)headers.get(MOMENT_HEADER)), netIds);
	}

}
