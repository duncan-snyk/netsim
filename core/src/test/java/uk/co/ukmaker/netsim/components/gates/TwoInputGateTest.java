package uk.co.ukmaker.netsim.components.gates;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.co.ukmaker.netsim.SignalValue.ONE;
import static uk.co.ukmaker.netsim.SignalValue.X;
import static uk.co.ukmaker.netsim.SignalValue.Z;
import static uk.co.ukmaker.netsim.SignalValue.ZERO;

import java.util.List;

import org.junit.Test;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.components.ComponentTest;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.Output;
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
