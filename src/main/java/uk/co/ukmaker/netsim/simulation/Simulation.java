package uk.co.ukmaker.netsim.simulation;

import java.util.ArrayList;
import java.util.List;

import uk.co.ukmaker.netsim.Circuit;

public class Simulation {
	
	private Circuit circuit;
	
	private List<TestProbe> probes;
	private List<String> formats = new ArrayList<String>();
		
	public Simulation(final Circuit circuit, final List<TestProbe> probes) {
		this.circuit = circuit;
		this.probes = probes;
		if(probes !=  null) {
			for(TestProbe probe : probes) {
				if(probe.getName().length() < 4) {
					formats.add("%4s ");
				} else {
					formats.add("%"+probe.getName().length()+"s ");
				}
			}
		}
	}
	
	public void simulate(long howLongFor) {
		
		long moment;
		
		printHeaders();
		
		for(moment = 0; moment < howLongFor; moment++) {
			
			printState(moment);
			circuit.propagateOutputEvents(moment);
			circuit.update(moment);
		}
		
		printState(howLongFor);
	}
	
	public void printHeaders() {
		if(probes != null) {
			int i = 0;
			StringBuffer sb = new StringBuffer();
			
			sb.append("Timestamp ");
			for(TestProbe probe : probes) {
				sb.append(String.format(formats.get(i++), probe.getName()));
			}
			
			System.out.println(sb.toString());
		}
	}
	
	public void printState(long moment) {
		if(probes != null) {
			int i=0;
			StringBuffer sb = new StringBuffer();
			sb.append(String.format("%9s ", moment));
			for(TestProbe probe : probes) {
				sb.append(String.format(formats.get(i++), probe.getValue()));
			}
			
			System.out.println(sb.toString());
		}
	}

}
