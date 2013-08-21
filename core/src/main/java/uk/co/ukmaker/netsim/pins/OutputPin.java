package uk.co.ukmaker.netsim.pins;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;

public class OutputPin extends Pin implements Output {
	
	private SignalValue outputValue = SignalValue.X;

	private ScheduledValue scheduledValue = null;
	
	private ScheduledValue mostRecentValue = null;

	public OutputPin(final Model model, final String name) {
		super(model, name);
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
	
	/**
	 * Return the moment at which the next change is scheduled, or null
	 */
	public Long getNextScheduleMoment() {
		ScheduledValue v = scheduledValue;
		while(v != null) {
			if(!v.getValue().isNoChange()) {
				return v.getMoment();
			}
			
			v = v.getNext();
		}
		
		return null;
	}
	

	@Override
	public void scheduleOutputValue(long scheduledMoment, SignalValue value) {
		
		mostRecentValue = new ScheduledValue(scheduledMoment, value);

		if(scheduledValue == null) {
			scheduledValue = mostRecentValue;
		} else {
			ScheduledValue v = scheduledValue;
			ScheduledValue p = null;
			
			// Insert the new value into the list at the appropriate place
			// Replace any currently scheduled value if needed
			while(v != null) {
				if(v.getMoment() == scheduledMoment) {
					mostRecentValue.setNext(v.getNext());
					if(p == null) {
						scheduledValue = mostRecentValue;
					} else {
						p.setNext(mostRecentValue);
					}
					break;
				}
				
				if(v.getMoment() < scheduledMoment) {
					p = v;
					v = v.getNext();
					if(v == null) {
						// got to the end, so just append the new value
						p.setNext(mostRecentValue);
						break;
					}
				} else {
					// v must be scheduled after the new value, so insert the new value before it and we're done
					if(p == null) {
						mostRecentValue.setNext(scheduledValue);
						scheduledValue = mostRecentValue;
					} else {
						mostRecentValue.setNext(v);
						p.setNext(mostRecentValue);
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
	public ScheduledValue useScheduledOutputValue(long moment) {
		ScheduledValue v = scheduledValue;
		
		while(v != null) {
			if(v.getMoment() >= moment) {
				break;
			}
			
			scheduledValue = v.getNext();
			outputValue = v.getValue();
			v = scheduledValue;
		}
		
		// v is the most recent value at or before moment
		if(v == null) {
			return null;
		}
		
		if(v.getMoment() == moment) {
			outputValue = v.getValue();
			scheduledValue = v.getNext();
		
			return v;
		}
		
		return null;
	}
	
	public ScheduledValue useMostRecentValue() {
		ScheduledValue v = mostRecentValue;
		mostRecentValue = null;
		return v;
	}

}
