package uk.co.ukmaker.netsim.amqp.master.spring;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import uk.co.ukmaker.netsim.amqp.master.ClusterNode;
import uk.co.ukmaker.netsim.amqp.messages.NetsimMessage;

public class RabbitTemplateClusterNode extends ClusterNode {
	
	private RabbitTemplate template;
	private final ExecutorService pool = Executors.newFixedThreadPool(40);
	
	
	public RabbitTemplateClusterNode(RabbitTemplate template, String name, long ramSize) {
		
		super(name, ramSize);
		
		this.template = template;
	}

	@Override
	protected void send(NetsimMessage m) throws Exception {
		String routingKey = getName();
		template.convertAndSend(routingKey, m, new NetsimMessagePostProcessor(m));
	}

	@Override
	protected Future<NetsimMessage> sendAndReceive(final NetsimMessage m) throws Exception {
		
		final String routingKey = getName();
		
		return pool.submit(new Callable<NetsimMessage>() {

			@Override
			public NetsimMessage call() throws Exception {
				return (NetsimMessage)template.convertSendAndReceive(routingKey, m, new NetsimMessagePostProcessor(m));
			}
			
		});
	}

}
