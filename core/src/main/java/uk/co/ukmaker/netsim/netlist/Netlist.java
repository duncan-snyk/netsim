package uk.co.ukmaker.netsim.netlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.models.test.TestProbe;

/**
 * A Circuit is compiled into a netlist consisting solely
 * of Devices with their Pins connected together by Nets
 * @author duncan
 *
 */
public class Netlist {

	private Map<String, Net> nets = new HashMap<String, Net>();
	
	private List<Model> models = new ArrayList<Model>();
	
	private List<TestProbe> probes = new ArrayList<TestProbe>();
	
	public void addNet(Net net) {
		nets.put(net.getId(), net);
	}
	
	public void addModel(Model d) {
		models.add(d);
	}
	
	public void addTestProbe(TestProbe probe) {
		probes.add(probe);
	}
	
	public List<TestProbe> getTestProbes() {
		return probes;
	}
	
	public void update(long moment) {
		for(Model d : models) {
			d.update(moment);
		}
	}
	
	public List<Net> getNets() {
		return Lists.newArrayList(nets.values());
	}
	
	public List<Model> getModels() {
		return models;
	}

	public Net getNet(String netId) {
		return nets.get(netId);
	}

}
