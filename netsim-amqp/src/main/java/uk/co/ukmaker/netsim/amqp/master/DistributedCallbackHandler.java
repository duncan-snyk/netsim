package uk.co.ukmaker.netsim.amqp.master;

import java.util.List;
import java.util.Map;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.simulation.NetEventPropagator;

public class DistributedCallbackHandler implements NetEventPropagator {

	@Override
	public void propagateOutput(String netId, ScheduledValue value)
			throws Exception {
		throw new RuntimeException("Not implemented");
	}

}
