package uk.co.ukmaker.netsim.amqp.master;

import static uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage.Type.CLEAR;
import static uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage.Type.RESET;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.ukmaker.netsim.amqp.Routing;
import uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage;
import uk.co.ukmaker.netsim.models.test.TestProbe;
import uk.co.ukmaker.netsim.netlist.Compiler;
import uk.co.ukmaker.netsim.netlist.Netlist;
import uk.co.ukmaker.netsim.netlist.TestFixture;
import uk.co.ukmaker.netsim.parser.Parser;
import uk.co.ukmaker.netsim.simulation.Simulator;
import uk.co.ukmaker.netsim.simulation.NetEventPropagator;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

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
	private long simulationEnd = 0;
	private List<TestProbe> testProbes;
	
	private DistributedNetlistDriver driver = new DistributedNetlistDriver();
	
	private Simulator simulator;
	private NetEventPropagator handler;
	
	public void initialize() throws IOException {
		
		broadcastChannel = connectionFactory.newConnection().createChannel();
		broadcastChannel.exchangeDeclare(routing.getBroadcastExchangeName(), "fanout");
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
		
		simulator = new Simulator();
		simulator.setNetlistDriver(driver);
		handler = new DistributedCallbackHandler();
	}
	

	
	public ClusterData discoverNodes() throws Exception {
		
		cluster = new ClusterData();
		cluster.setState(ClusterData.State.ENUMERATING);
		
		broadcast(new BroadcastMessage(BroadcastMessage.Type.ENUMERATE));
		
		// Now consume everything available on the discoveryChannel
		
		// crufty. Discovery gives the nodes one second to reply
		QueueingConsumer.Delivery delivery;
		while((delivery = consumer.nextDelivery(routing.getDiscoveryTimeout())) != null) {
			ClusterNode node = readClusterNode(new String(delivery.getBody()));
			cluster.getNodes().add(node);
		}
		
		cluster.setState(ClusterData.State.ENUMERATED);
		
		return cluster;
	}
	
	
	// Deserialize from wire format which is "name:ramSize"
	public ClusterNode readClusterNode(String serialized) {
		String[]  bits = serialized.split(":");
		return new ClusterNode(nodeChannel, routing.getNodesExchangeName(), bits[0], Integer.parseInt(bits[1]));
		
	}
	
	public void clearAll() throws Exception {
		broadcast(new BroadcastMessage(CLEAR));
	}
	
	public void resetAll() throws Exception {
		broadcast(new BroadcastMessage(RESET));
	}
	
	public void broadcast(BroadcastMessage message) throws IOException {
		
		Map<String, Object> headers = new HashMap<String, Object>();
		
		message.populateHeaders(headers);
		
		BasicProperties props = new BasicProperties.Builder()
		.headers(headers)
		.build();

		byte[] bytes = message.getBytes();
		
		broadcastChannel.basicPublish(routing.getBroadcastExchangeName(), "", props, bytes);
	}

	
	public void loadSimulation(String filename) throws Exception {

		URL r = new URL("file://"+filename);
		File f = new File(r.getFile());
		FileInputStream source = new FileInputStream(f);
		
		Parser p = new Parser();
		p.setBaseDir(f.getParentFile());
		p.parse(source);
		
		TestFixture testFixture = (TestFixture)p.getEntity();
		
		Compiler c = new Compiler();
		
		netlist = c.compile(testFixture);
		simulationEnd = testFixture.getEndMoment();
		testProbes = testFixture.getTestProbes();
		driver.setNetlist(netlist);
		
		System.out.print("Loaded Simulation");
		System.out.print("-------------------------------------------");
		System.out.println("Circuit = "+p.getEntity().getName());
		System.out.println("End Time = "+simulationEnd);
		System.out.println(testProbes.size()+" TestProbes are attached");
	}
	
	public void installModels() throws Exception {
		driver.installModels(cluster);
	}
	
	public void initialiseModels() throws Exception {
		driver.initialiseModels();
	}
	
	public void simulate() throws Exception {
		simulator.simulate(netlist, simulationEnd, testProbes);
	}
}
