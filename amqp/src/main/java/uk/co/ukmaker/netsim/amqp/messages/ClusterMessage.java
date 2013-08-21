package uk.co.ukmaker.netsim.amqp.messages;

public enum ClusterMessage {
	
	CLEAR,
	ENUMERATE,
	RESET,
	INSTALL_MODEL,
	CONNECT_NETS
	;
	
	public String toString() {
		switch(this) {
		case CLEAR: return "CLEAR";
		case ENUMERATE: return "ENUMERATE";
		case INSTALL_MODEL: return "INSTALL_MODEL";
		case RESET: return "RESET";
		case CONNECT_NETS: return "CONNECT_NETS";
		default:
			throw new RuntimeException("Illegal enum value");
		}
	}
	
	public static ClusterMessage read(String serialized) {
		if("CLEAR".equals(serialized)) {
			return CLEAR;
		}
		
		if("ENUMERATE".equals(serialized)) {
			return ENUMERATE;
		}
		
		if("INSTALL_MODEL".equals(serialized)) {
			return INSTALL_MODEL;
		}
		
		if("RESET".equals(serialized)) {
			return RESET;
		}
		
		if("CONNECT_NETS".equals(serialized)) {
			return CONNECT_NETS;
		}
		
		throw new RuntimeException("Cannot deserialize value "+serialized);
	}
}
