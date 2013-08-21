package uk.co.ukmaker.netsim.models.test;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.OutputPin;

public class ClockGenerator extends Model {
	
	final long period;
	boolean high;
	
	private OutputPin pin = new OutputPin(this, "pin");
	
	public ClockGenerator(String name, final long period, final boolean startHigh) {
		
		super(name);
		
		this.period = period;
		this.high = startHigh;
		addPin(pin);
	}

	public ClockGenerator(final long period, final boolean startHigh) {
		this("CLKGEN", period, startHigh);
	}

	@Override
	public void update(long moment) {

		if((moment % period) == 0) {
			high = !high;
    		if(high && !pin.getOutputValue().isOne()) {
    			pin.scheduleOutputValue(moment+period, SignalValue.ONE);
    		} else if(!high && !pin.getOutputValue().isZero()) {
    			pin.scheduleOutputValue(moment+period, SignalValue.ZERO);
    		} else if(!pin.getOutputValue().isX()) {
    			pin.scheduleOutputValue(moment+period, SignalValue.X);
    		}
		}
	}

	@Override
	public String getName() {
		return "ClockGenerator";
	}

}
