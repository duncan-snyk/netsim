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

public class XNorGateTest extends TwoInputGateTest {
	
	@Override
	public void setup() {
		
		gate = new XNorGate();
		
		a = (Input)gate.getPorts().get("a");
		b = (Input)gate.getPorts().get("b");
		q = (Output)gate.getPorts().get("q");
	}
	
	@Test
	public void testLogic() {
		
		expect(X, X);
		
		check(ZERO, ZERO, X, ONE);
		check(ZERO, ONE, ONE, ZERO);
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
