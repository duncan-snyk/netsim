package uk.co.ukmaker.netsim.models.gates;

import uk.co.ukmaker.netsim.SignalValue;

public class OrGate extends TwoInputLogicGate {
	
	public OrGate(String name) {
		super(name);
	}
	
	public OrGate() {
		this("OR");
	}
	
	public boolean fn(SignalValue av, SignalValue bv) {
		return av.isOne() || bv.isOne();
	}
}
