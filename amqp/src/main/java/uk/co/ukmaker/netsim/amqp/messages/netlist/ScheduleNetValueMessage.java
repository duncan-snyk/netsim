package uk.co.ukmaker.netsim.amqp.messages.netlist;

import java.util.Map;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.amqp.messages.Message;

public class ScheduleNetValueMessage implements Message {
	
	public static final String TYPE = "SNV";

	private final String netId;
	private final ScheduledValue value;

	public ScheduleNetValueMessage(String netId, ScheduledValue value) {
		super();
		this.netId = netId;
		this.value = value;
	}
	
	public String getNetId() {
		return netId;
	}

	public ScheduledValue getValue() {
		return value;
	}


	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
	}

	@Override
	public byte[] getBytes() {
		// netId:moment:value
		StringBuffer sb = new StringBuffer();
		sb.append(netId);
		sb.append(":");
		sb.append(value.getMoment());
		sb.append(":");
		sb.append(value.getValue());
		return sb.toString().getBytes();
	}
	
	public static ScheduleNetValueMessage read(Map<String, Object> headers, byte[] bytes) {
		
		String[] bits = new String(bytes).split(":");
		
		return new ScheduleNetValueMessage(
				bits[0], new ScheduledValue(Long.parseLong(bits[1]), SignalValue.valueOf(bits[2]))
				);
	}

}
