package uk.co.ukmaker.netsim.pins;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.ScheduledValueQueue;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;

public class InputPin extends Pin implements Input {
	
	private ScheduledValueQueue queue = new ScheduledValueQueue();

	private SignalValue currentValue = SignalValue.X;
	
	public InputPin(final Model model, final String name) {
		super(model, name);
	}

	@Override
	public SignalValue getInputValue() {
		return currentValue;
	}

	@Override
	public SignalValue useInputValue(long moment) {
		ScheduledValue sv = queue.useScheduledValue(moment);
		
		if(sv != null) {
			currentValue = sv.getValue();
		}
		
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
		while(!hasScheduledValue(moment, drivers)) { try {
			Thread.sleep(0, 100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} }
	}
}
