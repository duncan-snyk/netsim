package uk.co.ukmaker.netsim.components.gates;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.InputPort;
import uk.co.ukmaker.netsim.ports.Output;
import uk.co.ukmaker.netsim.ports.OutputPort;

public class AndGate extends Component {
	
	private final Input a = new InputPort(this, "a");
	private final Input b = new InputPort(this, "b");
	private final Output q = new OutputPort(this, "q");
		
	public AndGate(String name) {
		super(name);
		addPort(a);
		addPort(b);
		addPort( q);
	}
	
	public AndGate() {
		this("AND");
	}
	
	@Override
	public void update(long moment) {
		
		// If either input is unknown, so will be the output
		if(a.getInputValue().isUnknown() || b.getInputValue().isUnknown()) {
			if(!q.getOutputValue().isX()) {
				q.scheduleOutputValue(moment+1, SignalValue.X);
			}
		} else if(a.getInputValue().isOne() && b.getInputValue().isOne()) {
			// Real values lead to real answers
			if(!q.getOutputValue().isOne()) {
				q.scheduleOutputValue(moment+1, SignalValue.ONE);
			}
		} else if(!q.getOutputValue().isZero()) {
			q.scheduleOutputValue(moment+1, SignalValue.ZERO);
		}
	}
	
	public void propagateOutputEvents(long moment) {
		q.propagateOutputValue(moment);
	}
	
	public String toString() {
		return String.format("AND %s [a=%s, b=%s, q=%s]", 
				name, a.getInputValue(),b.getInputValue(),q.getOutputValue());
	}
}
