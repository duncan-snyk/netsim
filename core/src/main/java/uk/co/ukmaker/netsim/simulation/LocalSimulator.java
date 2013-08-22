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

public class LocalSimulator implements Simulator, SimulatorNode {

	private Netlist netlist;
	private List<String> formats = new ArrayList<String>();
	private List<TestProbe> probes;
	private boolean failed;
	
	private NetEventQueue netEventQueue = new NetEventQueue();
	
	private NetlistDriver driver;
	
	private Map<String, Integer> netDriversList;
	
	private long lastmoment = -1;
	
	@Override
	public void simulate(Netlist netlist, long howLongFor, List<TestProbe> testProbes) throws Exception {
		this.netlist = netlist;
		this.probes = testProbes;
		generateFormats();
		simulate(howLongFor);
	}

	protected void simulate(long howLongFor) throws Exception {
		
		driver = new NetlistDriver(netlist);

		long moment = 0;
		
		failed = false;

		printHeaders();
		
		if(propagate(moment)) {
			await(moment);
			update(moment);
		}
		
		moment = useNextMoment();
		
		while(moment < howLongFor) {
			
			List<NetEvent> netEvents = netEventQueue.useScheduledEvents(moment);
			Set<Net> netsToPropagate = new HashSet<Net>();
			for(NetEvent e : netEvents) {
				netsToPropagate.add(e.net);
			}
				
			if(propagate(moment, netsToPropagate)) {
				await(moment);
				update(moment);
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
	
	public void await(long moment) {
		driver.await(moment, netDriversList, this);
	}
	
	public void update(long moment) {
		driver.update(moment, this);
	}
	
	public boolean propagate(long moment) {
		netDriversList = new HashMap<String, Integer>();
		return driver.propagate(moment, this);
	}
	
	public boolean propagate(long moment, Set<Net> nets) {
		netDriversList = new HashMap<String, Integer>();
		return driver.propagate(moment, nets, this);
	}
	

	@Override
	public void scheduleValue(Net net, ScheduledValue value) {
		
		for(InputPin pin : net.getSinks()) {
			pin.scheduleInputValue(value.getMoment(), value.getValue());
		}
	}

	@Override
	public void updateDriversList(Map<Net, Integer> netDrivers) {
		
		for(Net n : netDrivers.keySet()) {
			Integer numDrivers = netDrivers.get(n);
			String id = n.getId();
			if(this.netDriversList.containsKey(id)) {
				this.netDriversList.put(id, this.netDriversList.get(id) + numDrivers);
			} else {
				this.netDriversList.put(id, numDrivers);
			}
		}
	}

	@Override
	public void updateEventQueue(Map<Net, List<Long>> nextValues) {
		for(Net n : nextValues.keySet()) {
			List<Long> moments = nextValues.get(n);
			for(Long moment : moments) {
				netEventQueue.schedule(new NetEvent(moment, n, 1));
			}
		}
		
	}

	@Override
	public void nodeAwaited() {
	}
	
	@Override
	public void nodeUpdated() {
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
					SignalValue v = probe.getExpectedValue(moment);
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
