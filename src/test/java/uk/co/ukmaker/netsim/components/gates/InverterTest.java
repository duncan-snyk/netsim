package uk.co.ukmaker.netsim.components.gates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.co.ukmaker.netsim.SignalValue.ONE;
import static uk.co.ukmaker.netsim.SignalValue.X;
import static uk.co.ukmaker.netsim.SignalValue.Z;
import static uk.co.ukmaker.netsim.SignalValue.ZERO;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.components.ComponentTest;
import uk.co.ukmaker.netsim.components.gates.Inverter;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.Output;

public class InverterTest extends ComponentTest {
	
	Inverter gate;
	Input a;
	Output q;
	
	@Override
	public Component getComponent() {
		return gate;
	}

	@Override
	public List<Input> getInputs() {
		return Lists.newArrayList(a);
	}

	@Override
	public List<Output> getOutputs() {
		return Lists.newArrayList(q);
	}

	@Override
	public void setup() {
		
		gate = new Inverter();
		
		a = (Input)gate.getPorts().get("a");
		q = (Output)gate.getPorts().get("q");
	}
	
	@Test
	public void testLogic() {
		
		expect(X, X);
		
		apply(X);
		expect(X, null);
		apply(ZERO);
		expect(X, ONE);
		apply(ONE);
		expect(ONE, ZERO);
		apply(Z);
		expect(ZERO, X);
		apply(Z);
		expect(X, null);
	}
}
