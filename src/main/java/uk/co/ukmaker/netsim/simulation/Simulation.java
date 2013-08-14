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

	public Simulation(final Circuit circuit) throws CompilationException {
		Compiler compiler = new Compiler();
		netlist = compiler.compile(circuit);

		for (TestProbe probe : netlist.getTestProbes()) {
			if (probe.getName().length() < 4) {
				formats.add("%4s ");
			} else {
				formats.add("%" + probe.getName().length() + "s ");
			}
		}
	}

	public void simulate(long howLongFor) {

		long moment;

		printHeaders();

		for (moment = 0; moment < howLongFor; moment++) {

			printState(moment);
			netlist.propagateOutputEvents(moment);
			netlist.update(moment);
		}

		printState(howLongFor);
	}

	public void printHeaders() {
		int i = 0;
		if (netlist.getTestProbes().size() > 0) {
			StringBuffer sb = new StringBuffer();

			sb.append("Timestamp ");
			for (TestProbe probe : netlist.getTestProbes()) {
				sb.append(String.format(formats.get(i++), probe.getName()));
			}

			System.out.println(sb.toString());
		}
	}

	public void printState(long moment) {
		int i = 0;

		if (netlist.getTestProbes().size() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append(String.format("%9s ", moment));
			for (TestProbe probe : netlist.getTestProbes()) {
				sb.append(String.format(formats.get(i++), probe.getValue()));
			}

			System.out.println(sb.toString());
		}
	}

}
