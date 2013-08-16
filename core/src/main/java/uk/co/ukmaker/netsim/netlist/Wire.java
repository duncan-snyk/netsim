package uk.co.ukmaker.netsim.netlist;

import java.util.ArrayList;
import java.util.List;


/**
 * Wires are used to connect Terminals together to describe a circuit
 * 
 * The compilation flattens this to a Net with Pins
 * 
 * @author duncan
 *
 */
public class Wire {
	
	private List<Terminal> terminals = new ArrayList<Terminal>();
	
	// Used by the compiler to mark wires which have been compiled to nets already
	private boolean netted = false;

	public List<Terminal> getTerminals() {
		return terminals;
	}
	
	public void addTerminal(Terminal t) {
		terminals.add(t);
	}
	
	public void setNetted(boolean netted) {
		this.netted = netted;
	}
	
	public boolean isNetted() {
		return netted;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WIRE { \n");
		for(Terminal t : getTerminals()) {
			sb.append(t.getPath());
			sb.append("\n");
		}
		sb.append("}\n");
		return sb.toString();
	}

}
