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
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
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
	private TankerAgent myDedaleAgent;
	private String[] agentsIds;
	public SimpleTankerBehaviour (final TankerAgent myagent) {
		super(myagent);
		this.myDedaleAgent=myagent;
		this.agentsIds=myDedaleAgent.getIdList();
	}

	@Override
	public void action() {
		System.out.println("Tanker main loop");

		myDedaleAgent.doWait(200);
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		
		
		//Example to retrieve the current position
		//System.out.println(this.myAgent.getLocalName()+" -- myCurrentPosition is: "+myPosition);
		if (myPosition!=null){
			if(myDedaleAgent.getMySpot()==null){
				myDedaleAgent.setMySpot(myPosition);
			}
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
			myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
			
			if(!myDedaleAgent.getMySpot().equals(myPosition)) {
				System.out.println("Tanker try to come back");
				List<String> spotList = new ArrayList<String>();
				spotList.add(myDedaleAgent.getMySpot());
				
				List<String> newPath = myDedaleAgent.getMap().getShortestPathOpenNodes(myPosition, spotList);
				myDedaleAgent.setTagetPath(newPath);
				
			
				myDedaleAgent.moveTo(newPath.get(0));
			}
			//List of observable from the agent's current position
			
			//ADD PING AUX AUTRES
			if(!myDedaleAgent.isCheckingBehaviourRunning()) {
				myDedaleAgent.addBehaviour(new ReceiveMessageBehaviour(myDedaleAgent));
			}
		}
		

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}