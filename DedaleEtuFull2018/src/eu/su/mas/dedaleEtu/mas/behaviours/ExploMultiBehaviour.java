package eu.su.mas.dedaleEtu.mas.behaviours;

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
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
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
public class ExploMultiBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */


	
	private String previousPosition;
	
	private String[] agentsIds;
	
	private DedaleAgent myDedaleAgent;


	public ExploMultiBehaviour(final DedaleAgent myagent,  String[] agentsIds) {
		super(myagent);
		this.myDedaleAgent = myagent;
		
		this.previousPosition=null;
		this.agentsIds = agentsIds;
	}

	@Override
	public void action() {
		
		if(myDedaleAgent.getMap()==null) {
			myDedaleAgent.setMap(new MapRepresentation());
		}
		
		//0) Retrieve the current position
		
		boolean blocked=false;
		
		String myPosition=myDedaleAgent.getCurrentPosition();
		
		
	
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
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			
			//1) remove the current node from openlist and add it to closedNodes.
			myDedaleAgent.getClosedNodes().add(myPosition);
			myDedaleAgent.getOpenNodes().remove(myPosition);
			//incomplet
			Couple<String, List<Couple<Observation, Integer>>> tmp = iter.next();
			List<Couple<Observation, Integer>> obs =tmp.getRight();
			//System.out.println("Courant "+myPosition+" iterator "+tmp.getLeft());
			
			//recup des informations
			int obsGold=-1;
			boolean obsTresorOpen = false;
			int obsLockPicking=-1;
			int obsForce=-1;
			boolean obsWumpus = false;
			Couple<String, String> obsAgent=null;
			
			
			for(Couple<Observation, Integer> c : obs) {
				switch (c.getLeft().getName()) {
					case("Gold"):
						obsGold=c.getRight().intValue();
						break;
					case("LockIsOpen"):
						if(c.getRight().intValue()==1) {
							obsTresorOpen=true;
						}
						break;
					case("LockPicking"):
						obsLockPicking=c.getRight().intValue();
						break;
					case("Strength"):
						obsForce=c.getRight().intValue();
						break;
					case("Wumpus"):
						if(c.getRight().intValue()==1) {
							obsWumpus=true;
						}
						break;
					
				}
			}
			this.myDedaleAgent.getMap().addNode(myPosition, false, obsGold, obsTresorOpen, obsLockPicking, obsForce, obsWumpus, null, new Date());
			
			while(iter.hasNext()){
				tmp = iter.next();
				
				obs = tmp.getRight();
				
				String nodeId=tmp.getLeft();
				
				// Pas de réexploration
				if (!myDedaleAgent.getClosedNodes().contains(nodeId)){
					if (!myDedaleAgent.getOpenNodes().contains(nodeId)){
						myDedaleAgent.getOpenNodes().add(nodeId);
						//Incomplet
						//recup des informations
						obsGold=-1;
						obsTresorOpen = false;
						obsLockPicking=-1;
						obsForce=-1;
						obsWumpus = false;
						obsAgent=null;
						
						
						for(Couple<Observation, Integer> c : obs) {
							switch (c.getLeft().getName()) {
								case("Gold"):
									obsGold=c.getRight().intValue();
									break;
								case("LockIsOpen"):
									if(c.getRight().intValue()==1) {
										obsTresorOpen=true;
									}
									break;
								case("LockPicking"):
									obsLockPicking=c.getRight().intValue();
									break;
								case("Strength"):
									obsForce=c.getRight().intValue();
									break;
								case("Wumpus"):
									if(c.getRight().intValue()==1) {
										obsWumpus=true;
									}
									break;
								
							}
						}
						this.myDedaleAgent.getMap().addNode(nodeId, true, obsGold, obsTresorOpen, obsLockPicking, obsForce, obsWumpus, null, new Date());
						
						
						this.myDedaleAgent.getMap().addEdge(myPosition, nodeId);	
					}else{
						//the node exist, but not necessarily the edge
						this.myDedaleAgent.getMap().addEdge(myPosition, nodeId);
					}
					
				}
			}
			
			

			//3) while openNodes is not empty, continues.
			if (myDedaleAgent.getOpenNodes().isEmpty()){
				//Explo finished
				finished=true;
				myDedaleAgent.addBehaviour(new RandomWalkBehaviour(myDedaleAgent, agentsIds));
				System.out.println("Exploration successufully done, behaviour removed.");
			}else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNode==null){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					//nextNode=this.myDedaleAgent.getMap().getShortestPath(myPosition, myDedaleAgent.getOpenNodes().get(0)).get(0);
					nextNode=this.myDedaleAgent.getMap().getShortestPathOpenNodes(myPosition, myDedaleAgent.getOpenNodes()).get(0);
					myDedaleAgent.setNextNode(nextNode);
				}
				myDedaleAgent.moveTo(nextNode);
			}
			
			//check si l'agent est bloqué
			if(previousPosition !=null && previousPosition.equals(myPosition)) {
				blocked=true;
				myDedaleAgent.incBlockedSince();
				if(nextNode!=null) {
					System.out.println(this.myDedaleAgent.getLocalName()+" est bloque, objectif : " + nextNode.toString() );
				}
				
				if(myDedaleAgent.getBlockedSince()<2) {
					myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1", agentsIds));
				}
				else if(myDedaleAgent.getBlockedSince()>=2) {
					myDedaleAgent.addBehaviour(new SimpleBlockingSendMessageBehaviour(myDedaleAgent, "-1", agentsIds));
				}
			

				myDedaleAgent.addBehaviour(new ReceiveMessageBehaviour(myDedaleAgent));
			}else{
				myDedaleAgent.resetBlockedSince();

			}
			previousPosition = myPosition;
			

		}
	}

	@Override
	public boolean done() {
		return finished;
	}
	
	

}
