package uk.co.ukmaker.netsim.models.gates;

import uk.co.ukmaker.netsim.SignalValue;

public class NorGate extends TwoInputLogicGate {
	
	public NorGate(String name) {
		super(name);
	}
	
	public NorGate() {
		this("NOR");
	}
	
	public boolean fn(SignalValue av, SignalValue bv) {
		return !(av.isOne() || bv.isOne());
	}
}
