package uk.co.ukmaker.netsim.netlist;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.co.ukmaker.netsim.Net;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.models.gates.AndGate;
import uk.co.ukmaker.netsim.models.gates.XorGate;
import uk.co.ukmaker.netsim.models.test.SequenceGenerator;
import uk.co.ukmaker.netsim.models.test.TestProbe;
import uk.co.ukmaker.netsim.simulation.Simulation;

public class ParserTest {
	
	@Test
	public void shouldTokenize() {
		String s = "component a     b";
		Parser p = new Parser();
		p.tokenize(s);
	}
	
	@Test
	public void shouldParseSimpleInverter() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, ParsingException {
		InputStream netlist = ParserTest.class.getClassLoader().getResourceAsStream("simple-inverter.netlist");
		
		Parser p = new Parser();
		p.parse(netlist);
		
		Component inverter = p.getEntity();
		
		assertEquals(1, inverter.getComponents().size());
		assertEquals(2, inverter.getTerminals().size());
	}
	
	@Test
	public void shouldParseTwoInverters() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, ParsingException {
		InputStream netlist = ParserTest.class.getClassLoader().getResourceAsStream("two-inverters.netlist");
		
		Parser p = new Parser();
		p.parse(netlist);
		
		Component inverter = p.getEntity();
		
		assertEquals(2, inverter.getComponents().size());
		assertEquals(2, inverter.getTerminals().size());
	}
	
	@Test
	public void shouldParseHalfAdder() throws Exception {
		
		InputStream netlist = ParserTest.class.getClassLoader().getResourceAsStream("half-adder.netlist");
		
		Parser p = new Parser();
		p.parse(netlist);
		
		Component halfAdder = p.getEntity();

		assertEquals(2, halfAdder.getComponents().size());
		assertEquals(4, halfAdder.getTerminals().size());
	}
	
	@Test
	public void shouldParseFullAdder() throws Exception {
		
		InputStream netlist = ParserTest.class.getClassLoader().getResourceAsStream("full-adder.netlist");
		
		Parser p = new Parser();
		p.parse(netlist);
		
		Component fullAdder = p.getEntity();

		assertEquals(5, fullAdder.getComponents().size());
		assertEquals(5, fullAdder.getTerminals().size());
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
		

	}

}
