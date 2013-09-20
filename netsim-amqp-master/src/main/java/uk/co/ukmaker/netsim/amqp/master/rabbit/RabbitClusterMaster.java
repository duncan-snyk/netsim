package uk.co.ukmaker.netsim.amqp.master.rabbit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;

import uk.co.ukmaker.netsim.amqp.Routing;
import uk.co.ukmaker.netsim.amqp.master.ClusterData;
import uk.co.ukmaker.netsim.amqp.master.ClusterMaster;
import uk.co.ukmaker.netsim.amqp.master.DistributedNetlistDriver;
import uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage;
import uk.co.ukmaker.netsim.amqp.messages.discovery.EnumeratedMessage;
import uk.co.ukmaker.netsim.amqp.node.RemoteNode;
import uk.co.ukmaker.netsim.simulation.Simulator;

@Service
@Profile("rabbit")
public class RabbitClusterMaster extends ClusterMaster {
	

	@Autowired
	Routing routing;
	
	@Autowired
	ConnectionFactory connectionFactory;
	
	@Autowired
	RemoteNode localNode;
	
	Channel broadcastChannel;
	Channel discoveryChannel;
	Channel nodeChannel;
	
	QueueingConsumer discoveryConsumer;
	
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void initialize() throws Exception {
		
		broadcastChannel = connectionFactory.createConnection().createChannel(false);
		broadcastChannel.exchangeDeclare(routing.getBroadcastExchangeName(), "fanout");
		//broadcastChannel.basicQos(1);
		
		discoveryChannel = connectionFactory.createConnection().createChannel(false);
		discoveryChannel.exchangeDeclare(routing.getDiscoveryExchangeName(), "direct");
		discoveryChannel.queueDeclare(routing.getDiscoveryQueueName(), false, false, false, null);
		discoveryChannel.queueBind(routing.getDiscoveryQueueName(), routing.getDiscoveryExchangeName(), "");
		//discoveryChannel.basicQos(1);
		
		nodeChannel = connectionFactory.createConnection().createChannel(false);
		nodeChannel.exchangeDeclare(routing.getNodesExchangeName(), "direct");
		//nodeChannel.basicQos(1);
		

		discoveryConsumer = new QueueingConsumer(discoveryChannel);
		discoveryChannel.basicConsume(routing.getDiscoveryQueueName(), true, discoveryConsumer);
		
		localNode.getNode().setName("node-master");
		localNode.initialise();	}

	@Override
	public void broadcast(BroadcastMessage message) throws Exception {
		Map<String, Object> headers = new HashMap<String, Object>();
		
		message.populateHeaders(headers);
		
		BasicProperties props = new BasicProperties.Builder()
		.headers(headers)
		.build();

		byte[] bytes = mapper.writeValueAsBytes(message);
		
		broadcastChannel.basicPublish(routing.getBroadcastExchangeName(), "", props, bytes);
	}

	@Override
	public ClusterData readDiscoveryQueue() throws Exception {
		
		ClusterData data = new ClusterData();
		
		// Now consume everything available on the discoveryChannel
		
		// crufty. Discovery gives the nodes one second to reply
		QueueingConsumer.Delivery delivery;
		while((delivery = discoveryConsumer.nextDelivery(routing.getDiscoveryTimeout())) != null) {
			RabbitClusterNode node = readClusterNode(delivery.getBody());
			data.getNodes().add(node);
		}
		
		return data;
	}
	
	
	// Deserialize from wire format which is "name:ramSize"
	public RabbitClusterNode readClusterNode(byte[] bytes) throws Exception {
		
		EnumeratedMessage m = mapper.readValue(bytes, EnumeratedMessage.class);
		return new RabbitClusterNode(nodeChannel, routing.getNodesExchangeName(), m.getName(), m.getRamSize());
		
	}

}
