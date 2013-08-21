package uk.co.ukmaker.netsim.amqp.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.netlist.Net;
import uk.co.ukmaker.netsim.pins.Pin;

@Component
public class Node {
	
	private Map<String, Model> models = new HashMap<String, Model>();
	private Map<String, Net> nets = new HashMap<String, Net>();
	
	public Map<String, Model> getModels() {
		return models;
	}
	public void setModels(Map<String, Model> models) {
		this.models = models;
	}
	public Map<String, Net> getNets() {
		return nets;
	}
	public void setNets(Map<String, Net> nets) {
		this.nets = nets;
	}
	
	public void addModel(String unitName, Model model) {
		models.put(unitName, model);
	}
	
	public void addPin(String unitName, String netId, String pinName) {
		Net n = getNet(netId);
		Pin p = models.get(unitName).getPin(pinName);
		n.addPin(p);
	}

	public Net getNet(String netId) {
		if(nets.containsKey(netId)) {
			return nets.get(netId);
		}
		
		Net net = new Net(netId);
		nets.put(netId, net);
		
		return net;
	}
	
	public Collection<String> getNetNames() {
		return nets.keySet();
	}
}
