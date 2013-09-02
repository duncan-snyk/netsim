package uk.co.ukmaker.netsim.amqp.messages.node;

import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class UpdateModelsMessage implements Message {
	
	public static final String TYPE = "UM";
	
	private long moment;
	
	public UpdateModelsMessage() {
		
	}

	public UpdateModelsMessage(long moment) {
		super();
		this.moment = moment;
	}
	
	public long getMoment() {
		return moment;
	}

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
		headers.put(MOMENT_HEADER, moment);
	}
}
