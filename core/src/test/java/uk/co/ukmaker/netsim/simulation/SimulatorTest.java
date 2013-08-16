package uk.co.ukmaker.netsim.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

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
		seqa.getModel().addValue(0, SignalValue.X);
		seqa.getModel().addValue(1, SignalValue.ZERO);
		seqa.getModel().addValue(2, SignalValue.ONE);
		seqa.getModel().addValue(3, SignalValue.ZERO);
		seqa.getModel().addValue(4, SignalValue.ONE);
		
		seqa.attach(halfAdder.getTerminal("a"));

		
		TestClip<SequenceGenerator> seqb = new TestClip<SequenceGenerator>("Bin", new SequenceGenerator());
		seqb.getModel().addValue(0, SignalValue.X);
		seqb.getModel().addValue(1, SignalValue.ZERO);
		seqb.getModel().addValue(2, SignalValue.ZERO);
		seqb.getModel().addValue(3, SignalValue.ONE);
		seqb.getModel().addValue(4, SignalValue.ONE);

		seqb.attach(halfAdder.getTerminal("b"));
		
		TestClip<TestProbe> a = new TestClip<TestProbe>(new TestProbe("A"));
		TestClip<TestProbe> b = new TestClip<TestProbe>(new TestProbe("B"));
		a.attach(halfAdder.getTerminal("a"));
		b.attach(halfAdder.getTerminal("b"));
		
		TestClip<TestProbe> sum = new TestClip<TestProbe>(new TestProbe("SUM"));
		sum.getModel().expect(0, SignalValue.X);
		sum.getModel().expect(1, SignalValue.X);
		sum.getModel().expect(2, SignalValue.ZERO);
		sum.getModel().expect(3, SignalValue.ONE);
		sum.getModel().expect(4, SignalValue.ONE);
		sum.getModel().expect(5, SignalValue.ZERO);
		sum.getModel().expect(6, SignalValue.ZERO);
		sum.getModel().expect(7, SignalValue.ZERO);
		sum.attach(halfAdder.getTerminal("sum"));

		TestClip<TestProbe> carry = new TestClip<TestProbe>(new TestProbe("CARRY"));
		carry.getModel().expect(0, SignalValue.X);
		carry.getModel().expect(1, SignalValue.X);
		carry.getModel().expect(2, SignalValue.ZERO);
		carry.getModel().expect(3, SignalValue.ZERO);
		carry.getModel().expect(4, SignalValue.ZERO);
		carry.getModel().expect(5, SignalValue.ONE);
		carry.getModel().expect(6, SignalValue.ONE);
		carry.getModel().expect(7, SignalValue.ONE);
		carry.attach(halfAdder.getTerminal("carry"));
		

		circuit.addComponent(seqa);
		circuit.addComponent(seqb);
		circuit.addComponent(sum);
		circuit.addComponent(carry);
		circuit.addComponent(a);
		circuit.addComponent(b);
		
		
		Simulation sim = new Simulation(circuit);
		sim.simulate(10);
		
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
		
		Simulation sim = new Simulation(testFixture);
		sim.simulate(testFixture.getEndMoment());

	}
}
