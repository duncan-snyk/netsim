package uk.co.ukmaker.netsim.components;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.Output;

abstract public class ComponentTest {
	
	protected long t = 0;
	
	protected Component component;
	protected List<Input> inputs;
	protected List<Output> outputs;
	
	public abstract Component getComponent();
	
	public abstract List<Input> getInputs();
	public abstract List<Output> getOutputs();
	
	@Before
	public void prepare() {
		setup();
		component = getComponent();
		inputs = getInputs();
		outputs = getOutputs();
	}
	
	public abstract void setup();
	
	public void apply(SignalValue... values) {
		
		int i=0;
		
		component.propagateOutputEvents(t);
		
		for(SignalValue v : values) {
			inputs.get(i++).setInputValue(t, v);
		}
		
		component.update(t);
		t++;
	}
	
	/**
	 * SignalValues are supplied in pairs, currentValue and scheduled value
	 * @param values
	 */
	public void expect(SignalValue... values) {
		
		if(values.length != outputs.size() * 2) {
			throw new RuntimeException("Incorrect number of expected values");
		}
		
		for(int i=0; i<outputs.size(); i++) {
			
			Output p = outputs.get(i);
			
			SignalValue c = values[i*2];
			SignalValue s = values[(i*2) + 1];
			
			if(c == null) {
				assertNull(p.getName(), p.getOutputValue());
			} else {
				assertEquals(p.getName(), c, p.getOutputValue());
			}
			
			if(s == null) {
				assertNull(p.getName(), p.getScheduledOutputValue(t));
			} else {
				assertEquals(p.getName(), s, p.getScheduledOutputValue(t));
			}
		}
	}
}
