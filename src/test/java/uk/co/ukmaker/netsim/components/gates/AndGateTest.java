package uk.co.ukmaker.netsim.components.gates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.co.ukmaker.netsim.SignalValue.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Lists.*;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.components.gates.AndGate;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.Output;

public class AndGateTest extends TwoInputGateTest {
	
	@Override
	public void setup() {
		
		gate = new AndGate();
		
		a = (Input)gate.getPorts().get("a");
		b = (Input)gate.getPorts().get("b");
		q = (Output)gate.getPorts().get("q");
	}
	
	@Test
	public void testLogic() {
		
		expect(X, X);
		
		check(ZERO, ZERO, X, ZERO);
		check(ZERO, ONE, ZERO, null);
		check(ONE, ZERO, ZERO, null);
		check(ONE, ONE, ZERO, ONE);

		check(ONE, Z, ONE, X);
		check(ONE, ONE, X, ONE);
		check(ZERO, Z, ONE, X);
		check(ONE, ONE, X, ONE);
		check(X, Z, ONE, X);
		check(ONE, ONE, X, ONE);
		check(Z, Z, ONE, X);
		check(ONE, ONE, X, ONE);

		check(Z, ONE, ONE, X);
		check(ONE, ONE, X, ONE);
		
		check(Z, ZERO, ONE, X);
		check(ONE, ONE, X, ONE);
		
		check(Z, X, ONE, X);
		check(ONE, ONE, X, ONE);
		
		check(X, X, ONE, X);
		check(ONE, ONE, X, ONE);

	}

}
