package uk.co.ukmaker.netsim;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.ports.Port;

/**
 * A component encapsulates the behaviour of any kind of device
 * 
 * 
 * @author duncan
 *
 */
abstract public class Component {
	
	private static int u = 1;
	protected int unit;
	
	protected String name;
	
	protected Map<String, Port> ports = new HashMap<String, Port>();
	
	public Component(String name) {
		unit = u++;
		this.name = name;
	}
	
	protected void addPort(Port p) {
		ports.put(p.getName(), p);
	}
	
	public Map<String, Port> getPorts() {
		return ports;
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
	
	public String getName() {
		return name;
	}
	
	public String getUnitName() {
		return "U"+unit;
	}

}
