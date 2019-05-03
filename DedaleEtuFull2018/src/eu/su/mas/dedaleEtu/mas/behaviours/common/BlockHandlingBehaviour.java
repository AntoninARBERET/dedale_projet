package eu.su.mas.dedaleEtu.mas.behaviours.common;

import java.util.Iterator;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.BlockingSendMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.SendMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.Block;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

//gestion d'un blocage
public class BlockHandlingBehaviour extends DedaleSimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;

	private DedaleAgent myDedaleAgent;
	
	private DedaleSimpleBehaviour callingBehaviour;
	
	public Block b;
	
	private String solution;


	public BlockHandlingBehaviour(DedaleAgent myagent, Block b, DedaleSimpleBehaviour callingBehaviour) {
		super(myagent);
		this.myDedaleAgent = myagent;
		this.b=b;
		this.callingBehaviour=callingBehaviour;
		callingBehaviour.block();
		this.solution=null;
		
		//recherche du noeud le plus proche qui ne gene pas
		String position = myDedaleAgent.getCurrentPosition();
		List<String> matePath = myDedaleAgent.getMap().getShortestPath(position, b.getObjective());
		
		//si voisin direct pas dans path
		Iterator<String> voisIt = myDedaleAgent.getMap().getNeighbours(position).iterator();
		while(voisIt.hasNext() && solution==null){
			String s=voisIt.next();
			if(!matePath.contains(s)&& !s.equals(b.getOrigin())&&!s.equals(myDedaleAgent.getPosition())) {
				solution=s;
			}
		}
		
		//sinon recherche un voisin d'un noeud du path
		Iterator<String> pathIt = matePath.iterator();
		while(pathIt.hasNext() && solution==null){
			String n=pathIt.next();
			voisIt = myDedaleAgent.getMap().getNeighbours(n).iterator();
			while(voisIt.hasNext() && solution==null){
				String s=voisIt.next();
				if(!matePath.contains(s) && !s.equals(b.getOrigin())&&!s.equals(myDedaleAgent.getPosition())) {
					solution=s;
				}
			}
		}
		System.out.println(myDedaleAgent.getLocalName()+" -----> solution : "+solution+" pour "+b.toString());

		
	}

	@Override
	public void action() {
		super.action();
		//SET MAINBEHAVIOUR
		myDedaleAgent.setMainBehaviour(this);
		myDedaleAgent.setPriority(60);
		//pas de solution 
		if(solution==null) {
			finished=true;
			myDedaleAgent.addBehaviour(new BlockingSendMessageBehaviour(myDedaleAgent,"-1", b.getPriority()+1, myDedaleAgent.getNextNode(), myDedaleAgent.getTargetNode()));
			callingBehaviour.restart();
		}
		
		//solution
		else {
			boolean moved=false;
			String myPosition=myDedaleAgent.getCurrentPosition();
			myDedaleAgent.setTargetNode(solution);
			if (myPosition!=null){
				myDedaleAgent.setPosition(myPosition);
				
				//OBSERVATIONS ET MAJ
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs=myDedaleAgent.observe();//myPosition
				MapRepresentation.updateMapWithObs( myDedaleAgent,  myPosition , lobs);
				String nextNode=null;
				
				//arrive
				if (myPosition.equals(myDedaleAgent.getTargetNode())){
					finished=true;
					callingBehaviour.restart();
				}else{
					//Calcul du chemin, MaJ de l'objectif
					if (nextNode==null){
						List<String> path = this.myDedaleAgent.getMap().getShortestPath(myPosition, myDedaleAgent.getTargetNode());
						nextNode=path.get(0);
						myDedaleAgent.setNextNode(nextNode);
					}
					boolean tmp=myDedaleAgent.moveTo(nextNode);
					if(!tmp) {
						System.out.println(myDedaleAgent.getLocalName() +"Fail move : " + myPosition + "next" +nextNode );

					}
					moved=true;
				}
				
				
				//GESTION DES BLOCAGES
				//
				//position inchangee meme si moveTo
				if(myDedaleAgent.getPreviousPos() !=null && myDedaleAgent.getPreviousPos().equals(myPosition) && moved  && nextNode!=null) {
					System.out.println(myDedaleAgent.getLocalName() +" -----> blocage dans handling prev : " +myDedaleAgent.getPreviousPos() + " current "+ myPosition + "next" +nextNode );
					System.out.println(myDedaleAgent.getLocalName() +" -----> block associe : " +b.toString() );
					myDedaleAgent.incBlockedSince();
					//premier blocage, envoie de map
					if(myDedaleAgent.getBlockedSince()<2) {
						myDedaleAgent.addBehaviour(new SendMapBehaviour(myDedaleAgent, "-1"));
					}
					//deuxieme blocage, envoie message
					else if(myDedaleAgent.getBlockedSince()<=10&&myDedaleAgent.isBlockDelayExpired()){
						myDedaleAgent.addBehaviour(new BlockingSendMessageBehaviour(myDedaleAgent, "-1", myDedaleAgent.getPriority(), nextNode, myDedaleAgent.getTargetNode()));
					}else {
						myDedaleAgent.addBehaviour(new RandomStepsBehaviour(myDedaleAgent, 2, false));
					}
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
