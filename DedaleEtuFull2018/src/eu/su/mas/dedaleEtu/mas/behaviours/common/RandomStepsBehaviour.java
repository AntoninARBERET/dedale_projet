package eu.su.mas.dedaleEtu.mas.behaviours.common;



import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.SimpleBehaviour;

/**************************************
 * 
 * 
 * 				BEHAVIOUR RandomWalk : Illustrates how an agent can interact with, and move in, the environment
 * 
 * 
 **************************************/


public class RandomStepsBehaviour extends SimpleBehaviour{
	
	private static final long serialVersionUID = 9088209402507795289L;
	private DedaleAgent myDedaleAgent;
	private int nbSteps;
	private boolean finished = false;
	private boolean mapSending;
	private String previousPosition;

	public RandomStepsBehaviour (final DedaleAgent myagent, int nbSteps, boolean mapSending) {
		super(myagent);
		this.myDedaleAgent=myagent;
		this.nbSteps=nbSteps;
		this.mapSending=mapSending;
		this.previousPosition=null;
	}

	@Override
	public void action() {
		//Envoie de map si requis
		if(mapSending) {
			myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
		}
		
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);			
			//Random move avec position précédente exclue
			Random r= new Random();
			int moveId=1+r.nextInt(lobs.size()-1);//removing the current position from the list of target, not necessary as to stay is an action but allow quicker random move
			String nextNode=lobs.get(moveId).getLeft();
			if(lobs.size()>1 && nextNode.equals(previousPosition)) {
				int tmp=1+r.nextInt(lobs.size()-2);
				if(tmp>=moveId){
					moveId=tmp+1;
				}
			}
			((AbstractDedaleAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
			myDedaleAgent.doWait(200);
			nbSteps--;
			if(nbSteps<=0) {
				this.finished=true;
			}
			previousPosition = myPosition;
		}

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return finished;
	}

}