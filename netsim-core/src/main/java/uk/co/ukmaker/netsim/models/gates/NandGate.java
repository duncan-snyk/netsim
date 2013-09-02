package uk.co.ukmaker.netsim.models.gates;

import uk.co.ukmaker.netsim.SignalValue;

public class NandGate extends TwoInputLogicGate {
	
	public NandGate(String name) {
		super(name);
	}
	
	public NandGate() {
		this("NAND");
	}
	
	public boolean fn(SignalValue av, SignalValue bv) {
		return !(av.isOne() && bv.isOne());
	}
}
