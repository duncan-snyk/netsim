package uk.co.ukmaker.netsim.pins;

import uk.co.ukmaker.netsim.SignalValue;

/**
 * Implementations may include propagation delay between setValue and getValue
 * @author duncan
 *
 */
public interface Input {
	
	/**
	 * Returns the current value on the input
	 */
	public SignalValue getInputValue();
	
	/**
	 * Returns the current value on the input
	 */
	public SignalValue useInputValue(long moment);
	
	/**
	 * Schedule a new value on the input.
	 */
	public void scheduleInputValue(long moment, SignalValue value);
	
	public boolean hasScheduledValue(long moment);
	public boolean hasScheduledValue(long moment, int drivers);

	public void await(long moment, int drivers);

}
