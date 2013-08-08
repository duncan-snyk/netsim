package uk.co.ukmaker.netsim.components.gates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.co.ukmaker.netsim.SignalValue.ONE;
import static uk.co.ukmaker.netsim.SignalValue.X;
import static uk.co.ukmaker.netsim.SignalValue.Z;
import static uk.co.ukmaker.netsim.SignalValue.ZERO;

import org.junit.Before;
import org.junit.Test;

import uk.co.ukmaker.netsim.components.gates.XorGate;
import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.Output;

public class XorGateTest extends TwoInputGateTest {
	
	
	@Override
	public void setup() {
		
		gate = new XorGate();
		
		a = (Input)gate.getPorts().get("a");
		b = (Input)gate.getPorts().get("b");
		q = (Output)gate.getPorts().get("q");
	}
	
	@Test
	public void testLogic() {
		
		expect(X, X);
		
		check(ZERO, ZERO, X, ZERO);
		check(ZERO, ONE, ZERO, ONE);
		check(ONE, ZERO, ONE, null);
		check(ONE, ONE, ONE, ZERO);

		check(ONE, Z, ZERO, X);
		check(ONE, ONE, X, ZERO);
		check(ZERO, Z, ZERO, X);
		check(ONE, ONE, X, ZERO);
		check(X, Z, ZERO, X);
		check(ONE, ONE, X, ZERO);
		check(Z, Z, ZERO, X);
		check(ONE, ONE, X, ZERO);

		check(Z, ONE, ZERO, X);
		check(ONE, ONE, X, ZERO);
		
		check(Z, ZERO, ZERO, X);
		check(ONE, ONE, X, ZERO);
		
		check(Z, X, ZERO, X);
		check(ONE, ONE, X, ZERO);
		
		check(X, X, ZERO, X);
		check(ONE, ONE, X, ZERO);

	}
}
