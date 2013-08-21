package uk.co.ukmaker.netsim.simulation;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import org.junit.Test;

import uk.co.ukmaker.netsim.netlist.Netlist;
import uk.co.ukmaker.netsim.netlist.ParserTest;
import uk.co.ukmaker.netsim.netlist.TestFixture;
import uk.co.ukmaker.netsim.parser.Parser;
import uk.co.ukmaker.netsim.netlist.Compiler;

/**
 * A basic test of the LocalSimulator implementation
 * 
 * Loads the inverter simulation and runs it
 * 
 * @author mcintyred
 *
 */
public class LocalSimulatorTest {
	
	@Test
	public void shouldRunSimulationFromFile() throws Exception {
		URL r = ParserTest.class.getClassLoader().getResource("inverter.sim");
		File f = new File(r.getFile());
		FileInputStream source = new FileInputStream(f);
		
		Parser p = new Parser();
		p.setBaseDir(f.getParentFile());
		p.parse(source);
		
		TestFixture testFixture = (TestFixture)p.getEntity();
		
		Compiler c = new Compiler();
		
		Netlist netlist = c.compile(testFixture);
		
		LocalSimulator sim = new LocalSimulator();
		
		sim.simulate(netlist, testFixture.getEndMoment(), testFixture.getTestProbes());

	}

}
