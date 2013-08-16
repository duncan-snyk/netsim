package uk.co.ukmaker.netsim.cluster;

public class ClusterNode {
	
	private String name;
	private long ramSize;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getRamSize() {
		return ramSize;
	}
	public void setRamSize(long ramSize) {
		this.ramSize = ramSize;
	}
}