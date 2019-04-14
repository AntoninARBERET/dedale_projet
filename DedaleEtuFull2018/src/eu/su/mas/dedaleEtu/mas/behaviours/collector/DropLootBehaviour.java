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
public class DropLootBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */


	
	private String previousPosition;
	
	private String[] agentsIds;
	
	private CollectMultiAgent myDedaleAgent;
	
	private String targetAgent;


	public DropLootBehaviour(final CollectMultiAgent myagent) {
		super(myagent);
		this.myDedaleAgent = myagent;
		
		this.previousPosition=null;
		this.agentsIds = myDedaleAgent.getIdList();
		targetAgent=null;
		System.out.println("Start opening "+myDedaleAgent.getClosedTresor());
		myDedaleAgent.setTargetNode(null);
		
	}

	@Override
	public void action() {
		
	
		
		//0) Retrieve the current position
		
		boolean blocked=false;
		
		String myPosition=myDedaleAgent.getCurrentPosition();
		int freeSpace = myDedaleAgent.getBackPackFreeSpace();
		int totalSpace = myDedaleAgent.getTotalSpace();
		if(freeSpace==totalSpace) {
			finished=true;
			myDedaleAgent.addBehaviour(new CollectMultiBehaviour(myDedaleAgent));
		}
		else {
			if (myPosition!=null){
				
				myDedaleAgent.setPosition(myPosition);
				
				
				
				//List of observable from the agent's current position
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs=myDedaleAgent.observe();//myPosition
				//System.out.println(lobs.toString());
				/**
				 * Just added here to let you see what the agent is doing, otherwise he will be too quick
				 */
				try {
					this.myDedaleAgent.doWait(100);
				} catch (Exception e) {
					e.printStackTrace();
				}

				//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
				String nextNode=null;
				
				MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
				
			
					
					if(myPosition.equals(myDedaleAgent.getTargetNode())){
						finished = myDedaleAgent.emptyMyBackPack(targetAgent);
						MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
					}else {
						Couple<List<String>, List<String>> tmp = myDedaleAgent.getMap().getPosByType("tanker");
						List<String> goals=tmp.getRight();
						List<String> goalsId=tmp.getLeft();

						if(goals.size()==0) {
							myAgent.addBehaviour(new RandomStepsBehaviour(myDedaleAgent, 4));
						}
						
						if(goals.size()>0) {
							System.out.println("GOALS " + goals);
							List<String> newPath = myDedaleAgent.getMap().getShortestPathOpenNodes(myPosition, goals);
							String goalPos = newPath.get(newPath.size()-1);
							targetAgent = goalsId.get(goals.indexOf(goalPos));
							System.out.println("TARGET AGENT "+ targetAgent);
							myDedaleAgent.setTagetPath(newPath);
							myDedaleAgent.setTargetNode(newPath.get(newPath.size()-2));
							nextNode=newPath.get(0);
							myDedaleAgent.moveTo(nextNode);
						}
					}
			
					
				
				
				//check si l'agent est bloqué
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
				
		}
	}
		
		
			

		
	

	@Override
	public boolean done() {
		return finished;
	}
	
	

}
