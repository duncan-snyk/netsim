package uk.co.ukmaker.netsim.amqp.messages.node;

import java.util.HashMap;
import java.util.Map;

import uk.co.ukmaker.netsim.amqp.messages.NetsimMessage;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.pins.Pin;

/**
 * Sent from the Master to Nodes so that they can install Models from a simulation
 * 
 * @author mcintyred
 *
 */
public class InstallModelMessage implements NetsimMessage {
	
	public static final String TYPE = "IM";

	private  int unitId;
	private  String name;
	private  String className;
	private  Map<String, String> pinToNetMap;
	private  Map<String, String> parameters;
	
	public InstallModelMessage() {
	}
	
	public InstallModelMessage(String name, int unitId, String className, Map<String, String> pinToNetMap, Map<String, String> parameters) {
		this.name = name;
		this.unitId = unitId;
		this.className = className;
		this.pinToNetMap = pinToNetMap;
		this.parameters = parameters;
	}

	public InstallModelMessage(Model m) {
		name = m.getName();
		unitId = m.getUnitId();
		className = m.getClass().getCanonicalName();
		pinToNetMap = new HashMap<String, String>();
		for(Pin p : m.getPins().values()) {
			pinToNetMap.put(p.getName(), p.getNet().getId());
		}
		parameters = m.getParameters();
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
	
	public Map<String, String> getParameters() {
		return parameters;
	}
}
