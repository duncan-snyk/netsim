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
	
	public void addTerminal(Terminal t) {
		terminals.add(t);
	}
	
	public List<Terminal> getTerminals() {
		return terminals;
	}

}
