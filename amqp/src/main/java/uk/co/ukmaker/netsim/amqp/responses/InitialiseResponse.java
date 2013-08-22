package uk.co.ukmaker.netsim.amqp.responses;

import uk.co.ukmaker.netsim.amqp.ClusterNode;

public class InitialiseResponse implements Response {

	private final boolean success;
	private final ClusterNode node;
	
	public InitialiseResponse(ClusterNode node, boolean success) {
		this.node = node;
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public ClusterNode getNode() {
		return node;
	}

	public static InitialiseResponse decode(ClusterNode node, byte[] data) {
		return new InitialiseResponse(node, Boolean.parseBoolean(new String(data)));
	}
}
