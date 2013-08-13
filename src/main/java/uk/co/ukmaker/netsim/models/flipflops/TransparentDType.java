package uk.co.ukmaker.netsim.models.flipflops;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;
import uk.co.ukmaker.netsim.pins.Output;

public class TransparentDType extends Model {
	
	private InputPin d = new InputPin(this, "d");
	private InputPin clk = new InputPin(this, "clk");
	private OutputPin q = new OutputPin(this, "q");
	private OutputPin qn = new OutputPin(this, "qn");
	
	private SignalValue lastClk = null;

	public TransparentDType(String name) {
		super(name);
		addPin(d);
		addPin(clk);
		addPin(q);
		addPin(qn);
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
