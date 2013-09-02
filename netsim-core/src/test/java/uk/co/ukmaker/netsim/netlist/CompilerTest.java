package uk.co.ukmaker.netsim.netlist;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

import uk.co.ukmaker.netsim.parser.Parser;

public class CompilerTest {
	
	@Test
	public void shouldCompileSimpleInverter() throws Exception {
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
	public void shouldCompileTwoInverters() throws Exception {
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
	public void shouldCompileHalfAdder() throws Exception {
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
	public void shouldCompileFullAdder() throws Exception {
		InputStream netlistsrc = CompilerTest.class.getClassLoader().getResourceAsStream("full-adder.netlist");
		
		Parser p = new Parser();
		p.parse(netlistsrc);
		
		Circuit fullAdder = p.getEntity();

		Compiler compiler = new Compiler();
		
		Netlist netlist = compiler.compile(fullAdder);
		
		assertEquals(5, netlist.getModels().size());
		assertEquals(8, netlist.getNets().size());
	}
	
	
	@Test
	public void shouldCompile2BitAdderSlice() throws Exception {
		URL r = ParserTest.class.getClassLoader().getResource("2-bit-adder-slice.netlist");
		File f = new File(r.getFile());
		FileInputStream netlistsrc = new FileInputStream(f);
		
		Parser p = new Parser();
		p.setBaseDir(f.getParentFile());
		p.parse(netlistsrc);
		
		Circuit fullAdder = p.getEntity();

		Compiler compiler = new Compiler();
		
		Netlist netlist = compiler.compile(fullAdder);
		
		assertEquals(5 * 2, netlist.getModels().size());
		assertEquals(15, netlist.getNets().size());
	}
	
	@Test
	public void shouldCompileAdderSlice() throws Exception {
		URL r = ParserTest.class.getClassLoader().getResource("adder-slice.netlist");
		File f = new File(r.getFile());
		FileInputStream netlistsrc = new FileInputStream(f);
		
		Parser p = new Parser();
		p.setBaseDir(f.getParentFile());
		p.parse(netlistsrc);
		
		Circuit fullAdder = p.getEntity();

		Compiler compiler = new Compiler();
		
		Netlist netlist = compiler.compile(fullAdder);
		
		assertEquals(5 * 4, netlist.getModels().size());
		assertEquals(29, netlist.getNets().size());
	}

	
	@Test
	public void shouldCompile8BitAdderSlice() throws Exception {
		URL r = ParserTest.class.getClassLoader().getResource("8-bit-adder-slice.netlist");
		File f = new File(r.getFile());
		FileInputStream netlistsrc = new FileInputStream(f);
		
		Parser p = new Parser();
		p.setBaseDir(f.getParentFile());
		p.parse(netlistsrc);
		
		Circuit fullAdder = p.getEntity();

		Compiler compiler = new Compiler();
		
		Netlist netlist = compiler.compile(fullAdder);
		
		assertEquals(5 * 4 * 2, netlist.getModels().size());
		assertEquals(57, netlist.getNets().size());
	}
}
