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

import uk.co.ukmaker.netsim.components.ComponentTest;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.models.gates.Inverter;
import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.Output;
import uk.co.ukmaker.netsim.pins.OutputPin;

public class InverterTest extends ComponentTest {
	
	Inverter gate;
	InputPin a;
	OutputPin q;
	
	@Override
	public Model getComponent() {
		return gate;
	}

	@Override
	public List<InputPin> getInputs() {
		return Lists.newArrayList(a);
	}

	@Override
	public List<OutputPin> getOutputs() {
		return Lists.newArrayList(q);
	}

	@Override
	public void setup() {
		
		gate = new Inverter();
		
		a = (InputPin)gate.getPins().get("a");
		q = (OutputPin)gate.getPins().get("q");
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
