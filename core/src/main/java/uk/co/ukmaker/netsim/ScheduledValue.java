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
	private long moment;
	
	private SignalValue value;
	
	private ScheduledValue next;
	
	public ScheduledValue() {
		
	}

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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(moment);
		sb.append(":");
		sb.append(value.toString());
		return sb.toString();
	}
	
	public static ScheduledValue fromString(String string) throws Exception {
		String[] bits = string.split(":");
		return new ScheduledValue(Long.parseLong(bits[0]), SignalValue.fromChar(bits[1].charAt(0)));
	}
}
