package eu.su.mas.dedaleEtu.mas.behaviours.explorer;

import java.util.Iterator;
import java.util.List;

import org.graphstream.graph.Node;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.yours.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;


//ouverture des coffres
public class OpenBehaviour extends DedaleSimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;
	
	
	
	private ExploreMultiAgent myDedaleAgent;
	
	private boolean keepingPhase, helpNeeded;

	private List<String> objectives;
	
	private int currentObjective;
	
	private String keepingNode, keepedNode;

	public OpenBehaviour(final ExploreMultiAgent myagent) {
		super(myagent);
		this.myDedaleAgent = myagent;
		this.keepingPhase = false;
		this.helpNeeded=false;
		this.keepingNode=null;
		this.keepedNode=null;
		this.currentObjective = 0;
		
		myDedaleAgent.generateObjectives();
		this.objectives=myDedaleAgent.getObjectives();
		if(!objectives.isEmpty()) {
			myDedaleAgent.setTargetNode(objectives.get(0));
		}
		
		System.out.println(myDedaleAgent.getLocalName()+" ____________________START OPEN____________________");
		System.out.println(myDedaleAgent.getLocalName()+" objectifs : " +myDedaleAgent.getObjectives().toString());

	}

	@Override
	public void action() {
		//SET MAINBEHAVIOUR
				myDedaleAgent.setMainBehaviour(this);
				
				String myPosition=myDedaleAgent.getCurrentPosition();
				if (myPosition!=null){
					myDedaleAgent.setPosition(myPosition);
					boolean moved = false;
					
					//OBSERVATIONS ET MAJ
					List<Couple<String,List<Couple<Observation,Integer>>>> lobs=myDedaleAgent.observe();//myPosition
					MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);

					String nextNode=null;
					//Condition d'arret : check si plus de noeuds ouverts
					if(myDedaleAgent.getObjectives().isEmpty()){
						finished=true;
						System.out.println("AJOUTER PASSAGE AU BEHAVIOUR SUIVANT");
						System.out.println(myDedaleAgent.getLocalName()+" -----> Opening successufully done, behaviour removed.");
					}else {
						//phase d'ouverture
						if(!keepingPhase) {
							myDedaleAgent.setPriority(50);
							if(objectives.size()>0) {
								myDedaleAgent.setTargetNode(objectives.get(currentObjective));
							}
							//check si le coffre a ete ouvert
							if((boolean)myDedaleAgent.getMap().getNode(myDedaleAgent.getTargetNode()).getAttribute("tresor_open")) {
								objectives.remove(currentObjective);
								if(currentObjective>=objectives.size()) {
									currentObjective=0;
								}
								if(objectives.size()>0) {
									myDedaleAgent.setTargetNode(objectives.get(currentObjective));
								}
							}
							else {
								//sur objectif
								if(myPosition.equals(myDedaleAgent.getTargetNode())){
									if((boolean)myDedaleAgent.getMap().getNode(myPosition).getAttribute("tresor_open")) {
										System.out.println(myDedaleAgent.getLocalName()+" -----> Deja open on "+myPosition);
										//maj objectif
										objectives.remove(currentObjective);
										if(currentObjective>=objectives.size()) {
											currentObjective=0;
										}
										if(objectives.size()>0) {
											myDedaleAgent.setTargetNode(objectives.get(currentObjective));
										}
									}
									else {
										boolean openSucces=myDedaleAgent.openLock(Observation.GOLD);
										//OBSERVATIONS ET MAJ
										lobs=myDedaleAgent.observe();//myPosition
										MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
										//succes ouverture
										if(openSucces) {
											System.out.println(myDedaleAgent.getLocalName()+" -----> Open on "+myPosition);
											if(helpNeeded) {
												//Envoie du message de fin
											}
											
											//maj objectif
											objectives.remove(currentObjective);
											if(currentObjective>=objectives.size()) {
												currentObjective=0;
											}
											if(objectives.size()>0) {
												myDedaleAgent.setTargetNode(objectives.get(currentObjective));
											}
											keepedNode=myPosition;
											keepingPhase=true;
										}	
										//echec ouverture
										else{
											if(!(boolean)myDedaleAgent.getMap().getNode(myPosition).getAttribute("tresor_open")) {
												//demande aide
											}
											
											//ouvert par autre agent
											else {
												//maj objectif
												objectives.remove(currentObjective);
												if(currentObjective>=objectives.size()) {
													currentObjective=0;
												}
												if(objectives.size()>0) {
													myDedaleAgent.setTargetNode(objectives.get(currentObjective));
												}
											}
										}
									}
								}
								//vers objectif
								else {
									//Calcul du chemin, MaJ de l'objectif
									List<String> path = this.myDedaleAgent.getMap().getShortestPath(myPosition,myDedaleAgent.getTargetNode());
									nextNode=path.get(0);
									myDedaleAgent.setNextNode(nextNode);
									myDedaleAgent.moveTo(nextNode);
									moved=true;
								}
							}
							
						}
						//phase de garde
						else {
							myDedaleAgent.setPriority(10);
							//premier tour : init keepingNode
							if(this.keepingNode==null) {
								String tmpNode=getKeepingNode(myPosition);
								if(tmpNode==null) {
									keepingPhase=false;
								}else {
									keepingNode=tmpNode;
									myDedaleAgent.setTargetNode(tmpNode);
								}
							
							}else {
								myDedaleAgent.setTargetNode(keepingNode);
								if(!myPosition.equals(myDedaleAgent.getTargetNode())) {
									myDedaleAgent.doWait(100);
									//Calcul du chemin, MaJ de l'objectif
									List<String> path = this.myDedaleAgent.getMap().getShortestPath(myPosition,myDedaleAgent.getTargetNode());
									nextNode=path.get(0);//bug liste vide gprime ?
									myDedaleAgent.setNextNode(nextNode);
									myDedaleAgent.moveTo(nextNode);
									moved=true;
								}
								if((int)myDedaleAgent.getMap().getNode(keepedNode).getAttribute("gold")<=0){
									keepingPhase=false;
									keepingNode=null;
									keepedNode=null;
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
	
	public String getKeepingNode(String toKeep) {
		MapRepresentation map = myDedaleAgent.getMap();
		Iterator<Node> it = map.getNode(toKeep).getNeighborNodeIterator();
		int cpt=0;
		String voisin=null;
		String prec=null;
		//+2 voisin, garde sur le tresor
		while(it.hasNext()) {
			cpt++;
			voisin=it.next().getId();
			if(cpt==2) {
				return toKeep;
			}
		}
		
		prec=toKeep;
		cpt=2;
		
		//tant que les voisin ont degre 2
		while(cpt==2) {
			cpt=0;
			it = map.getNode(voisin).getNeighborNodeIterator();
			while(it.hasNext()) {
				cpt++;
				String tmp=it.next().getId();
				if(!tmp.equals(prec)) {
					voisin=tmp;
				}
				if(cpt==3) {
					return prec;
				}
			}
		}
		//couloir
		return null;
		
		
	}
	

}
