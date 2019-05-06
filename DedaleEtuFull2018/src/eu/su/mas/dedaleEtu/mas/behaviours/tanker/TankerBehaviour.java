package eu.su.mas.dedaleEtu.mas.behaviours.tanker;


import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.TankerAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.SendMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

//comportement du tanker
public class TankerBehaviour extends DedaleSimpleBehaviour{
	
	
	private static final long serialVersionUID = 9088209402507795289L;
	private TankerAgent myDedaleAgent;
	public TankerBehaviour (final TankerAgent myagent) {
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
		String myPosition=this.myDedaleAgent.getCurrentPosition();
		myDedaleAgent.setMainBehaviour(this);
		myDedaleAgent.setPriority(20);
		if (myPosition!=null){
			myDedaleAgent.setPosition(myPosition);
			boolean moved = false;
			String nextNode=null;
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
			
			
			//si je ne suis plus sur mon noeud
			if(!myDedaleAgent.getMySpot().equals(myPosition)) {
				System.out.println("Tanker try to come back");
				
				List<String> newPath = myDedaleAgent.getMap().getShortestPath(myPosition, myDedaleAgent.getMySpot());
				myDedaleAgent.setTagetPath(newPath);
				nextNode=newPath.get(0);
				myDedaleAgent.moveTo(nextNode);
				moved=true;
			}
			
			//GESTION DES BLOCAGES
			//
			//position inchangee meme si moveTo
			if(myDedaleAgent.getPreviousPos() !=null && myDedaleAgent.getPreviousPos().equals(myPosition) && moved  && nextNode!=null) {
				
				myDedaleAgent.onBlock(nextNode);
			}else{
				myDedaleAgent.resetBlockedSince();
			}
			myDedaleAgent.setPreviousPos(myPosition);
		}
		

	}

	@Override
	public boolean done() {
		return false;
	}

}