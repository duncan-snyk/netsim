package uk.co.ukmaker.netsim.amqp.master;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.co.ukmaker.netsim.netlist.Compiler;
import uk.co.ukmaker.netsim.netlist.Netlist;
import uk.co.ukmaker.netsim.netlist.ParserTest;
import uk.co.ukmaker.netsim.netlist.TestFixture;
import uk.co.ukmaker.netsim.parser.Parser;

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
    				
    				
    				if(bits.length < 2) {
    					throw new Exception("load: filename required");
    				}
    				
    				master.loadSimulation(bits[1]);
    				
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
	


}
