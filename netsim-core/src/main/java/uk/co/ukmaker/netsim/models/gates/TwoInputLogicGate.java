package uk.co.ukmaker.netsim.models.gates;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;

abstract public class TwoInputLogicGate extends Model {

	
	private final InputPin a = new InputPin(this, "a");
	private final InputPin b = new InputPin(this, "b");
	private final OutputPin q = new OutputPin(this, "q");
	
	private long tpd = 10000;
		
	public TwoInputLogicGate(String name) {
		super(name);
		addPin(a);
		addPin(b);
		addPin( q);
	}
	
	abstract public boolean fn(SignalValue av, SignalValue bv);
	
	
	@Override
	public void update(long moment) {
		
		if(!needsUpdate(moment)) {
			return;
		}
		
		SignalValue av = a.getInputValue();
		SignalValue bv = b.getInputValue();
		
		// If either input is unknown, so will be the output
		if(av.isUnknown() || bv.isUnknown()) {
			if(!q.getOutputValue().isX()) {
				q.scheduleOutputValue(moment+tpd, SignalValue.X);
			}
		} else if(fn(av, bv)) {
			// Real values lead to real answers
			if(!q.getOutputValue().isOne()) {
				q.scheduleOutputValue(moment+tpd, SignalValue.ONE);
			}
		} else if(!q.getOutputValue().isZero()) {
			q.scheduleOutputValue(moment+tpd, SignalValue.ZERO);
		}
	}
}
