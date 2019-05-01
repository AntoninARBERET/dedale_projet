package eu.su.mas.dedaleEtu.mas.behaviours.collector;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.yours.CollectMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.BlockingSendMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.CheckTankerBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.SendMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

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
public class ExploCollectorBehaviour extends DedaleSimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	/**
	 * Current knowledge of the agent regarding the environment
	 */


	
	private String previousPosition;
	
	
	private CollectMultiAgent myDedaleAgent;


	public ExploCollectorBehaviour(final CollectMultiAgent myagent) {
		super(myagent);
		this.myDedaleAgent = myagent;
		
		this.previousPosition=null;
	}

	@Override
	public void action() {
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
			}
			
			
			//GESTION DES BLOCAGES
			//
			
			if(previousPosition !=null && previousPosition.equals(myPosition) && nextNode!=null) {
				myDedaleAgent.incBlockedSince();
				if(myDedaleAgent.getBlockedSince()<2) {
					myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
				}
				else/* if(myDedaleAgent.getBlockedSince()<=5) */{
					myDedaleAgent.addBehaviour(new BlockingSendMessageBehaviour(myDedaleAgent, "-1", myDedaleAgent.getPriority(), nextNode, myDedaleAgent.getTargetNode()));
				}
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
