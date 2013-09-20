package uk.co.ukmaker.netsim.amqp.node;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.ukmaker.netsim.amqp.Routing;
import uk.co.ukmaker.netsim.amqp.messages.netlist.ScheduleNetValueMessage;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class FilteredNetsListenerContainer implements NetsListener {
	

	
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
	
	@Autowired
	AmqpAdmin admin;
	
	@Override
	public void initialise() throws IOException {
		
		netsQueueName = routing.getNetsQueueName(node.getName());
		System.out.println("Connecting to nets exchange as q "+netsQueueName);
		admin.declareExchange(new FanoutExchange(routing.getNetsExchangeName()));
		admin.declareQueue(new Queue(netsQueueName));
		admin.declareBinding(new Binding(netsQueueName, DestinationType.QUEUE, routing.getNetsExchangeName(), "", null));

		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
		container.setQueueNames(netsQueueName);
		container.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				try {
					onNetsMessage(message.getBody());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		container.start();
		
	}
	
	
	public void onNetsMessage(byte[] bytes) throws Exception {
		ScheduleNetValueMessage m = mapper.readValue(bytes, ScheduleNetValueMessage.class);
		
		if(node.getNetlist().hasNet(m.getNetId())) {
			node.getNetlistDriver().scheduleNetValue(m.getNetId(), m.getValue());
		}
	}

	@Override
	public void connectNets() throws IOException {
		// The nets queue is exclusive to this node
		// Bind it to the exchange with no routing key
		System.out.println("Binding to "+netsQueueName);
	}

}
