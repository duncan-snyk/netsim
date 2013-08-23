package uk.co.ukmaker.netsim.amqp.messages.node;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.Message;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.Pin;

/**
 * Sent from the Master to Nodes so that they can install Models from a simulation
 * 
 * @author mcintyred
 *
 */
public class InstallModelMessage implements Message {
	
	public static final String TYPE = "IM";

	private final int unitId;
	private final String name;
	private final String className;
	private final Map<String, String> pinToNetMap;
	
	public InstallModelMessage(String name, int unitId, String className, Map<String, String> pinToNetMap) {
		this.name = name;
		this.unitId = unitId;
		this.className = className;
		this.pinToNetMap = pinToNetMap;
	}

	public InstallModelMessage(Model m) {
		name = m.getName();
		unitId = m.getUnitId();
		className = m.getClass().getCanonicalName();
		pinToNetMap = new HashMap<String, String>();
		for(Pin p : m.getPins().values()) {
			pinToNetMap.put(p.getName(), p.getNet().getId());
		}
	}
	

	@Override
	public void populateHeaders(Map<String, Object> headers) {
		headers.put(TYPE_HEADER, TYPE);
	}

	public int getUnitId() {
		return unitId;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}

	public Map<String, String> getPinToNetMap() {
		return pinToNetMap;
	}
	
	@Override
	public byte[] getBytes() {
		StringBuilder sb = new StringBuilder();
		// Serialized format for now is:\
		// name\n
		// unitId\n
		// className\n
		// pinName:netName\n +
		sb.append(name);
		sb.append("\n");
		sb.append(unitId);
		sb.append("\n");
		sb.append(className);
		sb.append("\n");
		for(String key : pinToNetMap.keySet()) {
			sb.append(key);
			sb.append(":");
			sb.append(pinToNetMap.get(key));
			sb.append("\n");
		}
		return sb.toString().getBytes();
	}

	public static InstallModelMessage read(Map<String, Object> headers, byte[] bytes) {
		String[] lines = new String(bytes).split("\n");
		Map<String, String> pinToNetMap = new HashMap<String, String>();
		for(int i=3; i< lines.length; i++) {
			String[] bits = lines[i].split(":");
			pinToNetMap.put(bits[0],  bits[1]);
		}
		InstallModelMessage installModelMessage = new InstallModelMessage(
				lines[0], 
				Integer.parseInt(lines[1]), 
				lines[2], 
				pinToNetMap
				);
		
		return installModelMessage;
	}
}
