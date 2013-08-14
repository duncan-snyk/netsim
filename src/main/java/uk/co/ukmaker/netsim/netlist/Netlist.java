package uk.co.ukmaker.netsim.netlist;

import java.util.ArrayList;
import java.util.List;

import uk.co.ukmaker.netsim.Net;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.models.test.TestProbe;

/**
 * A Circuit is compiled into a netlist consisting solely
 * of Devices with their Pins connected together by Nets
 * @author duncan
 *
 */
public class Netlist {
	
	private List<Net> nets = new ArrayList<Net>();
	
	private List<Model> models = new ArrayList<Model>();
	
	private List<TestProbe> probes = new ArrayList<TestProbe>();
	
	public void addNet(Net net) {
		nets.add(net);
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
	
	public void propagateOutputEvents(long moment) {
		for(Net n : nets) {
			n.propagate(moment);
		}
	}
	
	public void update(long moment) {
		for(Model d : models) {
			d.update(moment);
		}
	}
	
	public List<Net> getNets() {
		return nets;
	}
	
	public List<Model> getModels() {
		return models;
	}

}
