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

public class CompilerTest {
	
	@Test
	public void shouldCompileSimpleInverter() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, ParsingException, CompilationException {
		InputStream netlistsrc = CompilerTest.class.getClassLoader().getResourceAsStream("simple-inverter.netlist");
		
		Parser p = new Parser();
		p.parse(netlistsrc);
		
		Circuit inverter = p.getEntity();

		Compiler compiler = new Compiler();
		
		Netlist netlist = compiler.compile(inverter);
		
		assertEquals(1, netlist.getModels().size());
		assertEquals(2, netlist.getNets().size());
	}
	@Test
	public void shouldCompileTwoInverters() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, ParsingException, CompilationException {
		InputStream netlistsrc = CompilerTest.class.getClassLoader().getResourceAsStream("two-inverters.netlist");
		
		Parser p = new Parser();
		p.parse(netlistsrc);
		
		Circuit inverter = p.getEntity();

		Compiler compiler = new Compiler();
		
		Netlist netlist = compiler.compile(inverter);
		
		assertEquals(2, netlist.getModels().size());
		assertEquals(3, netlist.getNets().size());
	}
	@Test
	public void shouldCompileHalfAdder() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, ParsingException, CompilationException {
		InputStream netlistsrc = CompilerTest.class.getClassLoader().getResourceAsStream("half-adder.netlist");
		
		Parser p = new Parser();
		p.parse(netlistsrc);
		
		Circuit halfAdder = p.getEntity();

		Compiler compiler = new Compiler();
		
		Netlist netlist = compiler.compile(halfAdder);
		
		assertEquals(2, netlist.getModels().size());
		assertEquals(4, netlist.getNets().size());
	}
	@Test
	public void shouldCompileFullAdder() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, ParsingException, CompilationException {
		InputStream netlistsrc = CompilerTest.class.getClassLoader().getResourceAsStream("full-adder.netlist");
		
		Parser p = new Parser();
		p.parse(netlistsrc);
		
		Circuit fullAdder = p.getEntity();

		Compiler compiler = new Compiler();
		
		Netlist netlist = compiler.compile(fullAdder);
		
		assertEquals(5, netlist.getModels().size());
		assertEquals(8, netlist.getNets().size());
	}
}
