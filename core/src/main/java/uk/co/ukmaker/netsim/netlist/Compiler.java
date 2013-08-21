package uk.co.ukmaker.netsim.netlist;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.models.test.TestProbe;
import uk.co.ukmaker.netsim.pins.Pin;

/**
* The netlist compiler takes a Simulation and compiles it to a Netlist
* 
**/
public class Compiler {
	
	private int netId = 1;
	
	private Map<Device, Model> models = new HashMap<Device, Model>();
	
	private Netlist netlist;
	
	public Netlist compile(Circuit circuit) throws CompilationException {
		
		netlist = new Netlist();
		
		// generate a single Net for each network of Nets connected by one or more Terminals
		// this will be grotesquely inefficient since we have to iterate over all terminals
		
		netlist(circuit);
		return netlist;
	}
	
	public void netlist(Component component) throws CompilationException {
		for(Terminal t : component.getTerminals()) {		
			netlist(t, null);
		}
		
		for(Component c : component.getComponents()) {
			netlist(c);
		}
	}
	
	public void netlist(Terminal t, Net net) throws CompilationException {
		if(t.isNetlisted()) {
			return;
		}
		
		t.setNetlisted(true);
		
		Component component = t.getComponent();
		if(net == null) {
			net = new Net(component.getPath()+"_"+t.getName());
			netlist.addNet(net);
		}
		
		
		if(component instanceof Device) {
			Wire w = t.getExternalWire();
			if(w == null) {
				throw new CompilationException("No external wire attached to terminal "+component.getPath()+"_"+t.getName());
			}

			Model m = getDeviceModel((Device)component);
			Pin p = m.getPins().get(t.getName());
			
			if(p.getNet() != null) {
				throw new CompilationException("Attempting to wire a pin twice");
			}
			net.addPin(p);

			// follow other terminals attached to this wire
			w.setNetted(true);
			netlist(w, net);

		} else {
			netlist(t.getExternalWire(), net);
			netlist(t.getInternalWire(), net);
		}
	}
	
	public void netlist(Wire w, Net net) throws CompilationException {
		if(w != null) {
			for(Terminal t : w.getTerminals()) {
				netlist(t, net);
			}
		}
	}
	
	public void warn(String s) {
		System.out.println("WARNING: "+s);
	}
	
	
	public Model getDeviceModel(Device d) {
		
		if(models.containsKey(d)) {
			return models.get(d);
		}
		
		Model model = d.getModel();
		models.put(d, model);
		netlist.addModel(model);
		
		if(model instanceof TestProbe) {
			netlist.addTestProbe((TestProbe)model);
		}
		
		return model;
	}
}
