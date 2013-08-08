package uk.co.ukmaker.netsim;
/**
 * A Change represents a change in signal value on a network
 * 
 * For the moment we assume zero propagation delay along the wires in a network
 * 
 * @author duncan
 *
 */
public class Change {
	
	private final long netId;
	private final SignalValue value;
	
	public Change(long netId, SignalValue value) {
		super();
		this.netId = netId;
		this.value = value;
	}
	
	public long getNetId() {
		return netId;
	}
	
	public SignalValue getValue() {
		return value;
	}

}
