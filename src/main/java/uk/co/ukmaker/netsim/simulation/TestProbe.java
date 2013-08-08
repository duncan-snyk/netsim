package uk.co.ukmaker.netsim.simulation;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.InputPort;

public class TestProbe extends Component {
	
	private Input pin = new InputPort(this, "pin");
	
	private Map<Long, SignalValue> expectedValues = new HashMap<Long, SignalValue>();


	public TestProbe(String name) {
		super(name);
		addPort(pin);
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
		
		if(expected != null) {
			SignalValue actual = pin.getInputValue();
			
			if(!expected.equals(actual)) {
				System.out.println("Test failure at "+moment+" = "+getName()+" expected "+expected+", but got "+actual);
			}
		}
		
	}

	@Override
	public void propagateOutputEvents(long moment) {
		
		// Nothing to do. A probe has no output (yet!)
		
	}
	
	public SignalValue getValue() {
		return pin.getInputValue();
	}

}