package uk.co.ukmaker.netsim.models.gates;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;
import uk.co.ukmaker.netsim.pins.Output;

public class NorGate extends Model {
	
	private final InputPin a = new InputPin(this, "a");
	private final InputPin b = new InputPin(this, "b");
	private final OutputPin q = new OutputPin(this, "q");
		
	public NorGate(String name) {
		super(name);
		addPin(a);
		addPin(b);
		addPin( q);
	}
	
	public NorGate() {
		this("AND");
	}
	
	@Override
	public void update(long moment) {
		
		// If either input is unknown, so will be the output
		if(a.getInputValue().isUnknown() || b.getInputValue().isUnknown()) {
			if(!q.getOutputValue().isX()) {
				q.scheduleOutputValue(moment, SignalValue.X);
			}
		} else if(a.getInputValue().isOne() || b.getInputValue().isOne()) {
			// Real values lead to real answers
			if(!q.getOutputValue().isZero()) {
				q.scheduleOutputValue(moment, SignalValue.ZERO);
			}
		} else if(!q.getOutputValue().isOne()) {
			q.scheduleOutputValue(moment, SignalValue.ONE);
		}
	}
	
	public void propagateOutputEvents(long moment) {
		q.propagateOutputValue(moment);
	}
}
