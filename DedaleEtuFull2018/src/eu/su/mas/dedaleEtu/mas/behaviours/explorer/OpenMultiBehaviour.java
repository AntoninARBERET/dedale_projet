package eu.su.mas.dedaleEtu.mas.behaviours.explorer;

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
import eu.su.mas.dedaleEtu.mas.agents.yours.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
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
public class OpenMultiBehaviour extends DedaleSimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */


	
	private String previousPosition;
	
	private String[] agentsIds;
	
	private ExploreMultiAgent myDedaleAgent;


	public OpenMultiBehaviour(final ExploreMultiAgent myagent) {
		super(myagent);
		this.myDedaleAgent = myagent;
		
		this.previousPosition=null;
		this.agentsIds = myDedaleAgent.getIdList();
		System.out.println("Start opening "+myDedaleAgent.getClosedTresor());
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
			
			MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
			
		
			//Plus de tresor ferm�s
			if (myDedaleAgent.getClosedTresor().isEmpty()){
				finished=true;
				myDedaleAgent.addBehaviour(new RandomWalkBehaviour(myDedaleAgent));
				System.out.println("Opening successufully done no more closed treasure, behaviour removed.");
			//Plus de tresor ouvrable
			}else if(myDedaleAgent.getOpenable().isEmpty()){
				finished=true;
				myDedaleAgent.addBehaviour(new RandomWalkBehaviour(myDedaleAgent));
				System.out.println("Opening successufully done no more openable treasure, behaviour removed.");
			}
			else{
				//si sur target
				if(myPosition.equals(myDedaleAgent.getTargetNode())){
					System.out.println(myDedaleAgent.getLocalName() + "-----> " +myDedaleAgent.getMyExpertise()+ " expertise to "+ lobs);
					if(myDedaleAgent.openLock(Observation.GOLD)) {
						myDedaleAgent.getOpenTresor().add(myPosition);
						myDedaleAgent.getClosedTresor().remove(myPosition);
						myDedaleAgent.setTargetNode(null);
						System.out.println(myDedaleAgent.getLocalName() + "-----> Open at "+myPosition);
					}
					lobs=myDedaleAgent.observe();
					MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
				}else {
					
					List<String> newPath = myDedaleAgent.getMap().getShortestPathOpenNodes(myPosition, myDedaleAgent.getOpenable());
					myDedaleAgent.setTagetPath(newPath);
					myDedaleAgent.setTargetNode(newPath.get(newPath.size()-1));
					nextNode=newPath.get(0);
					System.out.println(myDedaleAgent.getLocalName() +" -------> chose target in "+ myDedaleAgent.getOpenable()+" from "+myPosition +" next node ="+nextNode);
					
					lobs=myDedaleAgent.observe();
					MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
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

	@Override
	public boolean done() {
		return finished;
	}
	
	

}
