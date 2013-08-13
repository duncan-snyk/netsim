package uk.co.ukmaker.netsim.netlist;

import java.util.HashSet;
import java.util.Set;

import uk.co.ukmaker.netsim.Net;
import uk.co.ukmaker.netsim.Netlist;

/**
* The netlist compiler takes a Circuit and compiles it to a Netlist
* 
**/
public class Compiler {
	
	public Netlist compile(Circuit src) {
		
		Netlist netlist = new Netlist();
		
		// generate a single Net for each network of Nets connected by one or more Terminals
		// this will be grotesquely inefficient since we have to iterate over all terminals
		
		// Maintain a set of visited Nets
		Set<Net> visitedNets  = new HashSet<Net>();
		for(Terminal t : src.getTerminals().values()) {
			
		}
	}

}
