package uk.co.ukmaker.netsim.amqp.master.rabbit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.codehaus.jackson.map.ObjectMapper;

import uk.co.ukmaker.netsim.amqp.master.ClusterNode;
import uk.co.ukmaker.netsim.amqp.messages.NetsimMessage;
import uk.co.ukmaker.netsim.amqp.messages.nodereply.NodeReplyMessageFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;

public class RabbitClusterNode extends ClusterNode {
	
	private final Channel channel;
	private final String exchangeName;
	private final String replyQueueName;
	
	private final ExecutorService pool = Executors.newFixedThreadPool(40);
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	public RabbitClusterNode(Channel channel, String exchangeName, String name, long ramSize) throws Exception {
		super(name, ramSize);
		this.channel = channel;
		this.exchangeName = exchangeName;
		this.replyQueueName  = channel.queueDeclare().getQueue();
	}
	
	@Override
	protected void send(NetsimMessage m) throws Exception {
		String routingKey = getName();
		
		Map<String, Object> headers = new HashMap<String, Object>();
		
		m.populateHeaders(headers);
		
		BasicProperties props = new BasicProperties.Builder()
		.headers(headers)
		.build();

		
		channel.basicPublish(exchangeName, routingKey, props, mapper.writeValueAsBytes(m));
	}
	
	@Override
	protected Future<NetsimMessage> sendAndReceive(NetsimMessage m) throws Exception {
		
		String routingKey = getName();
		
		Map<String, Object> headers = new HashMap<String, Object>();
		m.populateHeaders(headers);
		
		BasicProperties props = new BasicProperties.Builder()
		.replyTo(replyQueueName)
		.headers(headers)
		.build();

		channel.basicPublish(exchangeName, routingKey, props, mapper.writeValueAsBytes(m));
		// We need to return a Future, so fire up a Callable which loops
		// on basic.get until we've got the response
		
		final ClusterNode node = this;
		
		return pool.submit(new Callable<NetsimMessage>() {

			@Override
			public NetsimMessage call() throws Exception {
				GetResponse r;
				
				while((r = channel.basicGet(replyQueueName, true)) == null) {
					// loop
				}
								
				return NodeReplyMessageFactory.decode(r.getProps().getHeaders(), r.getBody());
				
			}
		});
	}
}