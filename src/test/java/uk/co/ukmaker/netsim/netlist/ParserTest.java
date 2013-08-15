package uk.co.ukmaker.netsim.netlist;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

public class ParserTest {
	
	@Test
	public void shouldTokenize() {
		String s = "component a     b";
		Parser p = new Parser();
		p.tokenize(s);
	}
	
	@Test
	public void shouldParseSimpleInverter() throws Exception {
		InputStream netlist = ParserTest.class.getClassLoader().getResourceAsStream("simple-inverter.netlist");
		
		Parser p = new Parser();
		p.parse(netlist);
		
		Component inverter = p.getEntity();
		
		assertEquals(1, inverter.getComponents().size());
		assertEquals(2, inverter.getTerminals().size());
	}
	
	@Test
	public void shouldParseTwoInverters() throws Exception {
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
	public void shouldIncludeAndReference() throws Exception {
		
		URL r = ParserTest.class.getClassLoader().getResource("include-test.netlist");
		File f = new File(r.getFile());
		FileInputStream netlist = new FileInputStream(f);
		
		Parser p = new Parser();
		p.setBaseDir(f.getParentFile());
		p.parse(netlist);
		
		Component wrapper = p.getEntity();

		assertEquals(1, wrapper.getComponents().size());
		assertEquals(2, wrapper.getTerminals().size());
	}
	
	@Test
	public void shouldParseTerminalRanges() throws Exception {
		
		URL r = ParserTest.class.getClassLoader().getResource("adder.netlist");
		File f = new File(r.getFile());
		FileInputStream netlist = new FileInputStream(f);
		
		Parser p = new Parser();
		p.setBaseDir(f.getParentFile());
		p.parse(netlist);
		
		Component adder = p.getEntity();

		assertEquals(4, adder.getComponents().size());
		assertEquals(14, adder.getTerminals().size());
	}
}
