package uk.co.ukmaker.netsim.cluster;

import java.util.ArrayList;
import java.util.List;


public class ClusterData {
	
	private List<ClusterNode> nodes = new ArrayList<ClusterNode>();

	public List<ClusterNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<ClusterNode> nodes) {
		this.nodes = nodes;
	}
}
