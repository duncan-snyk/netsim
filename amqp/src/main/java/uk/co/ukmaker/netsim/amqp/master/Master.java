package uk.co.ukmaker.netsim.amqp.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import uk.co.ukmaker.netsim.amqp.ClusterData;
import uk.co.ukmaker.netsim.amqp.ClusterNode;
import uk.co.ukmaker.netsim.amqp.Routing;
import uk.co.ukmaker.netsim.amqp.messages.ClusterMessage;
import uk.co.ukmaker.netsim.amqp.messages.ModelMessage;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.netlist.Netlist;
import uk.co.ukmaker.netsim.netlist.ParserTest;
import uk.co.ukmaker.netsim.netlist.TestFixture;
import uk.co.ukmaker.netsim.parser.Parser;
import uk.co.ukmaker.netsim.simulation.LocalSimulatorNode;

/**
 * A cluster Master is responsible for:
 * 
 * 1. Loading and compiling a netlist
 * 2. Discovering cluster Nodes
 * 3. Creating queues for each Net
 * 4. Distributing Models to the Nodes
 * 5. Sending clock events
 * @author duncan
 *
 */
@Service
public class Master {
	

	@Autowired
	Routing routing;
	
	@Autowired
	ConnectionFactory connectionFactory;
	
	Channel broadcastChannel;
	Channel discoveryChannel;
	Channel nodeChannel;
	
	QueueingConsumer consumer;
	
	private ClusterData cluster;
	
	private Netlist netlist;
	
	private LocalSimulatorNode simulation;
	
	public void initialize() throws IOException {
		
		broadcastChannel = connectionFactory.newConnection().createChannel();
		broadcastChannel.exchangeDeclare(routing.getBroadcastExchangeName(), "fanout");
		broadcastChannel.queueDeclare(routing.getBroadcastQueueName(), false, false, false, null);
		broadcastChannel.queueBind(routing.getBroadcastQueueName(), routing.getBroadcastExchangeName(), "");
		broadcastChannel.basicQos(1);
		
		discoveryChannel = connectionFactory.newConnection().createChannel();
		discoveryChannel.exchangeDeclare(routing.getDiscoveryExchangeName(), "direct");
		discoveryChannel.queueDeclare(routing.getDiscoveryQueueName(), false, false, false, null);
		discoveryChannel.queueBind(routing.getDiscoveryQueueName(), routing.getDiscoveryExchangeName(), "");
		discoveryChannel.basicQos(1);
		
		nodeChannel = connectionFactory.newConnection().createChannel();
		nodeChannel.exchangeDeclare(routing.getNodesExchangeName(), "direct");
		nodeChannel.basicQos(1);
		

		consumer = new QueueingConsumer(discoveryChannel);
		discoveryChannel.basicConsume(routing.getDiscoveryQueueName(), true, consumer);
	}
	
	public LocalSimulatorNode loadSimulation(String filename) throws Exception {
		File f = new File(filename);
		FileInputStream netlist = new FileInputStream(f);
		
		Parser p = new Parser();
		p.setBaseDir(f.getParentFile());
		p.parse(netlist);
		
		TestFixture testFixture = (TestFixture)p.getEntity();
		
		simulation = new LocalSimulatorNode(testFixture);
		
		return simulation;
//		sim.simulate(testFixture.getEndMoment());
	}
	
	public void installModels() throws IOException {
		Netlist netlist = simulation.getNetlist();
		List<Model> models = netlist.getModels();
		// give each node a fair proportion of the models
		// we really ought to hae some way of weighting things
		// as a function of e.g. memory used, but hey
		int modelsPerNode = models.size() / cluster.getNodes().size();
		int leftovers = models.size() - (modelsPerNode * cluster.getNodes().size());
		
		int installed = 0;
		
		for(ClusterNode node : cluster.getNodes()) {
			for(int i=0; i<modelsPerNode; i++) {
				Model m = models.get(installed++);
				installModel(node, m);
			}
		}
		
		for(ClusterNode node : cluster.getNodes()) {
			for(int i=0; i<leftovers; i++) {
				Model m = models.get(installed++);
				installModel(node, m);
			}
		}
	}
	
	public ClusterData discoverNodes() throws Exception {
		
		cluster = new ClusterData();
		cluster.setState(ClusterData.State.ENUMERATING);
		
		broadcast(ClusterMessage.ENUMERATE);
		
		// Now consume everything available on the discoveryChannel
		
		// crufty. Discovery gives the nodes one second to reply
		QueueingConsumer.Delivery delivery;
		while((delivery = consumer.nextDelivery(routing.getDiscoveryTimeout())) != null) {
			ClusterNode node = ClusterNode.read(new String(delivery.getBody()));
			cluster.getNodes().add(node);
		}
		
		cluster.setState(ClusterData.State.ENUMERATED);
		
		return cluster;
	}
	
	public void clearAll() throws Exception {
		broadcast(ClusterMessage.CLEAR);
	}
	
	public void resetAll() throws Exception {
		broadcast(ClusterMessage.RESET);
	}
	
	public void connectNets() throws Exception {
		broadcast(ClusterMessage.CONNECT_NETS);
	}
	
	public void broadcast(ClusterMessage message) throws IOException {
		BasicProperties props = new BasicProperties.Builder()
			.build();
		
		byte[] bytes = message.toString().getBytes();
		
		broadcastChannel.basicPublish(routing.getBroadcastExchangeName(), "", props, bytes);
	}
	
	public void installModel(ClusterNode node, Model model) throws IOException {
		String routingKey = routing.getNodeRoutingKey(node);
		ModelMessage message = new ModelMessage(model);
		
		BasicProperties props = new BasicProperties.Builder()
		.build();
	
		byte[] bytes = message.toString().getBytes();
	
		System.out.println("Sending "+model.getUnitName()+" to "+node.getName());

		nodeChannel.basicPublish(routing.getNodesExchangeName(), routingKey, props, bytes);
		
	}
	


}
