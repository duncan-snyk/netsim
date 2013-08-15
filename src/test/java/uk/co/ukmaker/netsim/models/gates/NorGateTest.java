package uk.co.ukmaker.netsim.models.gates;

import static uk.co.ukmaker.netsim.SignalValue.ONE;
import static uk.co.ukmaker.netsim.SignalValue.X;
import static uk.co.ukmaker.netsim.SignalValue.Z;
import static uk.co.ukmaker.netsim.SignalValue.ZERO;

import org.junit.Test;

import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;

public class NorGateTest extends TwoInputGateTest {
	
	@Override
	public void setup() {
		
		gate = new NorGate();
		
		a = (InputPin)gate.getPins().get("a");
		b = (InputPin)gate.getPins().get("b");
		q = (OutputPin)gate.getPins().get("q");
	}
	
	@Test
	public void testLogic() {
		
		expect(X, X);
		
		check(ZERO, ZERO, X, ONE);
		check(ZERO, ONE, ONE, ZERO);
		check(ONE, ZERO, ZERO, null);
		check(ONE, ONE, ZERO, null);

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
