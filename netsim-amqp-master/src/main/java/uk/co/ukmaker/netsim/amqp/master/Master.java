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

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.ukmaker.netsim.amqp.Routing;
import uk.co.ukmaker.netsim.amqp.master.rabbit.RabbitClusterNode;
import uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage;
import uk.co.ukmaker.netsim.amqp.messages.broadcast.BroadcastMessage.Type;
import uk.co.ukmaker.netsim.amqp.messages.discovery.EnumeratedMessage;
import uk.co.ukmaker.netsim.amqp.node.RemoteNode;
import uk.co.ukmaker.netsim.models.test.TestProbe;
import uk.co.ukmaker.netsim.netlist.Compiler;
import uk.co.ukmaker.netsim.netlist.Netlist;
import uk.co.ukmaker.netsim.netlist.TestFixture;
import uk.co.ukmaker.netsim.parser.Parser;
import uk.co.ukmaker.netsim.simulation.Simulator;
import uk.co.ukmaker.netsim.simulation.NetEventPropagator;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
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
	
	@Autowired
	RemoteNode localNode;
	
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
	
	@Autowired
	private ClusterMaster clusterMaster;
	
	public void initialize() throws Exception {
		
		simulator = new Simulator();
		simulator.setNetlistDriver(driver);
		
		clusterMaster.initialize();
	}
	
	public ClusterData discoverNodes() throws Exception {
		return clusterMaster.discoverNodes();
	}
	
	
	public void clearAll() throws Exception {
		clusterMaster.clearAll();
	}
	
	public void resetAll() throws Exception {
		clusterMaster.resetAll();
	}
	
	public void connectNets() throws Exception {
		clusterMaster.connectNets();
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
		driver.installModels(cluster, localNode);
	}
	
	public void initialiseModels() throws Exception {
		driver.initialiseModels();
	}
	
	public void simulate(boolean verbose) throws Exception {
		simulator.setVerbose(verbose);
		simulator.simulate(netlist, simulationEnd, localNode.getNode().getNetlist().getTestProbes());
	}
}
