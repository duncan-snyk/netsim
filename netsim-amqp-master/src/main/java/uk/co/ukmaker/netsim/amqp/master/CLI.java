package uk.co.ukmaker.netsim.amqp.master;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
 * Main control loop for a cluster node
 * 
 */

public class CLI {
	
	private Master master;
	
	private boolean verbose = false;
	
	public CLI(Master master) {
		this.master = master;
	}
	
	public void run() throws Exception {
		
		master.initialize();
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			
			try {
				System.out.println();
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
    				
    			} else if("installModels".equalsIgnoreCase(bits[0]) || "im".equalsIgnoreCase(bits[0])) {
    				
    				master.installModels();
    				
    			} else if("connectNets".equalsIgnoreCase(bits[0]) || "cn".equalsIgnoreCase(bits[0])) {
    				
    				master.connectNets();
    				
    			} else if("reset".equals(bits[0])) {
    				
    				master.resetAll();
    				
    			} else if("clear".equals(bits[0])) {
    				
    				master.clearAll();
    				
    			} else if("initialiseModels".equals(bits[0])) {
    				
    				master.initialiseModels();
    				
    			} else if("verbose".equalsIgnoreCase(bits[0])) {
    				
    				verbose = true;
    				
    			} else if("simulate".equals(bits[0])) {
    				
    				long start = System.currentTimeMillis();
    				master.simulate(verbose);
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
    				
    				master.simulate(verbose);
    				
    			} else if("help".equals(bits[0])) {
    				
    				System.out.println("Commands:");
    				System.out.println("reset");
    				System.out.println("clear");
    				System.out.println("enumerate");
    				System.out.println("load <fileName>");
    				System.out.println("installModels");
    				System.out.println("connectNets");
    				System.out.println("initialiseModels");
    				System.out.println("run <fileName>");
    				System.out.println("simulate");
    				System.out.println("");
    			} else {
    				System.out.println("ERROR - Unknown command '"+bits[0]+"'");
    			}
    			
			} catch(Exception e) {
				System.err.println("ERROR: "+e.getMessage());
				e.printStackTrace(System.err);
			}
		}

	}
	


}
