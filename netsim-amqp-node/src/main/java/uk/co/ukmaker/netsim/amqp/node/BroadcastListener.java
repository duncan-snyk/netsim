package uk.co.ukmaker.netsim.amqp.node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.ukmaker.netsim.amqp.Routing;
import uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage;
import uk.co.ukmaker.netsim.amqp.messages.discovery.EnumeratedMessage;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * Listens to messages coming in on the broadcast queue
 * 
 * @author mcintyred
 *
 */
@Service
public class BroadcastListener {
	
	@Autowired 
	Routing routing;
	
	@Autowired
	private ConnectionFactory connectionFactory;
	private Channel broadcastChannel;
	private Channel discoveryChannel;
	
	@Autowired
	private Node node;
	
	private NetsListener netsListener;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public void setNetsListener(NetsListener listener) {
		netsListener = listener;
	}
	
	public void initialise() throws Exception {
		
		broadcastChannel = connectionFactory.createConnection().createChannel(false);
		broadcastChannel.queueDeclare(routing.getBroadcastQueueName(node.getName()), false, false, false, null);
		broadcastChannel.queueBind(routing.getBroadcastQueueName(node.getName()), routing.getBroadcastExchangeName(), "");
		//broadcastChannel.basicQos(1);
		
		System.out.println("Connecting to broadcast exchange as q "+routing.getBroadcastQueueName(node.getName()));
		
		discoveryChannel = connectionFactory.createConnection().createChannel(false);
		discoveryChannel.exchangeDeclare(routing.getDiscoveryExchangeName(), "direct");
		//discoveryChannel.basicQos(1);
		
		Consumer broadcastCallback = new DefaultConsumer(broadcastChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				try {
					onBroadcastMessage(properties, body);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		};

		broadcastChannel.basicConsume(routing.getBroadcastQueueName(node.getName()), true, broadcastCallback);
	}
	

	public void onBroadcastMessage(BasicProperties properties, byte[] body) throws Exception {
		
		BroadcastMessage cm = mapper.readValue(body, BroadcastMessage.class);
		
		switch(cm.getType()) {
		
		case ENUMERATE:
			enumerate();
			break;

		case CLEAR:
			clear();
			break;
			
		case RESET:
			reset();
			break;
			
		case CONNECT_NETS:
			netsListener.connectNets();
			break;
			
		default:
			break;
			
		}
	}
	
	public void enumerate() throws IOException {
		
		EnumeratedMessage m = new EnumeratedMessage(node.getName(), node.getRamSize());
		
		Map<String, Object> headers = new HashMap<String, Object>();
		m.populateHeaders(headers);
		
		BasicProperties props = new BasicProperties.Builder().headers(headers).build();
		
		discoveryChannel.basicPublish(routing.getDiscoveryExchangeName(), "", props, mapper.writeValueAsBytes(m));
	}
	
	public void clear() throws Exception {
		node.clear();
	}
	
	public void reset() throws Exception {
		node.reset();
	}

}
