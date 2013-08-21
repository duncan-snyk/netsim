package uk.co.ukmaker.netsim.models.flipflops;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;

public class TransparentDType extends Model {
	
	private InputPin d = new InputPin(this, "d");
	private InputPin clk = new InputPin(this, "clk");
	private OutputPin q = new OutputPin(this, "q");
	private OutputPin qn = new OutputPin(this, "qn");
	
	private SignalValue lastClk = null;
	
	// simple model
	private long tpd = 10000; // 1 tick is 1ps, so tpd is 10ns

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
		
		if(!needsUpdate(moment)) {
			return;
		}
		
		// If the clock is unknown, so will be the output
		if(clk.getInputValue().isUnknown()) {
			if(!q.getOutputValue().isX() || !qn.getOutputValue().isX()) {
				q.scheduleOutputValue(moment+tpd, SignalValue.X);
				qn.scheduleOutputValue(moment+tpd, SignalValue.X);
			}
		} else {
			if(SignalValue.ZERO.equals(lastClk) && clk.getInputValue().isOne()) {
				// hold the value if it has changed
				if(!d.getInputValue().equals(q.getOutputValue())) {
					q.scheduleOutputValue(moment+tpd, d.getInputValue());
					qn.scheduleOutputValue(moment+tpd, d.getInputValue().not());
				}
			} else if(clk.getInputValue().isZero()) {
				// Output follows d while the clock is low
				if(!d.getInputValue().equals(q.getOutputValue())) {
					q.scheduleOutputValue(moment+tpd, d.getInputValue().isUnknown() ? SignalValue.X : d.getInputValue());
					qn.scheduleOutputValue(moment+tpd, d.getInputValue().isUnknown() ? SignalValue.X : d.getInputValue().not());
				}
			}
		}
		lastClk = clk.getInputValue();
	}

	public String toString() {
		return String.format("DFF[d=%s, clk=%s, q=%s, qn=%s",
				d.getInputValue(), clk.getInputValue(),
				q.getOutputValue(), qn.getOutputValue()
				);
	}
}
