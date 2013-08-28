package uk.co.ukmaker.netsim.netlist;

import java.util.ArrayList;
import java.util.Collection;
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

	private Map<String, Net> netMap = new HashMap<String, Net>();
	
	private Map<Integer, Model> models = new HashMap<Integer, Model>();
	
	private List<TestProbe> probes = new ArrayList<TestProbe>();
	
	public void addNet(Net net) {
		netMap.put(net.getId(), net);
	}
	
	public void addModel(Model m) {
		models.put(m.getUnitId(), m);
		
		if(m instanceof TestProbe) {
			addTestProbe((TestProbe)m);
		}
	}
	
	public void addTestProbe(TestProbe probe) {
		probes.add(probe);
	}
	
	public List<TestProbe> getTestProbes() {
		return probes;
	}
	
	public List<Net> getNets() {
		return Lists.newArrayList(netMap.values());
	}
	
	public Collection<String> getNetNames() {
		return netMap.keySet();
	}
	
	public List<Model> getModels() {
		return Lists.newArrayList(models.values());
	}

	public Net getNet(String netId) {
		return netMap.get(netId);
	}
	
	public boolean hasNet(String netId) {
		return netMap.containsKey(netId);
	}

}
