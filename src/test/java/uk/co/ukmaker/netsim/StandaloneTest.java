package uk.co.ukmaker.netsim;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.co.ukmaker.netsim.models.gates.AndGate;
import uk.co.ukmaker.netsim.models.gates.XorGate;
import uk.co.ukmaker.netsim.netlist.Circuit;
import uk.co.ukmaker.netsim.netlist.Wire;
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
		
		circuit.addComponent(and);
		circuit.addComponent(xor);
		
		Wire neta = new Wire();
		Wire netb = new Wire();
		
		Wire sumNet = new Wire();
		Wire carryNet = new Wire();
		
		neta.addTerminal(and.getPins().get("a"));
		neta.addTerminal(xor.getPins().get("a"));
		
		netb.addTerminal(and.getPins().get("b"));
		netb.addTerminal(xor.getPins().get("b"));
		
		sumNet.addTerminal(xor.getPins().get("q"));
		carryNet.addTerminal(and.getPins().get("q"));
		
		SequenceGenerator seqa = new SequenceGenerator();
		seqa.addValue(0, SignalValue.X);
		seqa.addValue(1, SignalValue.ZERO);
		seqa.addValue(2, SignalValue.ONE);
		seqa.addValue(3, SignalValue.ZERO);
		seqa.addValue(4, SignalValue.ONE);
		neta.addTerminal(seqa.getPins().get("q"));

		
		SequenceGenerator seqb = new SequenceGenerator();
		seqb.addValue(0, SignalValue.X);
		seqb.addValue(1, SignalValue.ZERO);
		seqb.addValue(2, SignalValue.ZERO);
		seqb.addValue(3, SignalValue.ONE);
		seqb.addValue(4, SignalValue.ONE);
		netb.addTerminal(seqb.getPins().get("q"));
		
		TestProbe a = new TestProbe("A");
		TestProbe b = new TestProbe("B");
		neta.addTerminal(a.getPins().get("pin"));
		netb.addTerminal(b.getPins().get("pin"));
		
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
		
		sumNet.addTerminal(sum.getPins().get("pin"));
		carryNet.addTerminal(carry.getPins().get("pin"));

		List<TestProbe> probes = new ArrayList<TestProbe>();
		probes.add(a);
		probes.add(b);
		probes.add(sum);
		probes.add(carry);
		
		circuit.addComponent(seqa);
		circuit.addComponent(seqb);
		circuit.addComponent(sum);
		circuit.addComponent(carry);
		
		Simulation sim = new Simulation(circuit, probes);
		sim.simulate(10);
		
		
	}

}
