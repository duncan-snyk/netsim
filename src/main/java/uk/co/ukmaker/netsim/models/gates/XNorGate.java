package uk.co.ukmaker.netsim.models.gates;

import static uk.co.ukmaker.netsim.SignalValue.ONE;
import static uk.co.ukmaker.netsim.SignalValue.X;
import static uk.co.ukmaker.netsim.SignalValue.ZERO;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;
import uk.co.ukmaker.netsim.pins.Output;

public class XNorGate extends Model {
	
	private final InputPin a = new InputPin(this, "a");
	private final InputPin b = new InputPin(this, "b");
	private final OutputPin q = new OutputPin(this, "q");
		
	public XNorGate(String name) {
		
		super(name);

		addPin(a);
		addPin(b);
		addPin(q);
	}
	
	public XNorGate() {
		this("XOR");
	}
	
	@Override
	public void update(long moment) {
		
		// If either input is unknown, so will be the output
		if(a.getInputValue().isUnknown() || b.getInputValue().isUnknown()) {
			if(!q.getOutputValue().isX()) {
				q.scheduleOutputValue(moment+1, X);
			}
			return;
		}
		
		// Real values lead to real answers
		if(a.getInputValue().equals(b.getInputValue())) {
			if(!q.getOutputValue().isOne()) {
				q.scheduleOutputValue(moment+1, ONE);
			}
			return;
		}
		
		if(!q.getOutputValue().isZero()) {
			q.scheduleOutputValue(moment+1, ZERO);
		}
	}
	
	public void propagateOutputEvents(long moment) {
		q.propagateOutputValue(moment);
	}

}
