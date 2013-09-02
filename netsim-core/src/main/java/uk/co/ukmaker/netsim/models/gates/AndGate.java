package uk.co.ukmaker.netsim.models.gates;

import uk.co.ukmaker.netsim.SignalValue;

public class AndGate extends TwoInputLogicGate {
	
	public AndGate(String name) {
		super(name);
	}
	
	public AndGate() {
		this("AND");
	}
	
	public boolean fn(SignalValue av, SignalValue bv) {
		return av.isOne() && bv.isOne();
	}
}
