package uk.co.ukmaker.netsim.amqp.endpoints;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;


/**
 * Abstracts out an AMQP exchange to which messages are sent
 * 
 * @author mcintyred
 *
 */
public class Exchange {

	@Autowired
	private ConnectionFactory connectionFactory;
	
	private Channel channel;
	
	public Exchange(String name, String type) throws IOException {
	
		channel = connectionFactory.newConnection().createChannel();
		channel.exchangeDeclare(name, type);
    	channel.basicQos(1);
	}
	
	public Queue createQueue(String name) throws IOException {
		channel.queueDeclare(name, false, true, true, null);
		
		return new Queue(name, channel);
	}
	
}
