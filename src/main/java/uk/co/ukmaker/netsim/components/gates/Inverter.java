package uk.co.ukmaker.netsim.components.gates;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.InputPort;
import uk.co.ukmaker.netsim.ports.Output;
import uk.co.ukmaker.netsim.ports.OutputPort;

public class Inverter extends Component {
	
	private final Input input   = new InputPort(this, "a");
	private final Output output = new OutputPort(this, "q");
	
	public Inverter(String name) {
		
		super(name);
 		addPort(input);
		addPort(output);
	}
	
	public Inverter() {
		this("INV");
	}
	
	@Override
	public void update(long moment) {
		
		// An unknown input always gives rise to an unknown output
		if(input.getInputValue().isUnknown()) {
			
			if(!output.getOutputValue().isX()) {
				output.scheduleOutputValue(moment+1, SignalValue.X);
			}
			
			return;
		}
		
		// Input must be a real value, schedule the inverse if needed
		if(input.getInputValue().isNot(output.getOutputValue())) {
			return;
		}
		
		output.scheduleOutputValue(moment+1, input.getInputValue().not());
	}
	
	public void propagateOutputEvents(long moment) {
		output.propagateOutputValue(moment);
	}

}
