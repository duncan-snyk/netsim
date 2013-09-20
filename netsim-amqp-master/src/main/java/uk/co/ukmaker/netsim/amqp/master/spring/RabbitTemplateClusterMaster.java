package uk.co.ukmaker.netsim.amqp.master.spring;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import uk.co.ukmaker.netsim.amqp.Routing;
import uk.co.ukmaker.netsim.amqp.master.ClusterData;
import uk.co.ukmaker.netsim.amqp.master.ClusterMaster;
import uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage;
import uk.co.ukmaker.netsim.amqp.messages.discovery.EnumeratedMessage;

@Service
@Profile("template")
public class RabbitTemplateClusterMaster extends ClusterMaster {
	
	@Autowired
	private Routing routing;

	@Autowired
	private RabbitTemplate template;

	@Override
	public void initialize() throws Exception {
		
	}

	@Override
	public void broadcast(BroadcastMessage m) throws Exception {
		template.convertAndSend(m, new NetsimMessagePostProcessor(m));	
	}

	@Override
	public ClusterData readDiscoveryQueue() throws Exception {
		
		ClusterData data = new ClusterData();
		
		EnumeratedMessage m;

		do {
			Thread.sleep(routing.getDiscoveryTimeout());
			m = (EnumeratedMessage)template.receiveAndConvert(routing.getDiscoveryQueueName());
			if(m != null) {
				RabbitTemplateClusterNode node = new RabbitTemplateClusterNode(template, m.getName(), m.getRamSize());
				data.getNodes().add(node);
			}
		} while(m != null);
		
		return data;
	}

}
