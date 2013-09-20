package uk.co.ukmaker.netsim.amqp.node;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/*
 * Main control loop for a cluster node
 * 
 */
public class Main {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("node-template-context.xml");
		
		RemoteNode remoteNode = ctx.getBean(RemoteNode.class);
		
		remoteNode.initialise();
		
		while(true) {
			// loop forever
			Thread.sleep(1000);
		}

	}

}
