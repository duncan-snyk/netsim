package uk.co.ukmaker.netsim.models.test;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.OutputPin;

public class ClockGenerator extends Model {
	
	long period;
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

	@Override
	public Map<String, String> getParameters() {
		Map<String, String> params = super.getParameters();
		params.put("period", Long.toString(period));
		params.put("startHigh", Boolean.toString(high));
		
		return params;
	}

	@Override
	public void setParameters(Map<String,String> params) throws Exception {
		// TODO Auto-generated method stub
		super.setParameters(params);
		if(params.containsKey("period")) {
			period = Long.parseLong(params.get("period"));
		}
		if(params.containsKey("startHigh")) {
			high = Boolean.parseBoolean(params.get("startHigh"));
		}
	}

}
