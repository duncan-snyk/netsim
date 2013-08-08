package uk.co.ukmaker.netsim.components.flipflops;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.InputPort;
import uk.co.ukmaker.netsim.ports.Output;
import uk.co.ukmaker.netsim.ports.OutputPort;

public class TransparentDType extends Component {
	
	private Input d = new InputPort(this, "d");
	private Input clk = new InputPort(this, "clk");
	private Output q = new OutputPort(this, "q");
	private Output qn = new OutputPort(this, "qn");
	
	private SignalValue lastClk = null;

	public TransparentDType(String name) {
		super(name);
		addPort(d);
		addPort(clk);
		addPort(q);
		addPort(qn);
	}
	
	public TransparentDType() {
		this("DType");
	}

	@Override
	public void update(long moment) {
		
		// If the clock is unknown, so will be the output
		if(clk.getInputValue().isUnknown()) {
			if(!q.getOutputValue().isX() || !qn.getOutputValue().isX()) {
				q.scheduleOutputValue(moment+1, SignalValue.X);
				qn.scheduleOutputValue(moment+1, SignalValue.X);
			}
		} else {
			if(SignalValue.ZERO.equals(lastClk) && clk.getInputValue().isOne()) {
				// hold the value if it has changed
				if(!d.getInputValue().equals(q.getOutputValue())) {
					q.scheduleOutputValue(moment+1, d.getInputValue());
					qn.scheduleOutputValue(moment+1, d.getInputValue().not());
				}
			} else if(clk.getInputValue().isZero()) {
				// Output follows d while the clock is low
				if(!d.getInputValue().equals(q.getOutputValue())) {
					q.scheduleOutputValue(moment+1, d.getInputValue().isUnknown() ? SignalValue.X : d.getInputValue());
					qn.scheduleOutputValue(moment+1, d.getInputValue().isUnknown() ? SignalValue.X : d.getInputValue().not());
				}
			}
		}
		lastClk = clk.getInputValue();
	}

	@Override
	public void propagateOutputEvents(long moment) {
		q.propagateOutputValue(moment);
		qn.propagateOutputValue(moment);
	}
	
	public String toString() {
		return String.format("DFF[d=%s, clk=%s, q=%s, qn=%s",
				d.getInputValue(), clk.getInputValue(),
				q.getOutputValue(), qn.getOutputValue()
				);
	}

}