package uk.co.ukmaker.netsim.amqp.node;

import java.io.IOException;
import com.rabbitmq.client.AMQP.BasicProperties;

public interface NetsListener {
	
	public void initialise() throws IOException;
	public void connectNets() throws IOException;
}
