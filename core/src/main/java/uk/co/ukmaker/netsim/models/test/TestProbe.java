package uk.co.ukmaker.netsim.models.test;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.InputPin;

public class TestProbe extends Model {
	
	private InputPin pin = new InputPin(this, "pin");
	
	private Map<Long, SignalValue> expectedValues = new HashMap<Long, SignalValue>();

	private boolean hasErrors = false;

	public TestProbe(String name) {
		super(name);
		addPin(pin);
	}
	
	public TestProbe() {
		this("TP");
	}
	
	public void expect(long moment, SignalValue value) {
		expectedValues.put(moment, value);
	}

	public SignalValue getExpectedValue(long moment) {
		
		if(expectedValues.containsKey(moment)) {
			return expectedValues.get(moment);
		}
		
		return null;
	}

	@Override
	public void update(long moment) {
		
		SignalValue expected = getExpectedValue(moment);
		SignalValue actual = pin.useInputValue(moment);
		
		hasErrors = false;
		
		if(expected != null) {
			
			if(!expected.equals(actual)) {
				hasErrors = true;
				//System.out.println("Test failure at "+moment+" = "+getName()+" expected "+expected+", but got "+actual);
			}
		}
		
	}
	
	public SignalValue getValue() {
		return pin.getInputValue();
	}
	
	public boolean hasErrors() {
		return hasErrors;
	}

}