package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.yours.CollectMultiAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.collector.CollectBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.SendMapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.explorer.OpenBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

//check du tanker entre explo et collecte
public class CheckTankerBehaviour extends DedaleSimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	private DedaleAgent myDedaleAgent;
	private String tankerPos;


	public CheckTankerBehaviour(final DedaleAgent myagent) {
		super(myagent);
		this.myDedaleAgent = myagent;
		this.temporised=true;
		
		tankerPos=null;
		Couple<List<String>, List<String>> tmp = myDedaleAgent.getMap().getPosByType("tanker");
		if(tmp.getRight().size()>0) {
			tankerPos=tmp.getRight().get(0);
		}
	}

	@Override
	public void action() {
		onAction();
		if(suspended) {
			return;
		}
		//si on a pas d'info sur le tanker
		if(tankerPos==null || myDedaleAgent.getMap().getSilochecked()) {
			finished = true;
			if(myDedaleAgent instanceof CollectMultiAgent) {
				System.out.println(myDedaleAgent.getLocalName() + " -----> pas ou plus de tanker a check, passage a la collecte");
				myDedaleAgent.addBehaviour(new CollectBehaviour((CollectMultiAgent)myDedaleAgent));
			}else {
				System.out.println(myDedaleAgent.getLocalName() + " -----> pas ou plus de tanker a check, passage a l'ouverture");
				myDedaleAgent.addBehaviour(new OpenBehaviour((ExploreMultiAgent)myDedaleAgent));
			}
			
		}else {
			//SET MAINBEHAVIOUR
			myDedaleAgent.setMainBehaviour(this);
			myDedaleAgent.setPriority(30);
			
			String myPosition=myDedaleAgent.getCurrentPosition();
			if (myPosition!=null){
				myDedaleAgent.setPosition(myPosition);
				
				//OBSERVATIONS ET MAJ
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs=myDedaleAgent.observe();//myPosition
				MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
				
				String nextNode=null;
				boolean moved=false;
				List<String> newPath = myDedaleAgent.getMap().getShortestPath(myPosition, tankerPos);
					
				//sur le spot
				if(newPath.size()==0) {
					myAgent.addBehaviour(new RandomStepsBehaviour(myDedaleAgent, 1, false));
				}
				else {
					
					if(newPath.size()>1){
						myDedaleAgent.setTargetNode(newPath.get(newPath.size()-2));//crash todo
					}
					//si sur un voisin du tanker
					else{
						myDedaleAgent.setTargetNode(myPosition);
					}
					
					//sur l'objectif
					if(myPosition.equals(myDedaleAgent.getTargetNode())){
						finished = true;
						myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
						myDedaleAgent.getMap().setSilochecked(true);
						myDedaleAgent.getMap().calculateSilloSpot();
						if(myDedaleAgent instanceof CollectMultiAgent) {
							System.out.println(myDedaleAgent.getLocalName() + " -----> tanker checked, passage a la collecte");
							myDedaleAgent.addBehaviour(new CollectBehaviour((CollectMultiAgent)myDedaleAgent));
						}else {
							System.out.println(myDedaleAgent.getLocalName() + " -----> tanker checked, passage a l'ouverture");
							myDedaleAgent.addBehaviour(new OpenBehaviour((ExploreMultiAgent)myDedaleAgent));
						}
					}
					
					else {
						//Calcul du chemin, MaJ de l'objectif
						
						List<String> path = this.myDedaleAgent.getMap().getShortestPath(myPosition, myDedaleAgent.getTargetNode());
						nextNode=path.get(0);
						myDedaleAgent.setNextNode(nextNode);							
						myDedaleAgent.moveTo(nextNode);
						moved=true;
					}
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
		
	}
	@Override
	public boolean done() {
		return finished;
	}
	
	

}