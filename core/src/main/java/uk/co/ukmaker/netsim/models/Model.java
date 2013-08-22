package uk.co.ukmaker.netsim.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.OutputPin;
import uk.co.ukmaker.netsim.pins.Pin;

/**
 * This class encapsulates the behaviour of any kind of device.
 * 
 * A model is the most primitive entity in the simulation.
 * 
 * It is connected to Nets via Pins
 * 
 * 
 * @author duncan
 *
 */
abstract public class Model {
	
	private static int u = 1;
	protected int unit;
	
	protected String name;
	
	protected Map<String, Pin> pins = new HashMap<String, Pin>();
	protected List<InputPin> inputs = new ArrayList<InputPin>();
	protected List<OutputPin> outputs = new ArrayList<OutputPin>();
	
	public Model(String name) {
		unit = u++;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	protected void addPin(Pin p) {
		pins.put(p.getName(), p);
		if(p instanceof InputPin) {
			inputs.add((InputPin)p);
		} else {
			outputs.add((OutputPin)p);
		}
	}
	
	public Map<String, Pin> getPins() {
		return pins;
	}
	
	public List<OutputPin> getOutputPins() {
		return outputs;
	}
	
	public List<InputPin> getInputPins() {
		return inputs;
	}
	
	/**
	 * Ask the Component to run its behaviour at the given moment
	 * 
	 * It should use its input values and may schedule new events on its output pins
	 * 
	 * @param moment
	 */
	public abstract void update(long moment);
	
	public void useInputValues(long moment) {
		for(InputPin p : inputs) {
			p.useInputValue(moment);
		}
	}
	
	public boolean needsUpdate(long moment) {
		
		boolean needsUpdate = false;
		
		for(InputPin p : inputs) {
			if(p.hasScheduledValue(moment)) {
				needsUpdate=true;
			}
			p.useInputValue(moment);
		}
		
		return needsUpdate;
	}
	
	public Long getNextScheduleMoment() {
		Long next = null;
		for(OutputPin p : outputs) {
			Long pnext = p.getNextScheduleMoment();
			if(next == null) {
				next = pnext;
			} else if(pnext != null && (pnext < next)) {
				next = pnext;
			}
		}
		
		return next;
	}
	
	public String getUnitName() {
		return "U"+unit;
	}

	public Pin getPin(String pinName) {
		return pins.get(pinName);
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s:%s [ ", getUnitName(), getName()));
		for(InputPin i : getInputPins()) {
			sb.append(String.format("->%s(%s) ", i.getName(), i.getInputValue()));
		}
		
		for(OutputPin o : getOutputPins()) {
			sb.append(String.format(" %s->(%s)", o.getName(), o.getOutputValue()));
		}
		
		sb.append("]\n");
		return sb.toString();
	}

}
