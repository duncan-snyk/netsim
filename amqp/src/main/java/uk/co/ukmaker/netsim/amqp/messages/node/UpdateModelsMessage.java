package uk.co.ukmaker.netsim.amqp.messages.node;

import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class UpdateModelsMessage implements Message {
	
	public static final String TYPE = "UM";
	
	private final long moment;

	public UpdateModelsMessage(long moment) {
		super();
		this.moment = moment;
	}
	
	public long getMoment() {
		return moment;
	}

	@Override
	public byte[] getBytes() {
		return null;
	}

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
		headers.put(MOMENT_HEADER, moment);
	}
	
	public static UpdateModelsMessage read(Map<String, Object> headers, byte[] bytes) {
		return new UpdateModelsMessage(Long.parseLong((String)headers.get(MOMENT_HEADER)));
	}

}
