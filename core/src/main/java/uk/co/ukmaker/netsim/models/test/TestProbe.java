package uk.co.ukmaker.netsim.models.test;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.ScheduledValueQueue;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.InputPin;

public class TestProbe extends Model {
	
	private InputPin pin = new InputPin(this, "pin");
	
	private ScheduledValueQueue expectedValues = new ScheduledValueQueue();
	
	private SignalValue expectedValue;

	private boolean hasErrors = false;

	public TestProbe(String name) {
		super(name);
		addPin(pin);
	}
	
	public TestProbe() {
		this("TP");
	}
	
	public void expect(long moment, SignalValue value) {
		expectedValues.schedule(new ScheduledValue(moment, value));
	}

	public SignalValue getExpectedValue() {
		
		return expectedValue;
	}

	@Override
	public void update(long moment) {
		
		ScheduledValue expected = expectedValues.useScheduledValue(moment);
		if(expected != null) {
			expectedValue = expected.getValue();
		} else {
			expectedValue = null;
		}
		
		SignalValue actual = pin.useInputValue(moment);
		
		hasErrors = false;
		
		if(expectedValue != null) {
			
			if(!expectedValue.equals(actual)) {
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
	
	
	@Override
	public Map<String, String> getParameters() {
		Map<String, String> params = super.getParameters();
		params.put("expectedValues", expectedValues.toString(false));		
		return params;
	}

	@Override
	public void setParameters(Map<String, String> params) throws Exception {
		// TODO Auto-generated method stub
		super.setParameters(params);
		if(params.containsKey("expectedValues")) {
			expectedValues = ScheduledValueQueue.fromString(params.get("expectedValues"));
		}
	}

}