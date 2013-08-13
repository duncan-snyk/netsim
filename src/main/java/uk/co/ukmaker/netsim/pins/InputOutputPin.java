package uk.co.ukmaker.netsim.pins;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;

public class InputOutputPin extends OutputPin implements Input {
	
	private SignalValue inputValue;

	public InputOutputPin(final Model model, final String name) {
		super(model, name);
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
