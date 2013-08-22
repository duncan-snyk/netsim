package uk.co.ukmaker.netsim.amqp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uk.co.ukmaker.netsim.amqp.messages.InitialiseMessage;
import uk.co.ukmaker.netsim.amqp.messages.Message;
import uk.co.ukmaker.netsim.amqp.messages.MessageFactory;
import uk.co.ukmaker.netsim.amqp.messages.ModelMessage;
import uk.co.ukmaker.netsim.amqp.responses.InitialiseResponse;
import uk.co.ukmaker.netsim.amqp.responses.Response;
import uk.co.ukmaker.netsim.amqp.responses.ResponseFactory;
import uk.co.ukmaker.netsim.models.Model;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;

public class ClusterNode {
	
	private final Channel channel;
	private final String exchangeName;
	private final String name;
	private final long ramSize;
	
	private final ExecutorService pool = Executors.newFixedThreadPool(4);
	
	public ClusterNode(Channel channel, String exchangeName, String name, long ramSize) {
		super();
		this.channel = channel;
		this.exchangeName = exchangeName;
		this.name = name;
		this.ramSize = ramSize;
	}
	
	public String getName() {
		return name;
	}

	public long getRamSize() {
		return ramSize;
	}


	@Override
	public String toString() {
		return name+':'+ramSize;
	}
	
	public void installModel(Model model) throws IOException {
		
		send(new ModelMessage(model));
	}
	
	public Future<Message> initialise() throws IOException {
		return sendAndReceive(new InitialiseMessage());
	}
	
	protected void send(Message m) throws IOException {
		String routingKey = getName();
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("MESSAGE_TYPE", m.getType());
		
		BasicProperties props = new BasicProperties.Builder()
		.headers(headers)
		.build();

		channel.basicPublish(exchangeName, routingKey, props, m.getBytes());
	}
	
	protected Future<Message> sendAndReceive(Message m) throws IOException {
		
		final String replyQueue = channel.queueDeclare().getQueue();
		
		String routingKey = getName();
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("MESSAGE_TYPE", m.getType());
		
		BasicProperties props = new BasicProperties.Builder()
		.replyTo(replyQueue)
		.headers(headers)
		.build();

		channel.basicPublish(exchangeName, routingKey, props, m.getBytes());
		// We need to return a Future, so fire up a Callable which loops
		// on basic.get until we've got the response
		
		final ClusterNode node = this;
		
		return pool.submit(new Callable<Message>() {

			@Override
			public Message call() throws Exception {
				GetResponse r;
				
				while((r = channel.basicGet(replyQueue, true)) == null) {
					// loop
				}
				
				return MessageFactory.decode(node, (String)r.getProps().getHeaders().get("MESSAGE_TYPE"), r.getBody());
				
			}
		});
	}
}