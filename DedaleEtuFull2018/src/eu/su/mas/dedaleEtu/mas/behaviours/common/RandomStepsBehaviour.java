package eu.su.mas.dedaleEtu.mas.behaviours.common;



import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

//effectue un nombre donne de deplacements aleatoires
public class RandomStepsBehaviour extends DedaleSimpleBehaviour{
	
	private static final long serialVersionUID = 9088209402507795289L;
	private DedaleAgent myDedaleAgent;
	private int nbSteps;
	private boolean finished = false;
	private boolean mapSending;

	public RandomStepsBehaviour (final DedaleAgent myagent, int nbSteps, boolean mapSending) {
		super(myagent);
		this.myDedaleAgent=myagent;
		this.nbSteps=nbSteps;
		//si true, envoie de la map a chaque pas
		this.mapSending=mapSending;
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
			//Random move avec position predente exclue
			Random r= new Random();
			int moveId=1+r.nextInt(lobs.size()-1);
			String nextNode=lobs.get(moveId).getLeft();
			if(lobs.size()>1 && nextNode.equals(myDedaleAgent.getPreviousPos())) {
				int tmp;
				if(lobs.size()>2){
					tmp=1+r.nextInt(lobs.size()-2);
				}else{
					tmp=0;
				}
				
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
			myDedaleAgent.setPreviousPos(myPosition);
		}

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return finished;
	}

}