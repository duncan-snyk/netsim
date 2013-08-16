package uk.co.ukmaker.netsim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.Output;
import uk.co.ukmaker.netsim.ports.Port;

/**
 * An Entity is a user-defined Component, usually configured by parsing a netlist
 * @author duncan
 *
 */
public class Entity extends Component {
	
	private List<Net> nets = new ArrayList<Net>();
	private long netId = 0;
	
	private static long entityId = 1;
	
	private long id = 0;
	
	// Maintain a separate list of all known components
	private Set<Component> components = new HashSet<Component>();

	public Entity(String name) {
		super(name);
		id = entityId++;
	}
	
	public Net addNet() {
		Net net = new Net(String.format("%s_%d_%d", name, id, netId++));
		nets.add(net);
		return net;
	}
	
	public List<Net> getNets() {
		return nets;
	}
	
	public void initialise() {
		// build the list of components
		components.clear();
		for(Net n : nets) {
			
			for(Input p : n.getSinks()) {
				if(p.getComponent() != null) components.add(p.getComponent());
			}
			
			for(Output p : n.getSources()) {
				if(p.getComponent() != null) components.add(p.getComponent());
			}
		}
	}

	@Override
	public void update(long moment) {
		// tell all components to update themselves
		for(Component c : components) {
			c.update(moment);
		}
	}

	@Override
	public void propagateOutputEvents(long moment) {
		// Now propagate the signals on all nets
		for(Net n : nets) {
			n.propagate(moment);
		}		
	}
	
	@Override
	public void addPort(Port p) {
		super.addPort(p);
	}

}
