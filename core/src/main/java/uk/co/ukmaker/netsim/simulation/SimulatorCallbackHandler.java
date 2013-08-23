package uk.co.ukmaker.netsim.simulation;

import java.util.List;
import java.util.Map;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.netlist.Net;

public interface SimulatorCallbackHandler {
	
	/**
	 * Called during propagateOutputs to send a new ScheduledValue to a net
	 * 
	 * @param net
	 * @param value
	 */
	public void propagateOutput(String netId, ScheduledValue value) throws Exception ;
	
	/**
	 * Called when propagateOutputs is complete to inform the master which nets have how many drivers
	 * @param netDrivers <netId, numDrivers>
	 */
	public void propagatedNetDrivers(Map<String, Integer> netDrivers) throws Exception ;
	
	/**
	 * Called when updateModels is complete to inform the master of newly scheduled future events
	 * @param netMoments <netId, List<moment>>
	 */
	public void updateEventQueue(Map<String, List<Long>> netMoments) throws Exception ;
	
	
	/**
	 * Called when propagateInputs is complete to inform the master that the netlist is now ready to update models
	 */
	public void inputsPropagated() throws Exception ;
}
