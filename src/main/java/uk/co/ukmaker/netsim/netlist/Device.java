package uk.co.ukmaker.netsim.netlist;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Device implements Component {
	
	private String name;
	private String modelClass;
	private Map<String, Terminal> terminals  = new HashMap<String, Terminal>();
	
	public Device(String name, String modelClass) {
		this.name = name;
		this.modelClass = modelClass;
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
	
	public String getModelClass() {
		return modelClass;
	}
	
	public void addTerminal(Terminal t) {
		terminals.put(t.getName(), t);
	}
}
