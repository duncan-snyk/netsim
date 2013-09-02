package uk.co.ukmaker.netsim.models.gates;

import uk.co.ukmaker.netsim.SignalValue;

public class XorGate extends TwoInputLogicGate {
	
	public XorGate(String name) {
		super(name);
	}
	
	public XorGate() {
		this("XOR");
	}
	
	public boolean fn(SignalValue av, SignalValue bv) {
		return !av.equals(bv);
	}
}