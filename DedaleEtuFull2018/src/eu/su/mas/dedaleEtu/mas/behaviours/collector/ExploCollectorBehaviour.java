package eu.su.mas.dedaleEtu.mas.behaviours.collector;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.yours.CollectMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.CheckTankerBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

//exploration du collecteur
public class ExploCollectorBehaviour extends DedaleSimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;



	
	
	
	private CollectMultiAgent myDedaleAgent;


	public ExploCollectorBehaviour(final CollectMultiAgent myagent) {
		super(myagent);
		this.myDedaleAgent = myagent;
		this.temporised=true;
	}

	@Override
	public void action() {
		onAction();
		if(suspended) {
			return;
		}
		//SET MAINBEHAVIOUR
		myDedaleAgent.setMainBehaviour(this);
		myDedaleAgent.setPriority(30);
		
		String myPosition=myDedaleAgent.getCurrentPosition();
		if (myPosition!=null){
			myDedaleAgent.setPosition(myPosition);
			boolean moved = false;
			//OBSERVATIONS ET MAJ
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=myDedaleAgent.observe();//myPosition
			MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);

			String nextNode=null;
			//check si plus de noeuds ouverts
			if (myDedaleAgent.getOpenNodes().isEmpty()){
				//Explo finished
				finished=true;
				myDedaleAgent.addBehaviour(new CheckTankerBehaviour(myDedaleAgent));
				System.out.println(myDedaleAgent.getLocalName()+" -----> Exploration successufully done, behaviour removed.");
			}else{
				//Calcul du chemin, MaJ de l'objectif
				if (nextNode==null){
					List<String> path = this.myDedaleAgent.getMap().getShortestPathOpenNodes(myPosition, myDedaleAgent.getOpenNodes());
					nextNode=path.get(0);
					myDedaleAgent.setNextNode(nextNode);
					myDedaleAgent.setTargetNode(path.get(path.size()-1));
				}
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
