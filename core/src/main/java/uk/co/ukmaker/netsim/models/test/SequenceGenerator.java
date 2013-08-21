package uk.co.ukmaker.netsim.models.test;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.ScheduledValueQueue;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.OutputPin;

public class SequenceGenerator extends Model {
	
	private final OutputPin pin = new OutputPin(this, "pin");
	
	private ScheduledValueQueue values = new ScheduledValueQueue();
	
	private ScheduledValue last;
	
	public SequenceGenerator(String name) {
		super(name);
		addPin(pin);
	}
	
	public SequenceGenerator() {
		this("SEQGEN");
	}
	
	public void addValue(long moment, SignalValue value) {
		values.schedule(new ScheduledValue(moment, value));
	}


	@Override
	public void update(long moment) {
		
		ScheduledValue next;
		
		if(last == null) {
			// If we haven't scheduled a value yet, schedule the first one from the list
			// so the simulation can start properly
			next = values.useHead();
		} else {
			
			if(last.getMoment() > moment) {
				return;
			}
			
			while((next = values.useHead()) != null) {
				if(!next.getValue().equals(last.getValue())) {
					break;
				}
				// dump things with the same value
			}
		}
		
		
		if(next != null) {
			pin.scheduleOutputValue(next.getMoment(), next.getValue());
			last = next;
		}
	}

	@Override
	public String getName() {
		return "SequenceGenerator";
	}

}
