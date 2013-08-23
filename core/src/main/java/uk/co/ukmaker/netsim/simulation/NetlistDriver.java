package uk.co.ukmaker.netsim.simulation;

import java.util.Collection;
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
	 * @param callbackHandler
	 */
	public abstract void initialise(SimulatorCallbackHandler callbackHandler) throws Exception;

	/**
	 * Propagate any scheduled events from output pins to their nets
	 * @param moment
	 * @param nets
	 * @param simulator
	 * @return
	 */
	public abstract void propagateOutputs(long moment, Set<String> netIds,
			SimulatorCallbackHandler callbackHandler) throws Exception;
	
	/**
	 * Propagate any scheduled events from nets to their input pins
	 * @param moment
	 * @param netDrivers
	 * @param callbackHandler
	 */
	public abstract void propagateInputs(long moment, Map<String, Integer> netDrivers,
			SimulatorCallbackHandler callbackHandler) throws Exception;

	/**
	 * Cause all models on the updated nets to update
	 * @param moment
	 */
	public abstract void updateModels(long moment,
			SimulatorCallbackHandler callbackHandler) throws Exception;


	
}