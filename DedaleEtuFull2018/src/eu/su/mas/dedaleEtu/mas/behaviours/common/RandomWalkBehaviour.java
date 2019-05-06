package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;


//randomwalk
public class RandomWalkBehaviour extends DedaleSimpleBehaviour{
	
	/**
	 * When an agent choose to move
	 *  
	 */
	private static final long serialVersionUID = 9088209402507795289L;
	private DedaleAgent myDedaleAgent;
	public RandomWalkBehaviour (final DedaleAgent myagent) {
		super(myagent);
		this.myDedaleAgent=myagent;
		this.temporised=true;
	}

	@Override
	public void action() {
		onAction();
		if(suspended) {
			return;
		}
		myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		if (myPosition!=null){
			myDedaleAgent.setPosition(myPosition);
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			Random r= new Random();
			int moveId=1+r.nextInt(lobs.size()-1);
			((AbstractDedaleAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
		}

	}

	@Override
	public boolean done() {
		return false;
	}

}