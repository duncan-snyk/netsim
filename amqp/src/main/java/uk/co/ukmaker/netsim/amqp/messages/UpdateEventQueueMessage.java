package uk.co.ukmaker.netsim.amqp.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpdateEventQueueMessage implements Message {
	
	private final Map<String, List<Long>> netMoments;
	
	public UpdateEventQueueMessage(final Map<String, List<Long>> netMoments) {
		this.netMoments = netMoments;
	}
	
	public Map<String, List<Long>> getNetMoments() {
		return netMoments;
	}

	@Override
	public String getType() {
		return "UEQ";
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
	
	public static UpdateEventQueueMessage readBytes(byte[] bytes) {
		String[] netBits = new String(bytes).split("}");
		Map<String, List<Long>> netMoments = new HashMap<String, List<Long>>();
		for(String netBit : netBits) {
			int lbr = netBit.indexOf('{');
			String netId = netBit.substring(0, lbr-1);
			String[] moments = netBit.substring(lbr+1).split(",");
			List<Long>longmoments = new ArrayList<Long>();
			for(String moment : moments) {
				longmoments.add(Long.parseLong(moment));
			}
			netMoments.put(netId, longmoments);
		}
		
		return new UpdateEventQueueMessage(netMoments);
	}

}
