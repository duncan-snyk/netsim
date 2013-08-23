package uk.co.ukmaker.netsim.amqp.node;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

import uk.co.ukmaker.netsim.ScheduledValue;
import uk.co.ukmaker.netsim.amqp.Routing;
import uk.co.ukmaker.netsim.amqp.master.ClusterNode;
import uk.co.ukmaker.netsim.amqp.messages.Message;
import uk.co.ukmaker.netsim.amqp.messages.netlist.ScheduleNetValueMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.InstallModelMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.PropagateInputsMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.PropagateOutputsMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.UpdateModelsMessage;
import uk.co.ukmaker.netsim.amqp.messages.nodereply.PropagatedNetDriversMessage;
import uk.co.ukmaker.netsim.amqp.messages.nodereply.UpdateEventQueueMessage;
import uk.co.ukmaker.netsim.models.Model;
import uk.co.ukmaker.netsim.simulation.SimulatorCallbackHandler;

/**
 * Handles messages on the node queue
 * @author mcintyred
 *
 */
public class NodeListener implements SimulatorCallbackHandler {
	
	@Autowired 
	Routing routing;
	
	@Autowired
	private ConnectionFactory connectionFactory;
	private Channel nodeChannel;
	private Channel netsChannel;
	
	@Autowired
	private Node node;
	
	private Consumer nodeCallback;
	private String nodeQueueName;
	private String netsExchangeName;

	public void initialise() throws Exception {
		
		nodeQueueName = routing.getNodeQueueName(node);
		
		nodeChannel = connectionFactory.newConnection().createChannel();
		nodeChannel.exchangeDeclare(routing.getNodesExchangeName(), "direct");
		nodeChannel.queueDeclare(nodeQueueName, false, true, true, null);
		nodeChannel.queueBind(nodeQueueName, routing.getNodesExchangeName(), routing.getNodeRoutingKey(node), null);
		nodeChannel.basicQos(1);
		
		nodeCallback = new DefaultConsumer(nodeChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				
				String message = new String(body);
				System.out.println("Processing node message: "+message);

				try {
					onNodeMessage(properties, body);
				} catch (Exception e) {
					throw new IOException("Error handling message", e);
				}
			}		
		};

		nodeChannel.basicConsume(nodeQueueName, true, nodeCallback);
		
		netsChannel = connectionFactory.newConnection().createChannel();
		netsExchangeName = routing.getNetsExchangeName();
	}
	
	public void onNodeMessage(BasicProperties properties, byte[] body) throws Exception {
	
		String type = (String)properties.getHeaders().get(Message.TYPE_HEADER);
		
		if(PropagateInputsMessage.TYPE.equals(type)) {
			propagateInputs(PropagateInputsMessage.read(properties.getHeaders(), body));
			return;
		}
		
		if(PropagateOutputsMessage.TYPE.equals(type)) {
			propagateOutputs(PropagateOutputsMessage.read(properties.getHeaders(), body));
			return;
		}
		
		if(UpdateModelsMessage.TYPE.equals(type)) {
			updateModels(UpdateModelsMessage.read(properties.getHeaders(), body));
			return;
		}
		
		if(InstallModelMessage.TYPE.equals(type)) {
			installModel(InstallModelMessage.read(properties.getHeaders(), body));
			return;
		}
		
		throw new Exception("Unknown message type "+type+" received by NodeListener");
	}
	
	public void propagateInputs(PropagateInputsMessage m) throws Exception {
		node.getNetlistDriver().propagateInputs(m.getMoment(), m.getNetDrivers(), this);
	}
	
	public void propagateOutputs(PropagateOutputsMessage m) throws Exception {
		node.getNetlistDriver().propagateOutputs(m.getMoment(), m.getNetIds(), this);
	}
	
	public void updateModels(UpdateModelsMessage m) throws Exception {
		node.getNetlistDriver().updateModels(m.getMoment(), this);
	}

	public void installModel(InstallModelMessage installModelMessage) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Model model = (Model)Class.forName(installModelMessage.getClassName()).newInstance();
		
		model.setUnitId(installModelMessage.getUnitId());
		model.setName(installModelMessage.getName());
		
		node.addModel(model);
		
		for(String pinName : installModelMessage.getPinToNetMap().keySet()) {
			String netId = installModelMessage.getPinToNetMap().get(pinName);
			node.connectPin(model, netId, pinName);
		}
	}

	@Override
	public void propagateOutput(String netId, ScheduledValue value) throws Exception {
		
		ScheduleNetValueMessage m = new ScheduleNetValueMessage(netId, value);
		
		Map<String, Object> headers = new HashMap<String, Object>();
		
		m.populateHeaders(headers);
		
		BasicProperties props = new BasicProperties.Builder()
		.headers(headers)
		.build();

		netsChannel.basicPublish(netsExchangeName, netId, props, m.getBytes());	
		
	}

	@Override
	public void propagatedNetDrivers(Map<String, Integer> netDrivers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateEventQueue(Map<String, List<Long>> netMoments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputsPropagated() {
		// TODO Auto-generated method stub
		
	}
	
	protected void sendToNets(Message m) throws IOException {

		Map<String, Object> headers = new HashMap<String, Object>();
		
		m.populateHeaders(headers);
		
		BasicProperties props = new BasicProperties.Builder()
		.headers(headers)
		.build();

		netsChannel.basicPublish(netsExchangeName, null, props, m.getBytes());
	}

}
