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
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
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


public class TankerBehaviour extends DedaleSimpleBehaviour{
	
	/**
	 * When an agent choose to move
	 *  
	 */
	private static final long serialVersionUID = 9088209402507795289L;
	private TankerAgent myDedaleAgent;
	private String[] agentsIds;
	public TankerBehaviour (final TankerAgent myagent) {
		super(myagent);
		this.myDedaleAgent=myagent;
		this.agentsIds=myDedaleAgent.getIdList();
	}

	@Override
	public void action() {
		String myPosition=this.myDedaleAgent.getCurrentPosition();
		myDedaleAgent.setMainBehaviour(this);
		myDedaleAgent.setPriority(20);
		if (myPosition!=null){
			myDedaleAgent.setPosition(myPosition);
			//premier spot : noeud initial
			if(myDedaleAgent.getMySpot()==null){
				System.out.println(myDedaleAgent.getName()+" -----> spot = "+myPosition);
				myDedaleAgent.setMySpot(myPosition);
				myDedaleAgent.setTargetNode(myPosition);
			}
			
			//observations
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
			myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
			
			if(!myDedaleAgent.isFinalSpot() && myDedaleAgent.getOpenNodes().isEmpty()) {
				myDedaleAgent.setMySpot(myDedaleAgent.getMap().calculateSilloSpot());
				myDedaleAgent.setFinalSpot(true);
			}
			
			
			
			if(!myDedaleAgent.getMySpot().equals(myPosition)) {
				System.out.println("Tanker try to come back");
				
				List<String> newPath = myDedaleAgent.getMap().getShortestPath(myPosition, myDedaleAgent.getMySpot());
				myDedaleAgent.setTagetPath(newPath);
				myDedaleAgent.moveTo(newPath.get(0));
			}
			
		}
		

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}