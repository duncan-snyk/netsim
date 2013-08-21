package uk.co.ukmaker.netsim.amqp.node;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

import uk.co.ukmaker.netsim.amqp.ClusterNode;
import uk.co.ukmaker.netsim.amqp.Routing;
import uk.co.ukmaker.netsim.amqp.messages.ClusterMessage;
import uk.co.ukmaker.netsim.amqp.messages.ModelMessage;

@Service
public class BroadcastPort {
	
	@Autowired 
	Routing routing;
	
	@Autowired
	private ConnectionFactory connectionFactory;
	private Channel broadcastChannel;
	private Channel discoveryChannel;
	private Channel nodeChannel;
	private Channel netsChannel;
	
	@Autowired
	private BroadcastListener listener;
	
	@Autowired
	private Node node;
	
	private ClusterNode clusterNode;

	private Consumer broadcastCallback; 
	private Consumer nodeCallback;
	private Consumer netsCallback;
	
	private String nodeQueueName;
	private String netsQueueName;

	public void setListener(BroadcastListener node) {
		this.listener = node;
	}

	public void initialize() throws IOException {
		
		clusterNode = new ClusterNode(listener.getName(), listener.getRamSize());
		
		nodeQueueName = routing.getNodeQueueName(clusterNode);
		netsQueueName = routing.getNetsQueueName(clusterNode);
		
		broadcastChannel = connectionFactory.newConnection().createChannel();
		broadcastChannel.queueDeclare(routing.getBroadcastQueueName(), false, false, false, null);
		broadcastChannel.basicQos(1);
		
		discoveryChannel = connectionFactory.newConnection().createChannel();
		discoveryChannel.exchangeDeclare(routing.getDiscoveryExchangeName(), "direct");
		discoveryChannel.basicQos(1);
		
		netsChannel = connectionFactory.newConnection().createChannel();
		netsChannel.exchangeDeclare(routing.getNetsExchangeName(), "topic");
		netsChannel.queueDeclare(netsQueueName, false, true, true, null);
		netsChannel.basicQos(1);
		
		nodeChannel = connectionFactory.newConnection().createChannel();
		nodeChannel.exchangeDeclare(routing.getNodesExchangeName(), "direct");
		nodeChannel.queueDeclare(nodeQueueName, false, true, true, null);
		nodeChannel.queueBind(nodeQueueName, routing.getNodesExchangeName(), routing.getNodeRoutingKey(clusterNode), null);
		nodeChannel.basicQos(1);

		
		broadcastCallback = new DefaultConsumer(broadcastChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				
				String message = new String(body);
				
				System.out.println("Processing broadcast message: "+message);
				onBroadcastMessage(message);
			}		
		};

		broadcastChannel.basicConsume(routing.getBroadcastQueueName(), true, broadcastCallback);
		
		nodeCallback = new DefaultConsumer(nodeChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				
				String message = new String(body);
				
				System.out.println("Processing node message: "+message);
				try {
					onNodeMessage(message);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		};

		nodeChannel.basicConsume(nodeQueueName, true, nodeCallback);
		
		netsCallback = new DefaultConsumer(netsChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				
				String message = new String(body);
				
				System.out.println("Processing net message: "+message);
				onNetsMessage(message);
			}		
		};

		netsChannel.basicConsume(netsQueueName, true, netsCallback);
		
	}

	public void onBroadcastMessage(String message) throws IOException {
		ClusterMessage cm = ClusterMessage.read(message);
		switch(cm) {
		case ENUMERATE:
			sendEnumeration(new ClusterNode(listener.getName(), listener.getRamSize()));
			break;
			
		case CONNECT_NETS:
			connectNets();
			break;
			
		}
	}
	
	public void sendEnumeration(ClusterNode node) throws IOException {
		
		BasicProperties props = new BasicProperties.Builder().build();
		
		discoveryChannel.basicPublish(routing.getDiscoveryExchangeName(), "", props, node.toString().getBytes());
		
	}
	
	public void onNodeMessage(String message) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		ModelMessage modelMessage = ModelMessage.parse(message);
		listener.installModel(modelMessage);
	}
	
	public void connectNets() throws IOException {
		Collection<String> netNames = node.getNetNames();
		// The nets queue is exclusive to this node
		// Bind it to the exchange using the netIds as the routing keys
		for(String netId : netNames) {
			netsChannel.queueBind(netsQueueName, routing.getNetsExchangeName(), netId);
		}
	}
	
	public void onNetsMessage(String message) {
		
	}

}
