package uk.co.ukmaker.netsim.netlist;

import java.util.Collection;



/**
 * A Component is either a Device (a placeholder for a Model) or a user-defined Circuit
 * 
 * @author duncan
 *
 */

public interface Component {
	
	public String getName();
	public Collection<Terminal> getTerminals();
	public Terminal getTerminal(String name);
	public void addTerminal(Terminal t);
}
