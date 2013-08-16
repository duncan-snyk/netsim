package uk.co.ukmaker.netsim.simulation;

import static org.junit.Assert.assertFalse;
import static uk.co.ukmaker.netsim.SignalValue.ONE;
import static uk.co.ukmaker.netsim.SignalValue.X;
import static uk.co.ukmaker.netsim.SignalValue.Z;
import static uk.co.ukmaker.netsim.SignalValue.ZERO;

import org.junit.Test;

import uk.co.ukmaker.netsim.SignalValue;

public class EightBitAdderSliceTest extends TestHarness {
	
	@Test
	public void shouldSimulateFullAdder() throws Exception {
		
		loadNetlist("8-bit-adder-slice.netlist");

		inject("Cin", "cin");
		inject("Ain[8]",   "a[8]");
		inject("Bin[8]",   "b[8]");

		probe("Sum[8]",     "sum[8]");
		probe("Carry", "cout");

		// Here come the test vectors
		//      CIN    A     B    SUM  CARRY
		expect(   X,    X,X,X,X,X,X,X,X,    X,X,X,X,X,X,X,X,    X,X,X,X,X,X,X,X,    X);
		for(int a=0; a<256; a++) {
			for(int b=0; b<256; b++) {
				for(int c=0; c<2; c++) {
					expect(c, a, b, (a+b+c) & 0xff, (a+b+c) > 255 ? 1 : 0);
				}
			}
		}
	
		Simulation sim = new Simulation(circuit, testProbes);
		sim.simulate(moment-1);
		
		assertFalse(sim.failed());
	}
	
	public void expect(int cin, int a, int b, int s, int carry) {
		SignalValue[] values = new SignalValue[26];
		values[0] = val(cin);
		
		values[8] = val ((a >> 0) & 1);
		values[7] = val ((a >> 1) & 1);
		values[6] = val ((a >> 2) & 1);
		values[5] = val ((a >> 3) & 1);
		values[4] = val ((a >> 4) & 1);
		values[3] = val ((a >> 5) & 1);
		values[2] = val ((a >> 6) & 1);
		values[1] = val ((a >> 7) & 1);
		
		values[16] = val ((b >> 0) & 1);
		values[15] = val ((b >> 1) & 1);
		values[14] = val ((b >> 2) & 1);
		values[13] = val ((b >> 3) & 1);
		values[12] = val ((b >> 4) & 1);
		values[11] = val ((b >> 5) & 1);
		values[10] = val ((b >> 6) & 1);
		values[9] = val ((b >> 7) & 1);
		
		values[24] = val ((s >> 0) & 1);
		values[23] = val ((s >> 1) & 1);
		values[22] = val ((s >> 2) & 1);
		values[21] = val ((s >> 3) & 1);
		values[20] = val ((s >> 4) & 1);
		values[19] = val ((s >> 5) & 1);
		values[18] = val ((s >> 6) & 1);
		values[17] = val ((s >> 7) & 1);
		


		values[25] = val (carry);
		
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
