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

public class ExploreMultiAgent extends DedaleAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	//private String[] idList;
	
	
	

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
		type="explorer";

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
	
	public List<String> getOpenable(){
		List<String> openable = new ArrayList<String>();
		
		for(String t : getClosedTresor()) {
			if((int)(getMap().getNode(t).getAttribute("lockPicking"))<=myLockPicking&&(int)(getMap().getNode(t).getAttribute("force"))<=myStrengh){
				openable.add(t);
				
			}
		}
		return openable;
	}
	
}
