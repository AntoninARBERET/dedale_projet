package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.util.Iterator;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.BlockingSendMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.SendMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.Block;
import eu.su.mas.dedaleEtu.mas.knowledge.Help;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

//gestion d'un blocage
public class HelpingBehaviour extends DedaleSimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	private DedaleAgent myDedaleAgent;
	
	private DedaleSimpleBehaviour callingBehaviour;
	
	public Help h;
	
	private String solution;

	//gestion de l'aide, icomplet
	public HelpingBehaviour(DedaleAgent myagent, Help h, DedaleSimpleBehaviour callingBehaviour) {
		super(myagent);
		this.myDedaleAgent = myagent;
		this.h=h;
		this.callingBehaviour=callingBehaviour;
		this.temporised=true;
		callingBehaviour.block();//changer
		
		
	}

	@Override
	public void action() {
		onAction();
		if(suspended) {
			return;
		}
		//SET MAINBEHAVIOUR
		myDedaleAgent.setTargetNode(h.getObjective());
		String myPosition = myDedaleAgent.getCurrentPosition();
		if (myPosition!=null){
			myDedaleAgent.setPosition(myPosition);
			boolean moved = false;
			String nextNode=null;
			//premier spot : noeud initial
			
			//observations
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
			myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
			
			
			
			
			//si je ne suis plus sur mon noeud
			if(!myDedaleAgent.getTargetNode().equals(myPosition)) {
				System.out.println("Tanker try to come back");
				
				List<String> newPath = myDedaleAgent.getMap().getShortestPath(myPosition, myDedaleAgent.getTargetNode());
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
		return finished;
	}
	
	

}
