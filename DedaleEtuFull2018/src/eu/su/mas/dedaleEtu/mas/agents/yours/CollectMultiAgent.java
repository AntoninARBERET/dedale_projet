package eu.su.mas.dedaleEtu.mas.agents.yours;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.archive.dummies.ExploSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.explorer.ExploMultiBehaviour;
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

public class CollectMultiAgent extends DedaleAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	//private String[] idList;
	private List<String> toRetry;

	
	

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected void setup(){
		
		super.setup();
		final Object[] args = getArguments();
		//idList = (String[])args[2];
		//myMap=new MapRepresentation();
		toRetry = new ArrayList<String>();
		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/
		
		lb.add(new ExploMultiBehaviour(this));
		
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	
	//Lootable open and closed
	public List<String> getLootable(){
		List<String> lootable = new ArrayList<String>();
		
		for(String t : getOpenTresor()) {
			if((int)(getMap().getNode(t).getAttribute("force"))<=myStrengh){
				lootable.add(t);
				
			}
		}
		for(String t : getClosedTresor()) {
			if((int)(getMap().getNode(t).getAttribute("force"))<=myStrengh){
				lootable.add(t);
				
			}
		}
		
		return lootable;
	}
	
	//open lootable
	public List<String> getOpenLootable(){
		List<String> lootable = new ArrayList<String>();
		
		for(String t : getOpenTresor()) {
			if((int)(getMap().getNode(t).getAttribute("force"))<=myStrengh){
				lootable.add(t);
				
			}
		}

		
		return lootable;
	}
	
	//open or close lootable except "without
	public List<String> getLootableWhithout(List<String> without){
		List<String> lootable = new ArrayList<String>();
		
		for(String t : getLootable()) {
			if(!without.contains(t)){
				lootable.add(t);
				
			}
		}
		
		
		return lootable;
	}

	public List<String> getToRetry() {
		return toRetry;
	}
	
	public void resetToRetry() {
		toRetry = new ArrayList<String>();
	}
	
}