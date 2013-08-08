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

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.components.ComponentTest;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.Output;

abstract public class TwoInputGateTest extends ComponentTest {
	
	protected Component gate;
	protected Input a;
	protected Input b;
	protected Output q;
	
	@Override
	public Component getComponent() {
		return gate;
	}

	@Override
	public List<Input> getInputs() {
		return newArrayList(a, b);
	}

	@Override
	public List<Output> getOutputs() {
		return newArrayList(q);
	}
	
	public void check(SignalValue a, SignalValue b, SignalValue q, SignalValue qs) {
		apply(a, b);
		expect(q, qs);
	}
}
