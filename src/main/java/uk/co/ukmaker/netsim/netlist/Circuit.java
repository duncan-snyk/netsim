package uk.co.ukmaker.netsim.netlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.Output;

/**
 * A Circuit is a user-defined Component built from a netlist of Components
 * 
 * @author duncan
 *
 */
public class Circuit implements Component {
	
	private String name;
	private Map<String, Terminal> terminals = new HashMap<String, Terminal>();
	private List<Component> components = new ArrayList<Component>();
	
	public Circuit(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<Terminal> getTerminals() {
		return terminals.values();
	}
	
	@Override
	public Terminal getTerminal(String name) {
		return terminals.get(name);
	}
	
	public void addTerminal(Terminal t) {
		terminals.put(t.getName(), t);
	}
	
	public Collection<Component> getComponents() {
		return components;
	}
	
	public void addComponent(Component c) {
		components.add(c);
	}
}
