package eu.su.mas.dedaleEtu.mas.behaviours.tanker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.TankerAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.ReceiveMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.SendMapBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;

/**************************************
 * 
 * 
 * 				BEHAVIOUR RandomWalk : Illustrates how an agent can interact with, and move in, the environment
 * 
 * 
 **************************************/


public class SimpleTankerBehaviour extends SimpleBehaviour{
	
	/**
	 * When an agent choose to move
	 *  
	 */
	private static final long serialVersionUID = 9088209402507795289L;
	private DedaleAgent myDedaleAgent;
	private String[] agentsIds;
	private String mySpot;
	public SimpleTankerBehaviour (final TankerAgent myagent) {
		super(myagent);
		this.myDedaleAgent=myagent;
		this.agentsIds=myDedaleAgent.getIdList();
		mySpot=null;
	}

	@Override
	public void action() {
		myDedaleAgent.doWait(500);
		myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
		
		//ADD PING AUX AUTRES
		
		myDedaleAgent.addBehaviour(new ReceiveMessageBehaviour(myDedaleAgent));
		//Example to retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		//System.out.println(this.myAgent.getLocalName()+" -- myCurrentPosition is: "+myPosition);
		if (myPosition!=null){
			if(mySpot==null){
				mySpot=myPosition;
			}
			if(!mySpot.equals(myPosition)) {
				List<String> spotList = new ArrayList<String>();
				spotList.add(mySpot);
				
				List<String> newPath = myDedaleAgent.getMap().getShortestPathOpenNodes(myPosition, spotList);
				myDedaleAgent.setTagetPath(newPath);
				
			
				myDedaleAgent.moveTo(newPath.get(0));
			}
			//List of observable from the agent's current position
			
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

		}
		

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}