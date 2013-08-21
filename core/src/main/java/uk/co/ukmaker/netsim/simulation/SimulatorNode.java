package uk.co.ukmaker.netsim.simulation;

import java.util.List;
import java.util.Map;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.netlist.Net;

public interface SimulatorNode {
	
	public void scheduleValue(Net net, ScheduledValue value);
	
	public void updateDriversList(Map<Net, Integer> netDrivers);
	public void updateEventQueue(Map<Net, List<Long>> nextValues);
	
	public void nodeAwaited() ;
	
	public void nodeUpdated();
}
