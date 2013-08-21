package uk.co.ukmaker.netsim.models.flipflops;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;

public class TransparentDTypeClrn extends Model {
	
	private InputPin d = new InputPin(this, "d");
	private InputPin clk = new InputPin(this, "clk");
	private InputPin clrn = new InputPin(this, "clrn");
	private OutputPin q = new OutputPin(this, "q");
	private OutputPin qn = new OutputPin(this, "qn");
	
	private SignalValue lastClk = null;
	
	private long tpd = 10000;

	public TransparentDTypeClrn(String name) {
		super(name);
		addPin(d);
		addPin(clk);
		addPin(clrn);
		addPin(q);
		addPin(qn);
	}
	
	public TransparentDTypeClrn() {
		this("DTypeClrn");
	}

	@Override
	public void update(long moment) {
		
		if(!needsUpdate(moment)) {
			return;
		}
		
		// If the clock or clrn is unknown, so will be the output
		if(clk.getInputValue().isUnknown() || clrn.getInputValue().isUnknown()) {
			if(!q.getOutputValue().isX() || !qn.getOutputValue().isX()) {
				q.scheduleOutputValue(moment+tpd, SignalValue.X);
				qn.scheduleOutputValue(moment+tpd, SignalValue.X);
			}
		} else if(clrn.getInputValue().isZero()) {
			if(q.getOutputValue().isNot(SignalValue.ZERO)) {
				q.scheduleOutputValue(moment+tpd, SignalValue.ZERO);
				qn.scheduleOutputValue(moment+tpd, SignalValue.ONE);
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
