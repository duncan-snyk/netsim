package uk.co.ukmaker.netsim.simulation;

import java.util.List;

import uk.co.ukmaker.netsim.models.test.TestProbe;
import uk.co.ukmaker.netsim.netlist.Netlist;

public interface Simulator {
	
	public void simulate(Netlist netlist, long howLongFor, List<TestProbe> testProbes) throws Exception;

}
