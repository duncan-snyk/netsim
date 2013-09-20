package uk.co.ukmaker.netsim.amqp.master.spring;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

import uk.co.ukmaker.netsim.amqp.messages.NetsimMessage;

class NetsimMessagePostProcessor implements MessagePostProcessor {
	
	private NetsimMessage netsimMessage;
	
	public NetsimMessagePostProcessor(NetsimMessage cm) {
		netsimMessage = cm;
	}

	@Override
	public Message postProcessMessage(Message message) throws AmqpException {
		netsimMessage.populateHeaders(message.getMessageProperties().getHeaders());
		return message;
	}
	
}