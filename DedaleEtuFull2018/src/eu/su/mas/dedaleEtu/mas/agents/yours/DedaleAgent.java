package eu.su.mas.dedaleEtu.mas.agents.yours;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.archive.dummies.ExploSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;

/**
 * ExploreSolo agent. 
 * It explore the map using a DFS algorithm.
 * It stops when all nodes have been visited
 *  
 *  
 * @author hc
 *
 */

public class DedaleAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	

	private MapRepresentation myMap;
	
	private String[] idList;
	/**
	 * Nodes known but not yet visited
	 */
	private List<String> openNodes;
	
	/**
	 * Visited nodes
	 */
	private Set<String> closedNodes;
	
	private String position;
	
	private String nextNode;
	




	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected void setup(){

		super.setup();
		this.openNodes=new ArrayList<String>();
		this.closedNodes=new HashSet<String>();
		//final Object[] args = getArguments();
		//idList = (String[])args[2];
		//myMap=new MapRepresentation();

		//List<Behaviour> lb=new ArrayList<Behaviour>();
		
		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/
		
		//lb.add(new ExploMultiBehaviour(this,this.myMap, idList));
		
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		
		/*addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");*/

	}
	
	
	public List<String> getOpenNodes() {
		return openNodes;
	}


	public void setOpenNodes(List<String> openNodes) {
		this.openNodes = openNodes;
	}


	public Set<String> getClosedNodes() {
		return closedNodes;
	}


	public void setClosedNodes(Set<String> closedNodes) {
		this.closedNodes = closedNodes;
	}


	public void setMap(MapRepresentation myMap) {
		this.myMap=myMap;
	}


	public MapRepresentation getMap() {
		return myMap;
	}


	public String getPosition() {
		return position;
	}


	public void setPosition(String position) {
		this.position = position;
	}


	public String getNextNode() {
		return nextNode;
	}


	public void setNextNode(String nextNode) {
		this.nextNode = nextNode;
	}
	
	
}
