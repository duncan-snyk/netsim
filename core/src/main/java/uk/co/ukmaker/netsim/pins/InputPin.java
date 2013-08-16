package uk.co.ukmaker.netsim.pins;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;

public class InputPin extends Pin implements Input {
	
	private SignalValue value = SignalValue.X;

	public InputPin(final Model model, final String name) {
		super(model, name);
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
