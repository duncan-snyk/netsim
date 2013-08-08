package uk.co.ukmaker.netsim.components.gates;

import static uk.co.ukmaker.netsim.SignalValue.ONE;
import static uk.co.ukmaker.netsim.SignalValue.X;
import static uk.co.ukmaker.netsim.SignalValue.ZERO;
import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.InputPort;
import uk.co.ukmaker.netsim.ports.Output;
import uk.co.ukmaker.netsim.ports.OutputPort;

public class XNorGate extends Component {
	
	private final Input a = new InputPort(this, "a");
	private final Input b = new InputPort(this, "b");
	private final Output q = new OutputPort(this, "q");
		
	public XNorGate(String name) {
		
		super(name);

		addPort(a);
		addPort(b);
		addPort(q);
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
