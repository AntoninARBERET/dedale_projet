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
import scala.util.Random;

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
		
		if(callingBehaviour instanceof HelpingBehaviour && h.getObjective()==((HelpingBehaviour) callingBehaviour).getObjective()) {
			if(h.isEnd()) {
				((HelpingBehaviour) callingBehaviour).setFinished(true);
			}
			finished=true;
		}else {
			this.myDedaleAgent = myagent;
			this.h=h;
			this.callingBehaviour=callingBehaviour;
			this.temporised=true;
			callingBehaviour.suspend();
			List<String> voisins = myDedaleAgent.getMap().getNeighbours(h.getNode());
			Random r = new Random();
			solution = voisins.get(r.nextInt(voisins.size()));
			System.out.println(myDedaleAgent.getLocalName()+" -----> go help :  "+h.toString());
		}
		
		
	}

	@Override
	public void action() {
		onAction();
		if(suspended) {
			return;
		}
		//SET MAINBEHAVIOUR
		myDedaleAgent.setMainBehaviour(this);
		System.out.println(myDedaleAgent.getLocalName()+" -----> go help :  "+h.toString());
		
		myDedaleAgent.setTargetNode(solution);
		String myPosition = myDedaleAgent.getCurrentPosition();
		if (myPosition!=null){
			myDedaleAgent.setPosition(myPosition);
			boolean moved = false;
			String nextNode=null;
			
			
			//observations
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
			myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
			
			
			
			
			
			if(!myDedaleAgent.getTargetNode().equals(myPosition)) {
				
				List<String> newPath = myDedaleAgent.getMap().getShortestPath(myPosition, myDedaleAgent.getTargetNode());
				myDedaleAgent.setTagetPath(newPath);
				nextNode=newPath.get(0);
				myDedaleAgent.moveTo(nextNode);
				moved=true;
			}else {
				myDedaleAgent.doWait(100);
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
		this.callingBehaviour.resume();
		return finished;
	}
	
	public String getObjective() {
		return h.getObjective();
	}
	
	public void setFinished(boolean f) {
		finished=f;
	}
	

}
