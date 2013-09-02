package uk.co.ukmaker.netsim.simulation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.netlist.Net;
import uk.co.ukmaker.netsim.netlist.Netlist;

public interface NetlistDriver {
	
	public void setNetlist(Netlist netlist);
	
	public void scheduleNetValue(String netId, ScheduledValue value) throws Exception;
	
	/**
	 * Initialise all models so they can schedule their first events
	 * @return Map<String, List<Long>> nextValues <netId, <moments>>
	 */
	public abstract Map<String, Set<Long>> initialiseModels() throws Exception;

	/**
	 * Propagate any scheduled events from output pins to their nets
	 * @param moment
	 * @param nets
	 * @param simulator
	 * @return Map<String, Integer> <netId, numDrivers>
	 */
	public abstract Map<String, Integer> propagateOutputs(long moment, Set<String> netIds,
			NetEventPropagator propagator) throws Exception;
	
	/**
	 * Propagate any scheduled events from nets to their input pins
	 * @param moment
	 * @param netDrivers
	 * @param callbackHandler
	 */
	public abstract void propagateInputs(long moment, Map<String, Integer> netDrivers) throws Exception;

	/**
	 * Cause all models on the updated nets to update
	 * @param moment
	 * @return Map<String, List<Long>> nextValues <netId, <moments>>
	 */
	public abstract Map<String, Set<Long>> updateModels(long moment) throws Exception;


	
}