package uk.co.ukmaker.netsim.amqp.messages.node;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.co.ukmaker.netsim.amqp.messages.Message;

public class PropagateOutputsMessage implements Message {
	
	public static final String TYPE = "PO";

	private long moment;
	private Set<String> netIds;
	
	public PropagateOutputsMessage() {
		
	}
	
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
}
