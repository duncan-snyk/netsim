package uk.co.ukmaker.netsim.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.test.TestProbe;
import uk.co.ukmaker.netsim.netlist.Net;
import uk.co.ukmaker.netsim.netlist.Netlist;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.simulation.NetEventQueue.NetEvent;

public class Simulator implements NetEventPropagator {

	private Netlist netlist;
	private List<String> formats = new ArrayList<String>();
	private List<TestProbe> probes;
	private boolean failed;
	
	private NetEventQueue netEventQueue = new NetEventQueue();
	
	private NetlistDriver driver = new LocalNetlistDriver();
	
	private Map<String, Integer> netDriversList;
	
	private long lastmoment = -1;
	
	private int printed = 0;
	
	public void setNetlistDriver(NetlistDriver driver) {
		this.driver = driver;
	}
	
	public void simulate(Netlist netlist, long howLongFor, List<TestProbe> testProbes) throws Exception {
		this.netlist = netlist;
		this.probes = testProbes;
		generateFormats();
		simulate(howLongFor);
	}

	protected void simulate(long howLongFor) throws Exception {
		
		driver.setNetlist(netlist);

		long moment = 0;
		
		failed = false;

		printHeaders();
		
		updateEventQueue(driver.initialiseModels());
		moment = useNextMoment();
		
		while(moment < howLongFor) {
			
			List<NetEvent> netEvents = netEventQueue.useScheduledEvents(moment);
			Set<String> netsToPropagate = new HashSet<String>();
			for(NetEvent e : netEvents) {
				netsToPropagate.add(e.net.getId());
			}
				
			if(propagateOutputs(moment, netsToPropagate)) {

				propagateInputs(moment);
				
				updateModels(moment);
			}
			
			moment = useNextMoment();
			
			printState(moment);
		}

		printState(howLongFor);
	}
	
	public long useNextMoment() throws Exception {
		
		NetEvent nextEvent = netEventQueue.head();
		
		if(nextEvent == null) {
			throw new Exception("No new net events are queued");
		}
		
		if(nextEvent.moment < lastmoment) {
			throw new Exception("Causality violation at "+lastmoment+" - trying to rewind time to "+nextEvent.moment);
		}
		
		lastmoment = nextEvent.moment;
		
		return nextEvent.moment;
	}
	
	public void propagateInputs(long moment)  throws Exception {
		driver.propagateInputs(moment, netDriversList);
	}
	
	public void updateModels(long moment) throws Exception {
		updateEventQueue(driver.updateModels(moment));
	}

	public boolean propagateOutputs(long moment, Set<String> netIds) throws Exception {
		netDriversList = new HashMap<String, Integer>();
		Map<String, Integer> netDrivers = driver.propagateOutputs(moment, netIds, this);
		
		boolean outputsPropagated = false;
		
		for(String id : netDrivers.keySet()) {
			
			// Yes, we have propagated something
			outputsPropagated = true;
			
			Integer numDrivers = netDrivers.get(id);
			if(this.netDriversList.containsKey(id)) {
				this.netDriversList.put(id, this.netDriversList.get(id) + numDrivers);
			} else {
				this.netDriversList.put(id, numDrivers);
			}
		}
		
		return outputsPropagated;
	}

	@Override
	public void propagateOutput(String netId, ScheduledValue value) {
		try {
			driver.scheduleNetValue(netId, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void updateEventQueue(Map<String, Set<Long>> nextValues) {
		for(String n : nextValues.keySet()) {
			Set<Long> moments = nextValues.get(n);
			for(Long moment : moments) {
				netEventQueue.schedule(new NetEvent(moment, netlist.getNet(n), 1));
			}
		}
	}

	public Netlist getNetlist() {
		return netlist;
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
		
		if(printed == 20) {
			printHeaders();
			printed = 0;
		}
		
		printed++;

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
					SignalValue v = probe.getExpectedValue();
					sb.append(String.format(formats.get(i++), v == null ? "" : v));
				}
			}
			System.out.println(sb.toString());
		}
	}
	
	public boolean failed() {
		return failed;
	}

}
