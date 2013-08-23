package uk.co.ukmaker.netsim.amqp.messages.nodereply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.ukmaker.netsim.amqp.messages.Message;


public class UpdateEventQueueMessage implements Message {
	
	public static final String TYPE = "UEQ";
	
	private final Map<String, Set<Long>> netMoments;
	
	public UpdateEventQueueMessage(final Map<String, Set<Long>> netMoments) {
		this.netMoments = netMoments;
	}
	
	public Map<String, Set<Long>> getNetMoments() {
		return netMoments;
	}

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
	}

	@Override
	public byte[] getBytes() {
		// format is
		// netId{moment,moment...}netId{moment,moment...{
		StringBuffer sb = new StringBuffer();
		for(String netId : netMoments.keySet()) {
			sb.append(netId);
			sb.append("{");
			boolean first=true;
			for(Long moment : netMoments.get(netId)) {
				if(!first) {
					sb.append(",");
				} else {
					first = false;
				}
				sb.append(moment);
			}
			sb.append("}");
		}
		return sb.toString().getBytes();
	}
	
	public static UpdateEventQueueMessage read(Map<String, Object> headers, byte[] bytes) {
		String[] netBits = new String(bytes).split("}");
		Map<String, Set<Long>> netMoments = new HashMap<String, Set<Long>>();
		for(String netBit : netBits) {
			int lbr = netBit.indexOf('{');
			if(lbr > 0) {
    			String netId = netBit.substring(0, lbr-1);
    			String[] moments = netBit.substring(lbr+1).split(",");
    			Set<Long>longmoments = new HashSet<Long>();
    			for(String moment : moments) {
    				longmoments.add(Long.parseLong(moment));
    			}
    			netMoments.put(netId, longmoments);
			}
		}
		
		return new UpdateEventQueueMessage(netMoments);
	}

}
