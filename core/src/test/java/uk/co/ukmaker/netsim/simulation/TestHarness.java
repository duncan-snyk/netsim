package uk.co.ukmaker.netsim.simulation;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.test.SequenceGenerator;
import uk.co.ukmaker.netsim.models.test.TestProbe;
import uk.co.ukmaker.netsim.netlist.Circuit;
import uk.co.ukmaker.netsim.netlist.CompilationException;
import uk.co.ukmaker.netsim.netlist.Compiler;
import uk.co.ukmaker.netsim.netlist.Component;
import uk.co.ukmaker.netsim.netlist.Netlist;
import uk.co.ukmaker.netsim.netlist.ParserTest;
import uk.co.ukmaker.netsim.netlist.TestClip;
import uk.co.ukmaker.netsim.parser.Parser;

abstract public class TestHarness {
	
	protected List<TestClip> testClips = new ArrayList<TestClip>();
	protected List<TestProbe> testProbes = new ArrayList<TestProbe>();
	protected long moment = 0;
	
	protected Component component;
	protected Circuit circuit;
	protected Netlist netlist;
	
	protected long propagationDelay = 10000;
	
	public void loadNetlist(String name) throws Exception {
		URL r = ParserTest.class.getClassLoader().getResource(name);
		File f = new File(r.getFile());
		FileInputStream netlistsrc = new FileInputStream(f);
		
		Parser p = new Parser();
		p.setBaseDir(f.getParentFile());
		p.parse(netlistsrc);
		
		component = p.getEntity();
		
		// Construct a simple circuit and test it
		// This will be a half-adder
		circuit = new Circuit("TestFixture");
		circuit.addComponent(component);
	}
	
	public LocalSimulator getSimulator() throws CompilationException {
		
		Compiler c = new Compiler();
		
		netlist = c.compile(circuit);
		
		LocalSimulator sim = new LocalSimulator();
		
		return sim;
	}
	
	public void inject(String name, String terminalName) throws Exception {
		String[] names = Parser.generatePortNames(name);
		String[] terminalNames = Parser.generatePortNames(terminalName);
		
		if(names.length != terminalNames.length) {
			throw new Exception("Vector names have different lengths "+name+" / "+terminalName);
		}
		
		for(int i=names.length-1; i>=0;  i--) {
			
			TestClip<SequenceGenerator> clip = new TestClip<SequenceGenerator>(names[i], new SequenceGenerator());
			clip.attach(component.getTerminal(terminalNames[i]));
			testClips.add(clip);
			circuit.addComponent(clip);
	
			TestClip<TestProbe> pclip = new TestClip<TestProbe>(new TestProbe("p"+names[i]));
			pclip.attach(component.getTerminal(terminalNames[i]));
			testProbes.add(pclip.getModel());
			circuit.addComponent(pclip);
		}
	}
	
	public void probe(String name, String terminalName) throws Exception {
		String[] names = Parser.generatePortNames(name);
		String[] terminalNames = Parser.generatePortNames(terminalName);
		
		if(names.length != terminalNames.length) {
			throw new Exception("Vector names have different lengths "+name+" / "+terminalName);
		}
		
		for(int i=names.length-1; i>=0;  i--) {
			
			TestClip<TestProbe> clip = new TestClip<TestProbe>(new TestProbe("p"+names[i]));
			clip.attach(component.getTerminal(terminalNames[i]));
			testClips.add(clip);
			circuit.addComponent(clip);
			
			testProbes.add(clip.getModel());
		}
	}
	
	public void expect(SignalValue...signalValues) {
		
		int i=0;
		
		for(SignalValue value : signalValues) {
			TestClip t = testClips.get(i++);
			if(t.getModel() instanceof SequenceGenerator) {
				((SequenceGenerator)t.getModel()).addValue(moment, value);
			} else {
				((TestProbe)t.getModel()).expect(moment, value);
			}
		}
		
		moment+= propagationDelay;
	}
}
