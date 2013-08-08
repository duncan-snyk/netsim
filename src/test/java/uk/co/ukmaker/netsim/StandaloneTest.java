package uk.co.ukmaker.netsim;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.co.ukmaker.netsim.components.gates.AndGate;
import uk.co.ukmaker.netsim.components.gates.XorGate;
import uk.co.ukmaker.netsim.simulation.SequenceGenerator;
import uk.co.ukmaker.netsim.simulation.Simulation;
import uk.co.ukmaker.netsim.simulation.TestProbe;


public class StandaloneTest {
	
	@Test
	public void test() {
		
		// Construct a simple circuit and test it
		// This will be a half-adder
		Circuit circuit = new Circuit("Test");

		AndGate and = new AndGate();
		XorGate xor = new XorGate();
		
		Net neta = circuit.addNet();
		Net netb = circuit.addNet();
		
		Net sumNet = circuit.addNet();
		Net carryNet = circuit.addNet();
		
		neta.addPort(and.getPorts().get("a"));
		neta.addPort(xor.getPorts().get("a"));
		
		netb.addPort(and.getPorts().get("b"));
		netb.addPort(xor.getPorts().get("b"));
		
		sumNet.addPort(xor.getPorts().get("q"));
		carryNet.addPort(and.getPorts().get("q"));
		
		SequenceGenerator seqa = new SequenceGenerator();
		seqa.addValue(0, SignalValue.X);
		seqa.addValue(1, SignalValue.ZERO);
		seqa.addValue(2, SignalValue.ONE);
		seqa.addValue(3, SignalValue.ZERO);
		seqa.addValue(4, SignalValue.ONE);
		neta.addPort(seqa.getPorts().get("q"));

		
		SequenceGenerator seqb = new SequenceGenerator();
		seqb.addValue(0, SignalValue.X);
		seqb.addValue(1, SignalValue.ZERO);
		seqb.addValue(2, SignalValue.ZERO);
		seqb.addValue(3, SignalValue.ONE);
		seqb.addValue(4, SignalValue.ONE);
		netb.addPort(seqb.getPorts().get("q"));
		
		TestProbe a = new TestProbe("A");
		TestProbe b = new TestProbe("B");
		neta.addPort(a.getPorts().get("pin"));
		netb.addPort(b.getPorts().get("pin"));
		
		TestProbe sum = new TestProbe("SUM");
		sum.expect(0, SignalValue.X);
		sum.expect(1, SignalValue.X);
		sum.expect(2, SignalValue.ZERO);
		sum.expect(3, SignalValue.ONE);
		sum.expect(4, SignalValue.ONE);
		sum.expect(5, SignalValue.ZERO);
		sum.expect(6, SignalValue.ZERO);
		sum.expect(7, SignalValue.ZERO);

		TestProbe carry = new TestProbe("CARRY");
		carry.expect(0, SignalValue.X);
		carry.expect(1, SignalValue.X);
		carry.expect(2, SignalValue.ZERO);
		carry.expect(3, SignalValue.ZERO);
		carry.expect(4, SignalValue.ZERO);
		carry.expect(5, SignalValue.ONE);
		carry.expect(6, SignalValue.ONE);
		carry.expect(7, SignalValue.ONE);
		
		sumNet.addPort(sum.getPorts().get("pin"));
		carryNet.addPort(carry.getPorts().get("pin"));

		List<TestProbe> probes = new ArrayList<TestProbe>();
		probes.add(a);
		probes.add(b);
		probes.add(sum);
		probes.add(carry);
		
		Simulation sim = new Simulation(circuit, probes);
		sim.simulate(10);
		
		
	}

}
