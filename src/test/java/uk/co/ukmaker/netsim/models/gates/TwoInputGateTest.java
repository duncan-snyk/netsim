package uk.co.ukmaker.netsim.models.gates;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.ComponentTest;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;

abstract public class TwoInputGateTest extends ComponentTest {
	
	protected Model gate;
	protected InputPin a;
	protected InputPin b;
	protected OutputPin q;
	
	@Override
	public Model getComponent() {
		return gate;
	}

	@Override
	public List<InputPin> getInputs() {
		return newArrayList(a, b);
	}

	@Override
	public List<OutputPin> getOutputs() {
		return newArrayList(q);
	}
	
	public void check(SignalValue a, SignalValue b, SignalValue q, SignalValue qs) {
		apply(a, b);
		expect(q, qs);
	}
}
