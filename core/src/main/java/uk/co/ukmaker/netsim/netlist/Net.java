package uk.co.ukmaker.netsim.netlist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.ukmaker.netsim.SignalValue;
import uk.co.ukmaker.netsim.models.Model;
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
	private Set<Model> sourceModels = new HashSet<Model>();
	private Set<Model> sinkModels = new HashSet<Model>();
	private Set<Model> models = new HashSet<Model>();
	
	public Net(final String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void addPin(Pin p) {
		
		if(p instanceof InputPin) {
			sinks.add((InputPin)p);
			sinkModels.add(p.getComponent());
		}
		
		if(p instanceof OutputPin) {
			sources.add((OutputPin)p);
			sourceModels.add(p.getComponent());
		}
		models.add(p.getComponent());
		p.setNet(this);
	}
	
	public List<InputPin> getSinks() {
		return sinks;
	}
	
	public List<OutputPin> getSources() {
		return sources;
	}
	
	public Set<Model> getSourceModels() {
		return sourceModels;
	}
	
	public Set<Model> getSinkModels() {
		return sinkModels;
	}
	
	public Set<Model> getModels() {
		return models;
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

	public void await(long moment, int numDrivers) {
		for(InputPin p : getSinks()) {
			p.await(moment, numDrivers);
		}
	}
}
