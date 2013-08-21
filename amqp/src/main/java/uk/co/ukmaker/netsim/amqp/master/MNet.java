package uk.co.ukmaker.netsim.amqp.master;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.netlist.Net;
import uk.co.ukmaker.netsim.pins.Pin;

public class MNet {
	
	private Net net;

	public boolean propagate(long moment, long seq) {
		
		// cause all sources to propagate any events
		// broadcast(PROPAGATE, moment.seq)
		
		// wait for all sources to confirm that they have propagated any scheduled events
		// confirmations say if there were any real changes scheduled, or just nops
		// boolean realChanges = waitAll(PROPAGATED, moment.tick)
		// return realChanges;
		
		return false;
		
	}
	
	public void broadcastPropagate(long moment, long seq) {
		// basicPublish(net.getNetId(), new Tick(moment, seq))
	}
	
	public boolean waitAll(long moment, long seq) {
		
		Map<String, Pin> awaiting = new HashMap<String, Pin>();
		
		boolean realChanges = false;
		
		for(Pin p : net.getSources()) {
			awaiting.put(p.getName(), p);
		}
		
		// loop draining messages from the queue until all pins have responded
		return false;
	}
	
	
}
