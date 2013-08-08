package uk.co.ukmaker.netsim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.ukmaker.netsim.ports.Input;
import uk.co.ukmaker.netsim.ports.Output;

/**
 * A Circuit is a collection of Nets
 * @author duncan
 *
 */
public class Circuit extends Component {
	
	private List<Net> nets = new ArrayList<Net>();
	
	// Maintain a separate list of all known components
	private Set<Component> components = new HashSet<Component>();
	
	private long netId = 0;
	
	public Circuit(String name) {
		super(name);
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

		// tell all components to update themselves
		for(Component c : components) {
			c.propagateOutputEvents(moment);
		}
	}

	public Net addNet() {
		Net net = new Net("G_"+netId++);
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
}
