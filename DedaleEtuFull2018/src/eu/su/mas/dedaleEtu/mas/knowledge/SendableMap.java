package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructures.tuple.Couple;

//encapsule la map pour l'envoie
public class SendableMap implements Serializable {
	
	private static final long serialVersionUID = 2105819789141240827L;

	//Liste de cases, 1 par node
	private ArrayList<Case> cases;
	
	//Liste de couple String String, 1 par edge
	private ArrayList<Couple<String,String>> edges;
	
	//id de l'envoyeur
	private String sender;
	
	//type de l'envoyeur
	private String type;

	//position a l'envoi
	private String sendingPos;
	
	private HashMap<String, Couple<String,String>> agentsInfo;
	
	private boolean silochecked;


	

	public SendableMap(ArrayList<Case> cases, ArrayList<Couple<String, String>> edges, HashMap<String, Couple<String,String>> agentsInfo,
			String sender, String type, String sendingPos, boolean silochecked) {
		
		this.cases = cases;
		this.edges = edges;
		this.sender = sender;
		this.type = type;
		this.sendingPos = sendingPos;
		this.agentsInfo=agentsInfo;
		this.silochecked=silochecked;
	}

	public ArrayList<Case> getCases() {
		return cases;
	}

	public ArrayList<Couple<String, String>> getEdges() {
		return edges;
	}

	public String getSender() {
		return sender;
	}

	public String getType() {
		return type;
	}

	public String getSendingPos() {
		return sendingPos;
	}

	public HashMap<String, Couple<String, String>> getAgentsInfo() {
		return agentsInfo;
	}
	
	public boolean getSilochecked() {
		return silochecked;
	}
	
}
