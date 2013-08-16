package uk.co.ukmaker.netsim.ports;

import uk.co.ukmaker.netsim.SignalValue;

/**
 * Implementations may include propagation delay between setValue and getValue
 * @author duncan
 *
 */
public interface Input extends Port {
	
	/**
	 * Returns the current value on the input
	 */
	public SignalValue getInputValue();
	
	/**
	 * Set a new value on the input.
	 */
	public void setInputValue(long moment, SignalValue value);

}
