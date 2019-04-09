package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.ArrayList;

import dataStructures.tuple.Couple;

public class SendableMap implements Serializable {
	//Liste de cases, 1 par node
	private ArrayList<Case> cases;
	
	//Liste de couple String String, 1 par edge
	private ArrayList<Couple<String,String>> edges;
	
	

	public SendableMap(ArrayList<Case> cases, ArrayList<Couple<String, String>> edges) {
		this.cases = cases;
		this.edges = edges;
	}

	public ArrayList<Case> getCases() {
		return cases;
	}

	public ArrayList<Couple<String, String>> getEdges() {
		return edges;
	}
	
	
}
