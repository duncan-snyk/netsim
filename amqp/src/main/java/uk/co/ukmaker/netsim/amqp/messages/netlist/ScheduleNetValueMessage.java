package uk.co.ukmaker.netsim.amqp.messages.netlist;

import java.util.Map;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.amqp.messages.Message;

public class ScheduleNetValueMessage implements Message {
	
	public static final String TYPE = "SNV";

	private String netId;
	private ScheduledValue value;
	
	public ScheduleNetValueMessage() {
		
	}

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
}
