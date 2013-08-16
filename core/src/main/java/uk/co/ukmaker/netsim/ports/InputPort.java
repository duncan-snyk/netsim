package uk.co.ukmaker.netsim.ports;

import uk.co.ukmaker.netsim.Component;
import uk.co.ukmaker.netsim.SignalValue;

public class InputPort extends AbstractPort implements Input {
	
	private SignalValue value;

	public InputPort(final Component component, final String name) {
		super(component, name);
	}

	@Override
	public SignalValue getInputValue() {
		return value;
	}

	@Override
	public void setInputValue(long moment, SignalValue value) {
		// ignore moment for now. Maybe one day we'll try to model propagation delays
		this.value = value;
	}
}
