package uk.co.ukmaker.netsim.simulation;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.OutputPin;
import uk.co.ukmaker.netsim.pins.Output;

public class ClockGenerator extends Model {
	
	final long period;
	boolean high;
	
	private OutputPin q = new OutputPin(this, "q");
	
	public ClockGenerator(String name, final long period, final boolean startHigh) {
		
		super(name);
		
		this.period = period;
		this.high = startHigh;
		addPin(q);
	}

	public ClockGenerator(final long period, final boolean startHigh) {
		this("CLKGEN", period, startHigh);
	}

	@Override
	public void update(long moment) {
		
		propagateOutputEvents(moment);
		
		if((moment % period) == 0) {
			high = !high;
		}
		
		if(high && !q.getOutputValue().isOne()) {
			q.scheduleOutputValue(moment+1, SignalValue.ONE);
		} else if(!high && !q.getOutputValue().isZero()) {
			q.scheduleOutputValue(moment+1, SignalValue.ZERO);
		} else if(!q.getOutputValue().isX()) {
			q.scheduleOutputValue(moment+1, SignalValue.X);
		}
		
	}

	@Override
	public void propagateOutputEvents(long moment) {
		q.propagateOutputValue(moment);
	}

	@Override
	public String getName() {
		return "ClockGenerator";
	}

}
