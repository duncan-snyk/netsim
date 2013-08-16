package uk.co.ukmaker.netsim.ports;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.SignalValue;

public class OutputPort extends AbstractPort implements Output {
	
	private SignalValue outputValue = SignalValue.X;

	private ScheduledValue scheduledValue = new ScheduledValue(0, SignalValue.X);

	public OutputPort(final Component component, final String name) {
		super(component, name);
	}

	@Override
	public SignalValue getOutputValue() {
		return outputValue;
	}
	
	/**
	 * Return the next value change scheduled at or before the moment or null if there is none
	 */
	public SignalValue getScheduledOutputValue(long moment) {
		ScheduledValue v = scheduledValue;
		while(v != null) {
			if(v.getMoment() <= moment) {
				return v.getValue();
			}
			
			v = v.getNext();
		}
		
		return null;
	}
	

	@Override
	public void scheduleOutputValue(long scheduledMoment, SignalValue value) {
		if(scheduledValue == null) {
			scheduledValue = new ScheduledValue(scheduledMoment, value);
		} else {
			ScheduledValue v = scheduledValue;
			ScheduledValue p = null;
			ScheduledValue newValue = new ScheduledValue(scheduledMoment, value);
			
			// Insert the new value into the list at the appropriate place
			// Replace any currently scheduled value if needed
			while(v != null) {
				if(v.getMoment() == scheduledMoment) {
					newValue.setNext(v.getNext());
					if(p == null) {
						scheduledValue = newValue;
					} else {
						p.setNext(newValue);
					}
					break;
				}
				
				if(v.getMoment() < scheduledMoment) {
					p = v;
					v = v.getNext();
					if(v == null) {
						// got to the end, so just append the new value
						p.setNext(newValue);
						break;
					}
				} else {
					// v must be scheduled after the new value, so insert the new value before it and we're done
					if(p == null) {
						newValue.setNext(scheduledValue);
						scheduledValue = newValue;
					} else {
						newValue.setNext(v);
						p.setNext(newValue);
					}
					break;
				}
			}
			
		}
		
	}

	/**
	 * Propagate the latest scheduled value up to the moment, discarding all earlier ones if there are any
	 */
	@Override
	public void propagateOutputValue(long moment) {
		ScheduledValue v = scheduledValue;
		
		while(v != null) {
			if(v.getMoment() > moment) {
				break;
			}
			
			scheduledValue = v.getNext();
			outputValue = v.getValue();
			v = scheduledValue;
		}
	}

}
