package uk.co.ukmaker.netsim.models.gates;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;
import uk.co.ukmaker.netsim.pins.Output;

public class Inverter extends Model {
	
	private final InputPin input   = new InputPin(this, "a");
	private final OutputPin output = new OutputPin(this, "q");
	
	public Inverter(String name) {
		
		super(name);
 		addPin(input);
		addPin(output);
	}
	
	public Inverter() {
		this("INV");
	}
	
	@Override
	public void update(long moment) {
		
		// An unknown input always gives rise to an unknown output
		if(input.getInputValue().isUnknown()) {
			
			if(!output.getOutputValue().isX()) {
				output.scheduleOutputValue(moment, SignalValue.X);
			}
			
			return;
		}
		
		// Input must be a real value, schedule the inverse if needed
		if(input.getInputValue().isNot(output.getOutputValue())) {
			return;
		}
		
		output.scheduleOutputValue(moment, input.getInputValue().not());
	}
	
	public void propagateOutputEvents(long moment) {
		output.propagateOutputValue(moment);
	}

}
