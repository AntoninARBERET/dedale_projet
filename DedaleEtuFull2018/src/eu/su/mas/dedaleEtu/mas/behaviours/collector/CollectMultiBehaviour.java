package eu.su.mas.dedaleEtu.mas.behaviours.collector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.CollectMultiAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.RandomStepsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.RandomWalkBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.ReceiveMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.SendMapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.SimpleBlockingSendMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;


/**
 * This behaviour allows an agent to explore the environment and learn the associated topological map.
 * The algorithm is a pseudo - DFS computationally consuming because its not optimised at all.</br>
 * 
 * When all the nodes around him are visited, the agent randomly select an open node and go there to restart its dfs.</br> 
 * This (non optimal) behaviour is done until all nodes are explored. </br> 
 * 
 * Warning, this behaviour does not save the content of visited nodes, only the topology.</br> 
 * Warning, this behaviour is a solo exploration and does not take into account the presence of other agents (or well) and indefinitely tries to reach its target node
 * @author hc
 *
 */
public class CollectMultiBehaviour extends DedaleSimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */


	
	private String previousPosition;
	
	private String[] agentsIds;
	
	private CollectMultiAgent myDedaleAgent;
	
	private boolean noMoreLoot, dropPhase;
	private String targetAgent;



	public CollectMultiBehaviour(final CollectMultiAgent myagent) {
		super(myagent);
		this.myDedaleAgent = myagent;
		
		this.previousPosition=null;
		this.agentsIds = myDedaleAgent.getIdList();
		noMoreLoot = false;
		dropPhase=false;
		targetAgent = null;
		System.out.println("Start collecting "+myDedaleAgent.getLootable());
		
	}

	@Override
	public void action() {
		
	
		
		//0) Retrieve the current position
		
		boolean blocked=false;
		String myPosition=myDedaleAgent.getCurrentPosition();
		if (myPosition!=null){
			
			
			/**
			 * Just added here to let you see what the agent is doing, otherwise he will be too quick
			 */
			try {
				this.myDedaleAgent.doWait(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			myDedaleAgent.setPosition(myPosition);
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=myDedaleAgent.observe();//myPosition
			String nextNode=null;
			
			MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
			
			
			
			//Si sac non plein et tresor restant : cherche plus de tresor
			if(myDedaleAgent.getBackPackFreeSpace()>0 && !myDedaleAgent.getOpenLootable().isEmpty()) {
				System.out.println(myDedaleAgent.getLocalName() + "-----> boucle1");
				if(dropPhase) {
					dropPhase=false;
					myDedaleAgent.setTargetNode(null);
					System.out.println(myDedaleAgent.getLocalName()+" ----> Going for more");
				}
				
				//si sur target
				if(myPosition.equals(myDedaleAgent.getTargetNode())){
					//ramassage
					int picked= myDedaleAgent.pick();
					if(picked>0) {
						System.out.println(myDedaleAgent.getLocalName() + "-----> Picked "+picked+" gold at "+myPosition);
					}else if(!(boolean)myDedaleAgent.getMap().getNode(myPosition).getAttribute("tresor_open")) {
						myDedaleAgent.getToRetry().add(myPosition);
					}
					lobs=myDedaleAgent.observe();//myPosition
					MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
				}else {
					//choix dans lootable ouvert
					List<String> goals = myDedaleAgent.getOpenLootable();
					//System.out.println("Choix dans les lootable ouverts : "+goals);
					//si pas de choix, choix dans lootable sans le deja testes
					if(goals.size()==0) {
						goals=myDedaleAgent.getLootableWhithout(myDedaleAgent.getToRetry());
						//si toujours pas de choix, reset des testes et random steps
						if(goals.size()==0) {
							myDedaleAgent.resetToRetry();
							goals=myDedaleAgent.getLootableWhithout(myDedaleAgent.getToRetry());
							myAgent.addBehaviour(new RandomStepsBehaviour(myDedaleAgent, 4, false));
						}
					}
					else {
						List<String> newPath = myDedaleAgent.getMap().getShortestPathOpenNodes(myPosition, goals);
						myDedaleAgent.setTagetPath(newPath);
						myDedaleAgent.setTargetNode(newPath.get(newPath.size()-1));//crash here
						nextNode=newPath.get(0);
						myDedaleAgent.moveTo(nextNode);
					}
				}
			}
			else if(myDedaleAgent.getOpenLootable().isEmpty()&&!noMoreLoot){
				System.out.println(myDedaleAgent.getLocalName() + "-----> boucle2");
				List<String> goals = myDedaleAgent.getOpenLootable();
				if(goals.size()>0) {
					List<String> newPath = myDedaleAgent.getMap().getShortestPathOpenNodes(myPosition, goals);
					myDedaleAgent.setTagetPath(newPath);
					myDedaleAgent.setTargetNode(newPath.get(newPath.size()-1));//crash here
					nextNode=newPath.get(0);
					myDedaleAgent.moveTo(nextNode);
				}
				
				else {
					myAgent.addBehaviour(new RandomStepsBehaviour(myDedaleAgent, 4, false));
				}
			}
			//si sac plein ou plus de tresor, depot
			else {
				System.out.println(myDedaleAgent.getLocalName() + "-----> boucle3");
				if(!dropPhase) {
					dropPhase=true;
					myDedaleAgent.setTargetNode(null);
					System.out.println(myDedaleAgent.getLocalName()+" ----> Need to empty my backpack");
				}
				
				if(myPosition.equals(myDedaleAgent.getTargetNode())){
					myDedaleAgent.emptyMyBackPack(targetAgent);
					System.out.println(myDedaleAgent.getLocalName() + "-----> on target");
					lobs=myDedaleAgent.observe();//myPosition
					MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
				}else {
					Couple<List<String>, List<String>> tmp = myDedaleAgent.getMap().getPosByType("tanker");//renvoie null
					List<String> goals=tmp.getRight();
					List<String> goalsId=tmp.getLeft();

					if(goals.size()==0) {
						System.out.println(myDedaleAgent.getLocalName() + "-----> size goal =0");
						myAgent.addBehaviour(new RandomStepsBehaviour(myDedaleAgent, 4, false));
					}
					
					if(goals.size()>0) {
						System.out.println(myDedaleAgent.getLocalName()+" -----> case goal size>0 "+goals + myDedaleAgent.getTargetNode());
						List<String> newPath = myDedaleAgent.getMap().getShortestPathOpenNodes(myPosition, goals);
						String goalPos = newPath.get(newPath.size()-1);
						targetAgent = goalsId.get(goals.indexOf(goalPos));
						System.out.println("TARGET AGENT "+ targetAgent);
						myDedaleAgent.setTagetPath(newPath);
						if(newPath.size()==1) {
							System.out.println(myDedaleAgent.getLocalName()+" -----> case path taille 1 "+goals + targetAgent);
							myDedaleAgent.setTargetNode(myPosition);
						}else {
							myDedaleAgent.setTargetNode(newPath.get(newPath.size()-2));
							nextNode=newPath.get(0);
							myDedaleAgent.moveTo(nextNode);
						}
						if(previousPosition !=null && previousPosition.equals(myPosition)) {
							System.out.println(myDedaleAgent.getLocalName()+" -----> goals : "+goals);
						}
						
					}
				}
			}
			
			
			
			
			//check si l'agent est bloque
			if(previousPosition !=null && previousPosition.equals(myPosition)) {
				
				
				blocked=true;
				myDedaleAgent.incBlockedSince();
				if(nextNode!=null) {
					System.out.println(this.myDedaleAgent.getLocalName()+" est bloque, next : " + nextNode.toString() );
				}
				
				if(myDedaleAgent.getBlockedSince()<2) {
					myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
				}
				else if(myDedaleAgent.getBlockedSince()>=2) {
					myDedaleAgent.addBehaviour(new SimpleBlockingSendMessageBehaviour(myDedaleAgent, "-1"));
				}
			

				myDedaleAgent.addBehaviour(new ReceiveMessageBehaviour(myDedaleAgent));
			}else{
				myDedaleAgent.resetBlockedSince();

			}
			previousPosition = myPosition;
			

		}
		noMoreLoot = myDedaleAgent.getLootable().isEmpty();//to check
		if(noMoreLoot && myDedaleAgent.getBackPackFreeSpace()==0) {
			finished = noMoreLoot;
			myDedaleAgent.addBehaviour(new RandomWalkBehaviour(myDedaleAgent));
			System.out.println(myDedaleAgent.getLocalName()+" -----> Fin de la collecte.");
		}
		
	}

	@Override
	public boolean done() {
		return finished;
	}
	
	

}
