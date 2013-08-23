package uk.co.ukmaker.netsim.amqp;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.co.ukmaker.netsim.amqp.master.ClusterNode;
import uk.co.ukmaker.netsim.amqp.node.BroadcastListener;
import uk.co.ukmaker.netsim.amqp.node.Node;

@Service
public class Routing {

	@Value("${cluster.broadcast.exchange}")
	private String broadcastExchangeName;
	
	@Value("${cluster.broadcast.queue}")
	private String broadcastQueueName;
	
	@Value("${cluster.discovery.exchange}")
	private String discoveryExchangeName;
	
	@Value("${cluster.discovery.queue}")
	private String discoveryQueueName;
	
	private long discoveryTimeout = 1000L;
	
	@Value("${cluster.nodes.exchange}")
	private String nodesExchangeName;

	@Value("${cluster.nodes.nodeQueueBase}")
	private String nodeQueueBase;

	@Value("${cluster.nets.exchange}")
	private String netsExchangeName;

	@Value("${cluster.nets.netsQueueBase}")
	private String netsQueueBase;

	public String getBroadcastExchangeName() {
		return broadcastExchangeName;
	}

	public void setBroadcastExchangeName(String broadcastExchangeName) {
		this.broadcastExchangeName = broadcastExchangeName;
	}

	public String getBroadcastQueueName() {
		return broadcastQueueName;
	}

	public void setBroadcastQueueName(String broadcastQueueName) {
		this.broadcastQueueName = broadcastQueueName;
	}

	public String getDiscoveryExchangeName() {
		return discoveryExchangeName;
	}

	public void setDiscoveryExchangeName(String discoveryExchangeName) {
		this.discoveryExchangeName = discoveryExchangeName;
	}

	public String getDiscoveryQueueName() {
		return discoveryQueueName;
	}

	public void setDiscoveryQueueName(String discoveryQueueName) {
		this.discoveryQueueName = discoveryQueueName;
	}

	public long getDiscoveryTimeout() {
		return discoveryTimeout;
	}

	public void setDiscoveryTimeout(long discoveryTimeout) {
		this.discoveryTimeout = discoveryTimeout;
	}

	public String getNodesExchangeName() {
		return nodesExchangeName;
	}

	public void setNodesExchangeName(String nodesExchangeName) {
		this.nodesExchangeName = nodesExchangeName;
	}

	public String getNodeQueueBase() {
		return nodeQueueBase;
	}

	public void setNodeQueueBase(String nodeQueueBase) {
		this.nodeQueueBase = nodeQueueBase;
	}
	
	public String getNodeQueueName(Node node) {
		return getNodeQueueBase() +  node.getName();
	}

	public String getNodeRoutingKey(Node node) {
		return node.getName();
	}

	public String getNetsExchangeName() {
		return netsExchangeName;
	}

	public void setNetsExchangeName(String netsExchangeName) {
		this.netsExchangeName = netsExchangeName;
	}

	public String getNetsQueueBase() {
		return netsQueueBase;
	}

	public void setNetsQueueBase(String netsQueueBase) {
		this.netsQueueBase = netsQueueBase;
	}

	public String getNetsQueueName(ClusterNode node) throws UnknownHostException {
		return getNetsQueueBase() +  node.getName();
	}
	
}
