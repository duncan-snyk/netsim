package uk.co.ukmaker.netsim.pins;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.SignalValue;

public interface Output {
	
	/**
	 * Return the current value of the output
	 * @return
	 */
	public SignalValue getOutputValue();
	
	/**
	 * Return the next value change scheduled at or before the moment or null if there is none
	 */
	public SignalValue getScheduledOutputValue(long moment);
	
	/**
	 * Return the moment at which the next change is scheduled if there is one, or null if no change is scheduled
	 */
	public Long getNextScheduleMoment();
	
	public void scheduleOutputValue(long scheduledMoment, SignalValue value);
	
	/**
	 * Remove the signal value from the output and return it
	 */
	public ScheduledValue useScheduledOutputValue(long moment);

}
