package uk.co.ukmaker.netsim.amqp.messages;

import java.util.Map;

public class NodesMessage {
	
	public static enum Type {
		INITIALISE,
		PROPAGATE_OUTPUTS,
		PROPAGATE_INPUTS,
		UPDATE_MODELS
	}
	
	private final Type type;
	
	private final long moment;
	
	private final Map<String, Long> netDrivers;

	public NodesMessage(Type type, long moment, Map<String, Long> netDrivers) {
		super();
		this.type = type;
		this.moment = moment;
		this.netDrivers = netDrivers;
	}

	public Type getType() {
		return type;
	}

	public long getMoment() {
		return moment;
	}

	public Map<String, Long> getNetDrivers() {
		return netDrivers;
	}

}
