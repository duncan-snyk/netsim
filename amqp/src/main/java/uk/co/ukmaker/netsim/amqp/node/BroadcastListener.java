package uk.co.ukmaker.netsim.amqp.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.ukmaker.netsim.amqp.ClusterNode;
import uk.co.ukmaker.netsim.amqp.messages.ModelMessage;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.netlist.Net;
import uk.co.ukmaker.netsim.pins.Pin;

@Service
public class BroadcastListener {
	

	@Autowired
	private Node node;
	
	public String getName() throws UnknownHostException {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return InetAddress.getLocalHost().getHostAddress();
		}
	}
	
	public long getRamSize() {
		return Runtime.getRuntime().totalMemory();
	}

	public void installModel(ModelMessage modelMessage) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Model model = (Model)Class.forName(modelMessage.getClassName()).newInstance();
		String unitName = modelMessage.getUnitName();
		
		node.addModel(unitName, model);
		
		for(String pinName : modelMessage.getPinToNetMap().keySet()) {
			String netId = modelMessage.getPinToNetMap().get(pinName);
			node.addPin(unitName, netId, pinName);
		}
	}
	


}
