package uk.co.ukmaker.netsim.ports;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.SignalValue;

public class IOPort extends OutputPort implements Input {
	
	private SignalValue inputValue;

	public IOPort(final Component component, final String name) {
		super(component, name);
	}


	@Override
	public SignalValue getInputValue() {
		return inputValue;
	}

	@Override
	public void setInputValue(long moment, SignalValue value) {
		// ignore moment for now. Maybe one day we'll try to model propagation delays
		this.inputValue = value;
	}
}
