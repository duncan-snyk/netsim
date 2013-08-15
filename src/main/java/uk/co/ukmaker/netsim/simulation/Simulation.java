package uk.co.ukmaker.netsim.simulation;

import java.util.ArrayList;
import java.util.List;

import uk.co.ukmaker.netsim.models.test.TestProbe;
import uk.co.ukmaker.netsim.netlist.Circuit;
import uk.co.ukmaker.netsim.netlist.CompilationException;
import uk.co.ukmaker.netsim.netlist.Compiler;
import uk.co.ukmaker.netsim.netlist.Netlist;

public class Simulation {

	private Netlist netlist;
	private List<String> formats = new ArrayList<String>();
	
	private List<TestProbe> probes;
	
	private boolean failed;

	public Simulation(final Circuit circuit, List<TestProbe> probes) throws CompilationException {
		Compiler compiler = new Compiler();
		netlist = compiler.compile(circuit);
		this.probes = probes;
		
		generateFormats();
	}
	
	public Simulation(final Circuit circuit) throws CompilationException {
		Compiler compiler = new Compiler();
		netlist = compiler.compile(circuit);
		this.probes = netlist.getTestProbes();
		generateFormats();
	}
	
	private void generateFormats() {
		for (TestProbe probe : probes) {
			if (probe.getName().length() < 4) {
				formats.add("%4s ");
			} else {
				formats.add("%" + probe.getName().length() + "s ");
			}
		}
	}

	public void simulate(long howLongFor) {

		long moment;
		
		failed = false;

		printHeaders();

		for (moment = 0; moment < howLongFor; moment++) {

			boolean propagated = false;
			
			do {
				propagated = netlist.propagateOutputEvents(moment);
				netlist.update(moment);
			} while(propagated);
			
			printState(moment);
		}

		printState(howLongFor);
	}

	public void printHeaders() {
		int i = 0;
		if (probes.size() > 0) {
			StringBuffer sb = new StringBuffer();

			sb.append("Timestamp ");
			for (TestProbe probe : probes) {
				sb.append(String.format(formats.get(i++), probe.getName()));
			}

			System.out.println(sb.toString());
		}
	}

	public void printState(long moment) {
		int i = 0;
		
		boolean hasErrors = false;

		if (probes.size() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append(String.format("%9s ", moment));
			for (TestProbe probe : probes) {
				sb.append(String.format(formats.get(i++), probe.getValue()));
				
				hasErrors = probe.hasErrors() ? true : hasErrors;
			}

			if(hasErrors) {
				failed = true;
				i=0;
				sb.append(String.format("\n%9s ", "ERROR"));
				for (TestProbe probe : probes) {
					sb.append(String.format(formats.get(i++), probe.getExpectedValue(moment)));
				}
			}
			System.out.println(sb.toString());
		}
	}
	
	public boolean failed() {
		return failed;
	}

}
