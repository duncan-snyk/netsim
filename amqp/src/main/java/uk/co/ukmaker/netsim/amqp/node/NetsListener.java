package uk.co.ukmaker.netsim.amqp.node;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.ukmaker.netsim.amqp.Routing;
import uk.co.ukmaker.netsim.amqp.messages.netlist.ScheduleNetValueMessage;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

@Service
public class NetsListener {
	
	@Autowired
	private ConnectionFactory connectionFactory;
	
	@Autowired 
	Routing routing;

	private Channel netsChannel;
	
	@Autowired
	private Node node;
	
	private Consumer netsCallback;
	
	private String netsQueueName;
	
	public void initialise() throws IOException {
		
		netsQueueName = routing.getNetsQueueName(node);
		
		netsChannel = connectionFactory.newConnection().createChannel();
		netsChannel.exchangeDeclare(routing.getNetsExchangeName(), "topic");
		netsChannel.queueDeclare(netsQueueName, false, true, true, null);
		netsChannel.basicQos(1);
		
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
		ScheduleNetValueMessage m = ScheduleNetValueMessage.read(properties.getHeaders(), bytes);
		
		node.getNetlistDriver().scheduleNetValue(m.getNetId(), m.getValue());
	}

	
	public void connectNets() throws IOException {
		Collection<String> netNames = node.getNetlist().getNetNames();
		// The nets queue is exclusive to this node
		// Bind it to the exchange using the netIds as the routing keys
		for(String netId : netNames) {
			netsChannel.queueBind(netsQueueName, routing.getNetsExchangeName(), netId);
		}
	}

}
