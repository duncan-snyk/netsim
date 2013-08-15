package uk.co.ukmaker.netsim.models.test;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.OutputPin;
import uk.co.ukmaker.netsim.pins.Output;

public class SequenceGenerator extends Model {
	
	private final OutputPin pin = new OutputPin(this, "pin");
	
	private Map<Long, SignalValue> values = new HashMap<Long, SignalValue>();
	
	public SequenceGenerator(String name) {
		super(name);
		addPin(pin);
	}
	
	public SequenceGenerator() {
		this("SEQGEN");
	}
	
	public void addValue(long moment, SignalValue value) {
		values.put(moment, value);
	}

	public SignalValue getValue(long moment) {
		
		if(values.containsKey(moment)) {
			return values.get(moment);
		}
		
		return null;
	}
	

	@Override
	public void update(long moment) {
		
		SignalValue v = getValue(moment+1);
		
		if(v != null) {
			if(!v.equals(pin.getOutputValue())) {
				pin.scheduleOutputValue(moment+1, v);
			}
		}
	}

	@Override
	public void propagateOutputEvents(long moment) {
		pin.propagateOutputValue(moment);
	}

	@Override
	public String getName() {
		return "SequenceGenerator";
	}

}