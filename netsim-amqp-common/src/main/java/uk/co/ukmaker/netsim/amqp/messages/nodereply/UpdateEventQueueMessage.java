package uk.co.ukmaker.netsim.amqp.messages.nodereply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.ukmaker.netsim.amqp.messages.NetsimMessage;


public class UpdateEventQueueMessage implements NetsimMessage {
	
	public static final String TYPE = "UEQ";
	
	private Map<String, Set<Long>> netMoments;
	
	public UpdateEventQueueMessage() {
		
	}
	
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
}
