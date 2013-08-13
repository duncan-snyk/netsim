package uk.co.ukmaker.netsim.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.ukmaker.netsim.netlist.Component;
import uk.co.ukmaker.netsim.netlist.Terminal;
import uk.co.ukmaker.netsim.pins.Pin;

/**
 * This class encapsulates the behaviour of any kind of device.
 * 
 * A model is the most primitive entity in the simulation.
 * 
 * It is connected to Nets via Pins
 * 
 * 
 * @author duncan
 *
 */
abstract public class Model {
	
	private static int u = 1;
	protected int unit;
	
	protected String name;
	
	protected Map<String, Pin> pins = new HashMap<String, Pin>();
	
	public Model(String name) {
		unit = u++;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	protected void addPin(Pin p) {
		pins.put(p.getName(), p);
	}
	
	public Map<String, Pin> getPins() {
		return pins;
	}
	
	/**
	 * Ask the Component to run its behaviour at the given moment
	 * 
	 * @param moment
	 */
	public abstract void update(long moment);
	
	/**
	 * Ask the Component to propagate any scheduled values to its outputs
	 * @return
	 */
	public abstract void propagateOutputEvents(long moment);
	
	
	public String getUnitName() {
		return "U"+unit;
	}

}
