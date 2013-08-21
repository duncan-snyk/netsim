package uk.co.ukmaker.netsim.amqp.endpoints;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;


public class Queue {
	
	private String name;
	
	private Channel channel;
	
	public Queue(String name, Channel channel) {
		this.name = name;
		this.channel = channel;
	}
	
	public void consume(Consumer consumer) throws IOException {
		channel.basicConsume(name, true, consumer);
	}

}
