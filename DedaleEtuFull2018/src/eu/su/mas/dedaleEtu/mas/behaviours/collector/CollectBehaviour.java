package eu.su.mas.dedaleEtu.mas.behaviours.collector;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.yours.CollectMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.RandomStepsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.SendMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;



//Collecte des tresors
public class CollectBehaviour extends DedaleSimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	private CollectMultiAgent myDedaleAgent;
	
	private boolean dropPhase;
	
	private List<String> objectives;
	
	private int currentObjective;
	
	private String targetAgent;


	public CollectBehaviour(final CollectMultiAgent myagent) {
		super(myagent);
		this.myDedaleAgent = myagent;
		this.dropPhase=false;
		this.currentObjective = 0;
		
		myDedaleAgent.generateObjectives();
		this.objectives=myDedaleAgent.getObjectives();
		if(!objectives.isEmpty()) {
			myDedaleAgent.setTargetNode(objectives.get(0));
		}
		
		System.out.println(myDedaleAgent.getLocalName()+" ____________________START COLLECT_________________");
		System.out.println(myDedaleAgent.getLocalName()+" objectifs : " +myDedaleAgent.getObjectives().toString());

	}

	@Override
	public void action() {
		//SET MAINBEHAVIOUR
		myDedaleAgent.setMainBehaviour(this);
		myDedaleAgent.setPriority(40);
		String myPosition=myDedaleAgent.getCurrentPosition();
		if (myPosition!=null){
			myDedaleAgent.setPosition(myPosition);
			boolean moved = false;
			
			//OBSERVATIONS ET MAJ
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=myDedaleAgent.observe();//myPosition
			MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);

			String nextNode=null;
			//Condition d'arret : check si plus de noeuds ouverts
			if(objectives.isEmpty() && myDedaleAgent.getBackPackFreeSpace()==myDedaleAgent.getTotalSpace()){
				finished=true;
				System.out.println("AJOUTER PASSAGE AU BEHAVIOUR SUIVANT");
				System.out.println("Exploration successufully done, behaviour removed.");
			}else {
				//phase de collecte
				if(!dropPhase) {
					if(objectives.size()>0) {
						myDedaleAgent.setTargetNode(objectives.get(currentObjective));
					}
					//sur l'objectif
					if(myPosition.equals(myDedaleAgent.getTargetNode())){
						//pick
						int picked= myDedaleAgent.pick();
						if(picked>0) {
							myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
							System.out.println(myDedaleAgent.getLocalName() + "-----> Picked "+picked+" gold at "+myPosition);
						}else if(!(boolean)myDedaleAgent.getMap().getNode(myPosition).getAttribute("tresor_open")) {
							myDedaleAgent.setTargetNode(nextObj());
						}
						
						//updatemap
						lobs=myDedaleAgent.observe();
						MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
						
						//coffrevide
						if(0>=(int)myDedaleAgent.getMap().getNode(myPosition).getAttribute("gold")) {
							//maj objectif
							objectives.remove(currentObjective);
							if(currentObjective>=objectives.size()) {
								currentObjective=0;
							}
							if(objectives.size()>0) {
								myDedaleAgent.setTargetNode(objectives.get(currentObjective));
							}						}
						
						if(myDedaleAgent.getBackPackFreeSpace()==0 || objectives.isEmpty()) {
							dropPhase=true;
						}
					
					//vers objectif
					}else {
							//Calcul du chemin, MaJ de l'objectif
							if (nextNode==null){
								List<String> path = this.myDedaleAgent.getMap().getShortestPath(myPosition,myDedaleAgent.getTargetNode());
								nextNode=path.get(0);
								myDedaleAgent.setNextNode(nextNode);
							}
							myDedaleAgent.moveTo(nextNode);
							moved=true;
					}
				//phase de depot
				}else {
					Couple<List<String>, List<String>> tmp = myDedaleAgent.getMap().getPosByType("tanker");
					String goalPos;
					List<String> newPath ;
					if(myDedaleAgent.getMap().getSilloSpot()!=null){
						goalPos=myDedaleAgent.getMap().getSilloSpot();
						targetAgent="Tanker1";//hardcoded, TODO
						newPath = myDedaleAgent.getMap().getShortestPath(myPosition, goalPos);
					}
					else{
						List<String> goals=tmp.getRight();
						newPath = myDedaleAgent.getMap().getShortestPathOpenNodes(myPosition, goals);
						/*if(newPath.size()>1) {
							myDedaleAgent.setTargetNode(newPath.get(newPath.size()-1));//TODO exception
							//targetAgent = goalsId.get(goals.indexOf(goalPos));
							
						}*/
						targetAgent="Tanker1";//hardcoded, TODO
						
						
					}
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
							if(myDedaleAgent.emptyMyBackPack(targetAgent)) {
								System.out.println(myDedaleAgent.getLocalName() +" -----> succes drop vers "+targetAgent+" en "+myPosition);
								dropPhase=false;
							//echec de drop
							}else {
								myAgent.addBehaviour(new RandomStepsBehaviour(myDedaleAgent, 4, false));
								System.out.println(myDedaleAgent.getLocalName() +" -----> echec drop vers "+targetAgent+" en "+myPosition);
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
	@Override
	public boolean done() {
		return finished;
	}
	
	//parcours des objectifs
	private String nextObj() {
		currentObjective++;
		if(currentObjective>=objectives.size()) {
			currentObjective=0;
		}
		return objectives.get(currentObjective);
	}
	

}