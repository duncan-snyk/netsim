package uk.co.ukmaker.netsim;

import java.util.ArrayList;
import java.util.List;

import uk.co.ukmaker.netsim.pins.Input;
import uk.co.ukmaker.netsim.pins.InputPin;
import uk.co.ukmaker.netsim.pins.Output;
import uk.co.ukmaker.netsim.pins.OutputPin;
import uk.co.ukmaker.netsim.pins.Pin;

/**
 * A Net ties two or more ports together. It represents all the wires connectings the ports.
 * 
 * This model is probably insufficient should we ever want to start modelling transport delay.
 * 
 * @author duncan
 *
 */
public class Net {
	
	private final String id;
	
	private SignalValue v;
	
	private List<InputPin> sinks = new ArrayList<InputPin>();
	private List<OutputPin> sources = new ArrayList<OutputPin>();
	
	public Net(final String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void addPin(Pin p) {
		
		if(p instanceof InputPin) {
			sinks.add((InputPin)p);
		}
		
		if(p instanceof OutputPin) {
			sources.add((OutputPin)p);
		}
	}
	
	public List<InputPin> getSinks() {
		return sinks;
	}
	
	public List<OutputPin> getSources() {
		return sources;
	}
	
	/**
	 * Propagate signals from sources to sinks
	 * If a source has a scheduled event, then there is something to propagate,
	 * otherwise nothing need be done. Note that this implies that all sources
	 * *must* schedule an event at t=0 to kick-start the system
	 */
	public void propagate(long moment) {
		
		SignalValue sv = null;
		SignalValue toPropagate = null;
		
		if(sources.size() == 0) {
			toPropagate = SignalValue.X;
		} else {
			for(Output p : sources) {
				
				sv = p.getScheduledOutputValue(moment);
				p.propagateOutputValue(moment);
				
				if(sv != null) {
					
					if(toPropagate == null) {
						toPropagate = sv;
					} else if(toPropagate.isZ()) {
						toPropagate = sv;
					} else {
						toPropagate = SignalValue.X;
					}
				}
			}
		}
		
		if(toPropagate != null) {
			// Now apply the signal to all the sinks
			for(Input p : sinks) {
				p.setInputValue(moment, toPropagate);
			}
			
			v = toPropagate;
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("NET %s = %s \n", id, v));
		for(InputPin i : sinks) {
			sb.append(String.format("    ->%s\n", i.getName()));
		}
		for(OutputPin o : sources) {
			sb.append(String.format("    %s->\n", o.getName()));
		}
		
		return sb.toString();
	}
}
