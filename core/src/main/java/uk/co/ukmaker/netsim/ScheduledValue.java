package uk.co.ukmaker.netsim;

/**
 * Used by e.g. OutputPorts to build a list of scheduled changes
 * @author duncan
 *
 */
public class ScheduledValue {
	
	/**
	 * The moment at which the value is scheduled to take effect
	 */
	private final long moment;
	
	private final SignalValue value;
	
	private ScheduledValue next;

	public ScheduledValue(long moment, SignalValue value) {
		super();
		this.moment = moment;
		this.value = value;
	}

	public ScheduledValue getNext() {
		return next;
	}

	public void setNext(ScheduledValue next) {
		this.next = next;
	}

	public long getMoment() {
		return moment;
	}

	public SignalValue getValue() {
		return value;
	}	
}
