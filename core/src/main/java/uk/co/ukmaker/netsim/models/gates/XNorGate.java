package uk.co.ukmaker.netsim.models.gates;

import uk.co.ukmaker.netsim.SignalValue;

public class XNorGate extends TwoInputLogicGate {
	
	public XNorGate(String name) {
		super(name);
	}
	
	public XNorGate() {
		this("XNOR");
	}
	
	public boolean fn(SignalValue av, SignalValue bv) {
		return av.equals(bv);
	}
}
