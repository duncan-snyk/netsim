package uk.co.ukmaker.netsim.amqp.node;

import java.io.IOException;
import java.util.Collection;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.ukmaker.netsim.amqp.Routing;
import uk.co.ukmaker.netsim.amqp.messages.netlist.ScheduleNetValueMessage;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * In this implementation, all net update messages are routed to every node's netlist queue
 * It is up to the individual node to discard messages to nets they do not host
 * 
 * @author mcintyred
 *
 */
@Service
public class FilteredNetsListener implements NetsListener {

	
	@Autowired
	private ConnectionFactory connectionFactory;
	
	@Autowired 
	Routing routing;

	private Channel netsChannel;
	
	@Autowired
	private Node node;
	
	private Consumer netsCallback;
	
	private String netsQueueName;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public void initialise() throws IOException {
		
		netsQueueName = routing.getNetsQueueName(node.getName());
		
		netsChannel = connectionFactory.newConnection().createChannel();
		netsChannel.exchangeDeclare(routing.getNetsExchangeName(), "fanout");
		netsChannel.queueDeclare(netsQueueName, false, true, true, null);
		netsChannel.basicQos(1);
		System.out.println("Connecting to nets exchange as q "+netsQueueName);

		netsCallback = new DefaultConsumer(netsChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				try {
					onNetsMessage(properties, body);
				} catch (Exception e) {
					throw new RuntimeException("Cannot schedule net value", e);
				}
			}		
		};

		netsChannel.basicConsume(netsQueueName, true, netsCallback);
		
	}
	
	
	public void onNetsMessage(BasicProperties properties, byte[] bytes) throws Exception {
		ScheduleNetValueMessage m = mapper.readValue(bytes, ScheduleNetValueMessage.class);
		
		if(node.getNetlist().hasNet(m.getNetId())) {
			node.getNetlistDriver().scheduleNetValue(m.getNetId(), m.getValue());
		}
	}

	
	public void connectNets() throws IOException {
		// The nets queue is exclusive to this node
		// Bind it to the exchange with no routing key
		netsChannel.queueBind(netsQueueName, routing.getNetsExchangeName(), "");
		System.out.println("Binding to "+netsQueueName);
	}

}
