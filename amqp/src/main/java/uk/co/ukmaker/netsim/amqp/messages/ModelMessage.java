package uk.co.ukmaker.netsim.amqp.messages;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.Pin;

/**
 * Sent from the Master to Nodes so that they can install Models from a simulation
 * 
 * @author mcintyred
 *
 */
public class ModelMessage {

	private final String unitName;
	private final String className;
	private final Map<String, String> pinToNetMap;
	
	public ModelMessage(String unitName, String className, Map<String, String> pinToNetMap) {
		this.unitName = unitName;
		this.className = className;
		this.pinToNetMap = pinToNetMap;
	}

	public ModelMessage(Model m) {
		unitName = m.getUnitName();
		className = m.getClass().getCanonicalName();
		pinToNetMap = new HashMap<String, String>();
		for(Pin p : m.getPins().values()) {
			pinToNetMap.put(p.getName(), p.getNet().getId());
		}
	}
	
	public String getUnitName() {
		return unitName;
	}

	public String getClassName() {
		return className;
	}

	public Map<String, String> getPinToNetMap() {
		return pinToNetMap;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		// Serialized format for now is:\
		// unitName\n
		// className\n
		// pinName:netName\n +
		sb.append(unitName);
		sb.append("\n");
		sb.append(className);
		sb.append("\n");
		for(String key : pinToNetMap.keySet()) {
			sb.append(key);
			sb.append(":");
			sb.append(pinToNetMap.get(key));
			sb.append("\n");
		}
		return sb.toString();
	}

	public static ModelMessage parse(String message) {
		String[] lines = message.split("\n");
		Map<String, String> pinToNetMap = new HashMap<String, String>();
		for(int i=2; i< lines.length; i++) {
			String[] bits = lines[i].split(":");
			pinToNetMap.put(bits[0],  bits[1]);
		}
		ModelMessage modelMessage = new ModelMessage(lines[0], lines[1], pinToNetMap);
		
		return modelMessage;
	}

}
