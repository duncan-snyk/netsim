package uk.co.ukmaker.netsim.netlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.test.SequenceGenerator;
import uk.co.ukmaker.netsim.models.test.TestProbe;
import uk.co.ukmaker.netsim.parser.Parser;

public class TestFixture extends Circuit {
	
	protected List<TestClip> testClips = new ArrayList<TestClip>();
	protected List<TestProbe> testProbes = new ArrayList<TestProbe>();
	
	protected Map<String, TestClip> testClipMap = new HashMap<String, TestClip>();
	protected long moment = 0;
	
	protected Component deviceUnderTest;


	public TestFixture(Component parentComponent, String name) {
		super(parentComponent, name);
	}
	
	public TestFixture(String name) {
		this(null, name);
	}
	
	public void setDeviceUnderTest(Component deviceUnderTest) {
		this.deviceUnderTest = deviceUnderTest;
	}
	
	public void inject(String name, String terminalName) throws Exception {
		String[] names = Parser.generatePortNames(name);
		String[] terminalNames = Parser.generatePortNames(terminalName);
		
		if(names.length != terminalNames.length) {
			throw new Exception("Vector names have different lengths "+name+" / "+terminalName);
		}
		
		for(int i=names.length-1; i>=0;  i--) {
			
			TestClip<SequenceGenerator> clip = new TestClip<SequenceGenerator>(names[i], new SequenceGenerator());
			clip.attach(deviceUnderTest.getTerminal(terminalNames[i]));
			testClips.add(clip);
			testClipMap.put(clip.getName(), clip);
			addComponent(clip);
	
			TestClip<TestProbe> pclip = new TestClip<TestProbe>(new TestProbe("p"+names[i]));
			pclip.attach(deviceUnderTest.getTerminal(terminalNames[i]));
			testProbes.add(pclip.getModel());
			addComponent(pclip);
		}
	}
	
	public void probe(String name, String terminalName) throws Exception {
		String[] names = Parser.generatePortNames(name);
		String[] terminalNames = Parser.generatePortNames(terminalName);
		
		if(names.length != terminalNames.length) {
			throw new Exception("Vector names have different lengths "+name+" / "+terminalName);
		}
		
		for(int i=names.length-1; i>=0;  i--) {
			
			TestClip<TestProbe> clip = new TestClip<TestProbe>(new TestProbe(names[i]));
			clip.attach(deviceUnderTest.getTerminal(terminalNames[i]));
			testClips.add(clip);
			testClipMap.put(clip.getName(), clip);
			addComponent(clip);
			
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
		
		moment++;
	}
	
	public TestClip getTestClip(String name) {
		return testClipMap.get(name);
	}
	
	public void generate(long moment, String  clipName, SignalValue value) {
		((SequenceGenerator)getTestClip(clipName).getModel()).addValue(moment, value);
	}
	
	public void expect(long moment, String  clipName, SignalValue value) {
		((TestProbe)getTestClip(clipName).getModel()).expect(moment, value);
	}
	
	public long getEndMoment() {
		return moment;
	}
	
	public void setEndMoment(long moment) {
		this.moment = moment;
	}
	
	public List<TestProbe> getTestProbes() {
		return testProbes;
	}
}
