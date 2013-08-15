package uk.co.ukmaker.netsim.netlist;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.Pin;


public class Device<T extends Model> implements Component {
	
	private Component parentComponent;
	private String name;
	private T model;
	private Map<String, Terminal> terminals  = new HashMap<String, Terminal>();
	
	public Device(Component parentComponent, String name, T model) {
		this.parentComponent = parentComponent;
		this.name = name;
		this.model = model;
		
		for(Pin p : model.getPins().values()) {
			if(p instanceof InputPin) {
				addTerminal(new Terminal(this, p.getName(), Terminal.Type.INPUT));
			} else {
				addTerminal(new Terminal(this, p.getName(), Terminal.Type.OUTPUT));
			}
		}
	}

	public Device(String name, T model) {
		this(null, name, model);
	}
	
	public Device(T model) {
		this(null, model.getName(), model);
	}
	
	@Override
	public void setParentComponent(Component parent) {
		parentComponent = parent;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getPath() {
		if(parentComponent == null) {
			return "/"+name;
		}
		
		return parentComponent.getName()+"/"+name;
	}

	@Override
	public Collection<Terminal> getTerminals() {
		return terminals.values();
	}

	@Override
	public Terminal getTerminal(String name) throws Exception {
		if(!terminals.containsKey(name)) {
			throw new Exception("Device "+getPath()+" contains no terminal named "+name);
		}
		return terminals.get(name);
	}
	
	@Override
	public List<Component> getComponents() {
		return Lists.newArrayList();
	}
	
	public T getModel() {
		return model;
	}
	
	public void addTerminal(Terminal t) {
		terminals.put(t.getName(), t);
	}
	
	public String toString() {
		return getPath()+"[ "+model.getClass().getName()+"]";
	}
}
