package uk.co.ukmaker.netsim.amqp;

public class ClusterNode {
	
	private final String name;
	private final long ramSize;
	
	
	
	public ClusterNode(String name, long ramSize) {
		super();
		this.name = name;
		this.ramSize = ramSize;
	}
	
	public String getName() {
		return name;
	}

	public long getRamSize() {
		return ramSize;
	}

	
	// Deserialize from wire format which is "name:ramSize"
	public static ClusterNode read(String serialized) {
		String[]  bits = serialized.split(":");
		return new ClusterNode(bits[0], Integer.parseInt(bits[1]));
		
	}

	@Override
	public String toString() {
		return name+':'+ramSize;
	}
}