package eu.su.mas.dedaleEtu.archive.dummies;

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
import eu.su.mas.dedaleEtu.mas.behaviours.common.ReceiveMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.SendMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;


public class ExploCollectorMultiBehaviour extends DedaleSimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;


	
	private String previousPosition;
	
	private String[] agentsIds;
	
	private CollectMultiAgent myDedaleAgent;


	public ExploCollectorMultiBehaviour(final CollectMultiAgent myagent) {
		super(myagent);
		this.myDedaleAgent = myagent;
		
		this.previousPosition=null;
		this.agentsIds = myDedaleAgent.getIdList();
	}

	@Override
	public void action() {
		//set main behaviour
		myDedaleAgent.setMainBehaviour(this);
		boolean blocked=false;
		String myPosition=myDedaleAgent.getCurrentPosition();
		if (myPosition!=null){
			
			myDedaleAgent.setPosition(myPosition);
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=myDedaleAgent.observe();//myPosition
			try {
				this.myDedaleAgent.doWait(100);
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			

			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			String nextNode=null;
			MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
			

			//3) while openNodes is not empty, continues.
			if (myDedaleAgent.getOpenNodes().isEmpty()){
				//Explo finished
				finished=true;
				//myDedaleAgent.addBehaviour(new RandomWalkBehaviour(myDedaleAgent, agentsIds));
				if(myDedaleAgent instanceof CollectMultiAgent) {
					myDedaleAgent.addBehaviour(new CollectMultiBehaviour((CollectMultiAgent)myDedaleAgent));

				}
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
			
			
			//GESTION DES BLOCAGES
			//
			
			if(previousPosition !=null && previousPosition.equals(myPosition)) {
				blocked=true;
				myDedaleAgent.incBlockedSince();
				if(nextNode!=null) {
					//System.out.println(this.myDedaleAgent.getLocalName()+" est bloque, objectif : " + nextNode.toString() );
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
