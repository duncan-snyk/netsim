package uk.co.ukmaker.netsim;

import java.util.ArrayList;
import java.util.List;

import uk.co.ukmaker.netsim.models.Model;

/**
 * A Circuit is compiled into a netlist consisting solely
 * of Devices with their Pins connected together by Nets
 * @author duncan
 *
 */
public class Netlist {
	
	private List<Net> nets = new ArrayList<Net>();
	
	private List<Model> models = new ArrayList<Model>();
	
	public void addNet(Net net) {
		nets.add(net);
	}
	
	public void addDevice(Model d) {
		models.add(d);
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

}
