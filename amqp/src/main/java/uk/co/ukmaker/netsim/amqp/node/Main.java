package uk.co.ukmaker.netsim.amqp.node;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
	public static void main(String[] args) throws InterruptedException, IOException {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("node-context.xml");
		
		BroadcastPort port = ctx.getBean(BroadcastPort.class);
		
		port.initialize();
		
		while(true) {
			// loop forever
			Thread.sleep(1000);
		}

	}

}
