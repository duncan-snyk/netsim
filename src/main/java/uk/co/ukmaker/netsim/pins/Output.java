package uk.co.ukmaker.netsim.pins;

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
	
	public void scheduleOutputValue(long scheduledMoment, SignalValue value);
	
	/**
	 * Update the current value if needed
	 */
	public void propagateOutputValue(long moment);

}
