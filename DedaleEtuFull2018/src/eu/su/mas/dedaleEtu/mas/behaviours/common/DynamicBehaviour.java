package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.archive.dummies.CollectMultiBehaviour;
import eu.su.mas.dedaleEtu.archive.dummies.SimpleBlockingSendMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.agents.yours.CollectMultiAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

public class DynamicBehaviour extends DedaleSimpleBehaviour{

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */


	
	private String previousPosition;
	
	private String[] agentsIds;
	
	private DedaleAgent myDedaleAgent;


	public DynamicBehaviour(final DedaleAgent myagent) {
		super(myagent);
		this.myDedaleAgent = myagent;

	}

	@Override
	public void action() {
		
		/*if(myDedaleAgent.getMap()==null) {
			System.out.println("CREATION MAP DE PORC");
			myDedaleAgent.setMap(new MapRepresentation());
		}*/
		
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
			
			//check si l'agent est bloqué
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
