package uk.co.ukmaker.netsim.pins;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.ScheduledValueQueue;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;

public class InputOutputPin extends OutputPin implements Input {
	
	private ScheduledValueQueue queue = new ScheduledValueQueue();
	
	private SignalValue currentValue = SignalValue.X;


	public InputOutputPin(final Model model, final String name) {
		super(model, name);
	}
	
	
	@Override
	public SignalValue getInputValue() {
		return currentValue;
	}

	@Override
	public SignalValue useInputValue(long moment) {
		currentValue = queue.useScheduledValue(moment).getValue();
		
		return currentValue;
	}


	@Override
	public void scheduleInputValue(long moment, SignalValue value) {
		queue.schedule(new ScheduledValue(moment, value));
	}
	
	@Override
	public boolean hasScheduledValue(long moment) {
		return queue.getScheduledDrivers(moment) > 0;
	}
	
	@Override
	public boolean hasScheduledValue(long moment, int drivers) {
		return queue.getScheduledDrivers(moment) == drivers;
	}
	
	public void await(long moment, int drivers) {
		while(!hasScheduledValue(moment, drivers));
	}
}
