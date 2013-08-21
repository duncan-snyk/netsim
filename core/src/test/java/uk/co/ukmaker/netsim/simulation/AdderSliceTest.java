package uk.co.ukmaker.netsim.simulation;

import static org.junit.Assert.assertFalse;
import static uk.co.ukmaker.netsim.SignalValue.ONE;
import static uk.co.ukmaker.netsim.SignalValue.X;
import static uk.co.ukmaker.netsim.SignalValue.Z;
import static uk.co.ukmaker.netsim.SignalValue.ZERO;

import org.junit.Test;

import uk.co.ukmaker.netsim.SignalValue;

public class AdderSliceTest extends TestHarness {
	
	@Test
	public void shouldSimulateFullAdder() throws Exception {
		
		loadNetlist("adder-slice.netlist");

		inject("Cin", "cin");
		inject("Ain[4]",   "a[4]");
		inject("Bin[4]",   "b[4]");

		probe("Sum[4]",     "sum[4]");
		probe("Carry", "cout");

		// Here come the test vectors
		//      CIN    A     B    SUM  CARRY
		expect(   X,    X,X,X,X,    X,X,X,X,    X,X,X,X,    X);
		for(int a=0; a<16; a++) {
			for(int b=0; b<16; b++) {
				for(int c=0; c<2; c++) {
					expect(c, a, b, (a+b+c) & 0xf, (a+b+c) > 15 ? 1 : 0);
				}
			}
		}
	
		LocalSimulator sim = getSimulator();
		
		propagationDelay = 1000000;
		
		sim.simulate(netlist, moment, testProbes);
		
		assertFalse(sim.failed());
	}
	
	public void expect(int cin, int a, int b, int sum, int carry) {
		SignalValue[] values = new SignalValue[14];
		values[0] = val(cin);
		
		values[4] = val(a & 1);
		values[3] = val ((a >> 1) & 1);
		values[2] = val ((a >> 2) & 1);
		values[1] = val ((a >> 3) & 1);
		
		values[8] = val(b & 1);
		values[7] = val ((b >> 1) & 1);
		values[6] = val ((b >> 2) & 1);
		values[5] = val ((b >> 3) & 1);
		
		values[12] = val(sum & 1);
		values[11] = val ((sum >> 1) & 1);
		values[10] = val ((sum >> 2) & 1);
		values[9] = val ((sum >> 3) & 1);

		values[13] = val (carry);
		
		expect(values);
}
	
	public SignalValue val(int v) {
		if(v == 0) {
			return ZERO;
		}
		
		if(v == 1) {
			return ONE;
		}
		
		if(v == -1) {
			return Z;
		}
		
		return X;
	}
}
