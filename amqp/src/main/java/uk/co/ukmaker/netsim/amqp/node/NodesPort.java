package uk.co.ukmaker.netsim.amqp.node;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.amqp.messages.NodesMessage;
import uk.co.ukmaker.netsim.simulation.NetlistDriver;

@Service
public class NodesPort {
	
	@Autowired
	private NetlistDriver listener;
	
	
	
	
	public void handleMessage(NodesMessage message) {
		switch(message.getType()) {
		case AWAIT:
			
			break;
			
		case AWAIT_ALL:
			
			break;
			
		case UPDATE:
		}
	}
	
	public void sendScheduledValue(String nodeId, ScheduledValue value) {
		
	}

	public void sendPropagated(Map<String, Integer> netDrivers,
			Map<String, List<Long>> nextValues) {
		// TODO Auto-generated method stub
		
	}
	
	public void sendNodeReady() {
		
	}

}
