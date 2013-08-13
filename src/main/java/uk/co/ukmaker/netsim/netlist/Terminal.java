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
	
	protected List<Net> nets = new ArrayList<Net>();
	
	private String name;
	
	protected Type type;
	
	public Terminal(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Net> getNets() {
		return nets;
	}
	
	public void connect(Net net) {
		nets.add(net);
	}
	
	public Type getType() {
		return type;
	}

}
