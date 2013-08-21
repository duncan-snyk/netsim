package uk.co.ukmaker.netsim.amqp.master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.ukmaker.netsim.amqp.ClusterData;
import uk.co.ukmaker.netsim.amqp.ClusterNode;
import uk.co.ukmaker.netsim.simulation.LocalSimulatorNode;

/*
 * Main control loop for a cluster node
 * 
 */
public class Main {
	
	private Master master;
	
	public Main(Master master) {
		this.master = master;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("master-context.xml");
		
		Master master = ctx.getBean(Master.class);
		master.initialize();
		
		Main main = new Main(master);
		
		main.run();
	}
	
	public void run() {
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			
			try {
				
				System.out.print("netsim>");
    			
    			String command = bufferedReader.readLine();		
    			
    			String[] bits = command.split("\\s+");
    			
    			if("enumerate".equals(command)) {
    				ClusterData data = master.discoverNodes();
    				
    				System.out.println("Discovered "+data.getNodes().size()+" nodes:");
    				for(ClusterNode node : data.getNodes()) {
    					System.out.println(String.format("%12s  RAM = %12d", node.getName(), node.getRamSize()));
    				}
    				
    			} else if("load".equals(bits[0])) {
    				
    				load(bits);
    				
    			} else if("installModels".equals(bits[0])) {
    				master.installModels();
    			} else if("connectNets".equals(bits[0])) {
    				master.connectNets();
    			}
    			
			} catch(Exception e) {
				System.err.println("ERROR: "+e.getMessage());
			}
		}

	}
	
	public void load(String[] cmd) throws Exception {
		
		if(cmd.length < 2) {
			throw new Exception("load: filename required");
		}
		
		LocalSimulatorNode sim = master.loadSimulation(cmd[1]);
		
		System.out.print("Loaded.  Circuit = ");
		System.out.println(sim.getNetlist().getCircuit().getName());
	}

}
