package uk.co.ukmaker.netsim.simulation;

import static org.junit.Assert.assertFalse;
import static uk.co.ukmaker.netsim.SignalValue.ONE;
import static uk.co.ukmaker.netsim.SignalValue.X;
import static uk.co.ukmaker.netsim.SignalValue.ZERO;

import org.junit.Test;

public class FullAdderTest extends TestHarness {
	
	@Test
	public void shouldSimulateFullAdder() throws Exception {
		
		loadNetlist("full-adder.netlist");

		inject("Cin", "cin");
		inject("Ain",   "a");
		inject("Bin",   "b");

		probe("Sum",     "sum");
		probe("Carry", "cout");
		
		propagationDelay = 1000000;


		// Here come the test vectors
		//      CIN    A     B    SUM  CARRY
		expect(   X,    X,    X,    X,    X);
		
		expect(ZERO, ZERO, ZERO, ZERO, ZERO);
		expect(ZERO, ZERO,  ONE,  ONE, ZERO);
		expect(ZERO,  ONE, ZERO,  ONE, ZERO);
		expect(ZERO,  ONE,  ONE, ZERO,  ONE);
		
		expect( ONE, ZERO, ZERO,  ONE, ZERO);
		expect( ONE, ZERO,  ONE, ZERO,  ONE);
		expect( ONE,  ONE, ZERO, ZERO,  ONE);
		expect( ONE,  ONE,  ONE,  ONE,  ONE);
		
		expect(   X, ZERO, ZERO,    X,    X);
		expect(ZERO,    X, ZERO,    X,    X);
		expect(ZERO, ZERO,    X,    X,    X);
	
		
		LocalSimulator sim = getSimulator();
				
		sim.simulate(netlist, moment, testProbes);
		
		assertFalse(sim.failed());
	}
}
