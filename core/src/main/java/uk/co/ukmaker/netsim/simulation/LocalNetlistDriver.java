package uk.co.ukmaker.netsim.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.netlist.Net;
import uk.co.ukmaker.netsim.netlist.Netlist;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;

/**
 * A simulation cycle works as follows:
 * 
 * Devices maintain a buffer of scheduled events on their output pins
 * Events scheduled for some future time can be cancelled in-flight
 *  (imagine an output enable with a short tpd overriding an address decode)
 * At a simulation moment t
 * 
 *   - The master sends a PROPAGATE message to all nets with scheduled changes
 *   - At t=0 this is all models
 *   - Models connected to those nets schedule events on their output pins
 *   - The time of the scheduled event and the net is noted
 *   - Any events scheduled for the current time are removed from the output pin and sent 
 *   - to the appropriate net queue
 *   - A PROPAGATED message is sent back to the master containing:
 *     x {netId, numDrivers} listing nets which received events this moment
 *     x {netId, time} listing nets with output pins currently scheduled to produce future events
 *     
 *   - Once the master receives the PROPAGATED messages from all nodes it
 *     x Updates its list of future events
 *     x Sends an AWAIT message to each node with new events to process on any of its nets
 *     x each AWAIT message contains the list {netId, numDrivers}
 *     x Once each node has awaited arrival of the signal events at the relevant nets, it sends an AWAITED
 *     x message back to the master.
 *     x When all the AWAITED messages have been received, the master sends an UPDATE message
 *     x to all the AWAITed nodes to cause them to update their models and schedule any new
 *     x events onto their output pins. Once all models have been updated the node sends an UPDATED message
 *     x Note that this may cause a previously scheduled event to be cancelled. In this case the model
 *     x must return a SignalType.CANCELLED event, which will cause the master to locate the event
 *     x at the relevant moment and decrement numDrivers, removing the event completely if numDrivers
 *     x falls to zero.
 *  
 *  Exchanges and Queues are assigned as follows:
 *  
 *  Exchange            Queues                     Binding    Comment
 *  ==================================================================================
 *  E_Broadcast         Q_Broadcast_<nodeId>       fanout      Used to send global control messages to the cluster
 *  
 *  E_Nets              Q_Net_<nodeId>             topic       Outputs send SCHEDULE messages to these queues
 *                                                             Nodes bind to each netId routing key
 *  
 *  E_Nodes             Q_Node_<nodeId>            topic       PROPAGATE[ moment, {netId}]
 *                                                             AWAIT[ moment, {netId, numDrivers} ]
 *                                                             UPDATE[ moment, {netId} ]
 *                                                             The master send MODEL messages to this queue to install models on nodes
 *  
 *  E_Acks              Q_Acks                     exclusive   Nodes send status messages back to the master
 *                                                             PROPAGATED[ {netId, numDrivers}, {netId, moment} ]
 *                                                             AWAITED[ nodeId ]
 *                                                             UPDATED[ nodeId ]
 *  
 *  
 * @author mcintyred
 *
 */
public class LocalNetlistDriver implements NetlistDriver {
	
	private Netlist netlist;
	
	private Set<Net> netsToUpdate = new HashSet<Net>();
	
	@Override
	public void setNetlist(Netlist netlist) {
		this.netlist = netlist;
	}
	
	@Override
	public void scheduleNetValue(String netId, ScheduledValue value) {
		
		for(InputPin pin : netlist.getNet(netId).getSinks()) {
			pin.scheduleInputValue(value.getMoment(), value.getValue());
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.co.ukmaker.netsim.simulation.INetlistDriver#propagate(long, uk.co.ukmaker.netsim.simulation.NetlistDriverCallbackHandler)
	 */
	@Override
	public Map<String, Set<Long>> initialiseModels() throws Exception {
		// Initialisation
		// Update all models first
		netsToUpdate.clear();
		netsToUpdate.addAll(netlist.getNets());
		return updateModels(0);
	}
	
	/* (non-Javadoc)
	 * @see uk.co.ukmaker.netsim.simulation.INetlistDriver#propagate(long, java.util.Collection, uk.co.ukmaker.netsim.simulation.NetlistDriverCallbackHandler)
	 */
	@Override
	public Map<String, Integer> propagateOutputs(long moment, Set<String> netIds, NetEventPropagator propagator) throws Exception {
		
		// Keep track of how many drivers propagated a signal onto each net so we can tell the master
		Map<String, Integer> netDrivers = new HashMap<String, Integer>();
		
		for(String netId : netIds) {
			
			Net n = netlist.getNet(netId);

			int drivers = 0;
			
			for(OutputPin p : n.getSources()) {
				
				// Get any events scheduled for now
				ScheduledValue v = p.useScheduledOutputValue(moment);
				if(v != null) {
					drivers++;
					// Send them to the net queue
					propagator.propagateOutput(n.getId(), v);
				}
			}
			
			if(drivers > 0) netDrivers.put(n.getId(), drivers);

		}
		
		return netDrivers;
	}
	
	/* (non-Javadoc)
	 * @see uk.co.ukmaker.netsim.simulation.INetlistDriver#await(long, java.util.Map, uk.co.ukmaker.netsim.simulation.NetlistDriverCallbackHandler)
	 */
	@Override
	public void propagateInputs(long moment, Map<String, Integer> netDrivers) throws Exception {
		
		netsToUpdate.clear();
		
		for(String netId : netDrivers.keySet()) {
			Net n = netlist.getNet(netId);
			n.await(moment, netDrivers.get(netId));
			netsToUpdate.add(n);
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.co.ukmaker.netsim.simulation.INetlistDriver#update(long, uk.co.ukmaker.netsim.simulation.NetlistDriverCallbackHandler)
	 */
	@Override
	public Map<String, Set<Long>> updateModels(long moment) throws Exception {

		// When is the next value scheduled on a net?
		Map<String, Set<Long>> nextValues = new HashMap<String, Set<Long>>();
		
		Set<Model> processedModels = new HashSet<Model>();
		
		for(Net n : netsToUpdate) {
			
			for(Model m : n.getModels()) {
				
				if(processedModels.contains(m)) {
					continue;
				}
				
				processedModels.add(m);
				m.update(moment);
				
				for(OutputPin p : m.getOutputPins()) {
					// When is the next new event scheduled for?
					ScheduledValue nextValue = p.useMostRecentValue();
					if(nextValue != null) {
						String pn = p.getNet().getId();
						if(nextValues.containsKey(pn)) {
							nextValues.get(pn).add(nextValue.getMoment());
						} else {
							nextValues.put(pn, new HashSet<Long>());
							nextValues.get(pn).add(nextValue.getMoment());
						}
					}
				}
			}
		}
		
		return nextValues;
	}
}
