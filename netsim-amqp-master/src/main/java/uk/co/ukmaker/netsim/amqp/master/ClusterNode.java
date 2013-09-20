package uk.co.ukmaker.netsim.amqp.master;


import java.util.concurrent.Future;

import uk.co.ukmaker.netsim.amqp.messages.NetsimMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.InitialiseModelsMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.InstallModelMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.PropagateInputsMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.PropagateOutputsMessage;
import uk.co.ukmaker.netsim.amqp.messages.node.UpdateModelsMessage;
import uk.co.ukmaker.netsim.models.Model;

abstract public class ClusterNode {
	
	private final String name;
	private final long ramSize;
	
	public ClusterNode(String name, long ramSize) {
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
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClusterNode other = (ClusterNode) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Future<NetsimMessage>  installModel(Model model) throws Exception {
		
		return sendAndReceive(new InstallModelMessage(model));
	}
	
	public Future<NetsimMessage> initialiseModels() throws Exception {
		return sendAndReceive(new InitialiseModelsMessage());
	}
	
	public Future<NetsimMessage> updateModels(long moment) throws Exception {
		return sendAndReceive(new UpdateModelsMessage(moment));
	}
	
	public Future<NetsimMessage> propagateOutputs(PropagateOutputsMessage m) throws Exception {
		return sendAndReceive(m);
	}
	
	public Future<NetsimMessage> propagateInputs(PropagateInputsMessage m) throws Exception {
		return sendAndReceive(m);
	}
	
	abstract protected void send(NetsimMessage m) throws Exception;
	
	abstract protected Future<NetsimMessage> sendAndReceive(NetsimMessage m) throws Exception;

}