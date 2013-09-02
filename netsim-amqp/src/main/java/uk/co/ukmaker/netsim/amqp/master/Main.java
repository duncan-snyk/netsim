package uk.co.ukmaker.netsim.amqp.master;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.ukmaker.netsim.amqp.node.RemoteNode;

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
	
	public void run() throws Exception {
		
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
    				
    				if(bits.length < 2) {
    					throw new Exception("load: filename required");
    				}
    				
    				master.loadSimulation(bits[1]);
    				
    			} else if("installModels".equals(bits[0])) {
    				
    				master.installModels();
    				
    			} else if("connectNets".equals(bits[0])) {
    				
    				master.connectNets();
    				
    			} else if("initialiseModels".equals(bits[0])) {
    				
    				master.initialiseModels();
    				
    			} else if("simulate".equals(bits[0])) {
    				
    				long start = System.currentTimeMillis();
    				master.simulate();
    				long end = System.currentTimeMillis();
    				System.out.println("Simulation elapsed time (ms) = "+(end - start));
    				
    			} else if("run".equals(bits[0])) {
    				
    				if(bits.length < 2) {
    					throw new Exception("run: filename required");
    				}
    				
    				ClusterData data = master.discoverNodes();
    				
    				System.out.println("Discovered "+data.getNodes().size()+" nodes:");
    				for(ClusterNode node : data.getNodes()) {
    					System.out.println(String.format("%12s  RAM = %12d", node.getName(), node.getRamSize()));
    				}
    				
    				master.clearAll();
    				
    				master.loadSimulation(bits[1]);
    				
    				master.installModels();
    				
    				master.connectNets();
    				
    				master.simulate();
    				
    			} else if("help".equals(bits[0])) {
    				
    				System.out.println("Commands:");
    				System.out.println("enumerate");
    				System.out.println("load <fileName>");
    				System.out.println("installModels");
    				System.out.println("connectNets");
    				System.out.println("initialiseModels");
    				System.out.println("run <fileName>");
    				System.out.println("simulate");
    				System.out.println("");
    			}
    			
			} catch(Exception e) {
				System.err.println("ERROR: "+e.getMessage());
				e.printStackTrace(System.err);
			}
		}

	}
	


}
