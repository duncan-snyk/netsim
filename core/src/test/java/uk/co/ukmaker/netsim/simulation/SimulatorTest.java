package uk.co.ukmaker.netsim.simulation;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.test.SequenceGenerator;
import uk.co.ukmaker.netsim.models.test.TestProbe;
import uk.co.ukmaker.netsim.netlist.Circuit;
import uk.co.ukmaker.netsim.netlist.Component;
import uk.co.ukmaker.netsim.netlist.ParserTest;
import uk.co.ukmaker.netsim.netlist.TestClip;
import uk.co.ukmaker.netsim.netlist.TestFixture;
import uk.co.ukmaker.netsim.parser.Parser;

public class SimulatorTest {
	
	public long t(long b) {
		return b * 100000;
	}
	
	
	@Test
	public void shouldSimulateHalfAdder() throws Exception {
		
		InputStream netlist = ParserTest.class.getClassLoader().getResourceAsStream("half-adder.netlist");
		
		Parser p = new Parser();
		p.parse(netlist);
		
		Component halfAdder = p.getEntity();
		
		// Construct a simple circuit and test it
		// This will be a half-adder
		Circuit circuit = new Circuit("TestFixture");
		circuit.addComponent(halfAdder);
		
	
		TestClip<SequenceGenerator> seqa = new TestClip<SequenceGenerator>("Ain", new SequenceGenerator());
		seqa.getModel().addValue(t(0), SignalValue.X);
		seqa.getModel().addValue(t(1), SignalValue.ZERO);
		seqa.getModel().addValue(t(2), SignalValue.ONE);
		seqa.getModel().addValue(t(3), SignalValue.ZERO);
		seqa.getModel().addValue(t(4), SignalValue.ONE);
		
		seqa.attach(halfAdder.getTerminal("a"));

		
		TestClip<SequenceGenerator> seqb = new TestClip<SequenceGenerator>("Bin", new SequenceGenerator());
		seqb.getModel().addValue(t(0), SignalValue.X);
		seqb.getModel().addValue(t(1), SignalValue.ZERO);
		seqb.getModel().addValue(t(2), SignalValue.ZERO);
		seqb.getModel().addValue(t(3), SignalValue.ONE);
		seqb.getModel().addValue(t(4), SignalValue.ONE);

		seqb.attach(halfAdder.getTerminal("b"));
		
		TestClip<TestProbe> a = new TestClip<TestProbe>(new TestProbe("A"));
		TestClip<TestProbe> b = new TestClip<TestProbe>(new TestProbe("B"));
		a.attach(halfAdder.getTerminal("a"));
		b.attach(halfAdder.getTerminal("b"));
		
		TestClip<TestProbe> sum = new TestClip<TestProbe>(new TestProbe("SUM"));
		sum.getModel().expect(t(0), SignalValue.X);
		sum.getModel().expect(t(1), SignalValue.ZERO);
		sum.getModel().expect(t(2), SignalValue.ONE);
		sum.getModel().expect(t(3), SignalValue.ONE);
		sum.getModel().expect(t(4), SignalValue.ZERO);
		sum.getModel().expect(t(5), SignalValue.ZERO);
		sum.getModel().expect(t(6), SignalValue.ZERO);
		sum.attach(halfAdder.getTerminal("sum"));

		TestClip<TestProbe> carry = new TestClip<TestProbe>(new TestProbe("CARRY"));
		carry.getModel().expect(t(0), SignalValue.X);
		carry.getModel().expect(t(1), SignalValue.ZERO);
		carry.getModel().expect(t(2), SignalValue.ZERO);
		carry.getModel().expect(t(3), SignalValue.ZERO);
		carry.getModel().expect(t(4), SignalValue.ONE);
		carry.getModel().expect(t(5), SignalValue.ONE);
		carry.getModel().expect(t(6), SignalValue.ONE);
		carry.attach(halfAdder.getTerminal("carry"));
		

		circuit.addComponent(seqa);
		circuit.addComponent(seqb);
		circuit.addComponent(sum);
		circuit.addComponent(carry);
		circuit.addComponent(a);
		circuit.addComponent(b);
		
		List<TestProbe> probes = new ArrayList<TestProbe>();
		probes.add(a.getModel());
		probes.add(b.getModel());
		probes.add(sum.getModel());
		probes.add(carry.getModel());
		
		LocalSimulatorNode sim = new LocalSimulatorNode(circuit, probes);
		sim.simulate(t(7));
		
		assertFalse(sum.getModel().hasErrors());
		assertFalse(carry.getModel().hasErrors());
	}
	
	@Test
	public void shouldRunSimulationFromFile() throws Exception {
		URL r = ParserTest.class.getClassLoader().getResource("adder.sim");
		File f = new File(r.getFile());
		FileInputStream netlist = new FileInputStream(f);
		
		Parser p = new Parser();
		p.setBaseDir(f.getParentFile());
		p.parse(netlist);
		
		TestFixture testFixture = (TestFixture)p.getEntity();
		
		LocalSimulatorNode sim = new LocalSimulatorNode(testFixture);
		sim.simulate(testFixture.getEndMoment());

	}
}
