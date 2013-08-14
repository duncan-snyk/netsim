package uk.co.ukmaker.netsim.netlist;

import java.util.ArrayList;
import java.util.List;

import uk.co.ukmaker.netsim.Net;

/**
 * A base class for anything which can be attached to a Net
 * 
 * @author duncan
 *
 */
public class Terminal {
	
	public enum Type {
		INPUT,
		OUTPUT,
		IO
	}
	
	protected Wire internalWire;
	protected Wire externalWire;
	
	private String name;
	
	protected Type type;
	
	protected Component component;
	
	protected boolean netlisted = false;
	
	public Terminal(Component component, String name, Type type) {
		this.component = component;
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public Component getComponent() {
		return component;
	}

	public Wire getInternalWire() {
		return internalWire;
	}

	public void setInternalWire(Wire internalWire) {
		this.internalWire = internalWire;
		internalWire.addTerminal(this);
	}

	public Wire getExternalWire() {
		return externalWire;
	}

	public void setExternalWire(Wire externalWire) {
		this.externalWire = externalWire;
		externalWire.addTerminal(this);
	}

	public boolean isNetlisted() {
		return netlisted;
	}

	public void setNetlisted(boolean netlisted) {
		this.netlisted = netlisted;
	}
	
	public String getPath() {
		return component.getPath()+"_"+name;
	}
	
	public String toString() {
		return getPath();
	}

}
