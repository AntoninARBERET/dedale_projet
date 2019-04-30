package eu.su.mas.dedaleEtu.mas.agents.yours;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.graphstream.graph.Node;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * ExploreSolo agent. 
 * It explore the map using a DFS algorithm.
 * It stops when all nodes have been visited
 *  
 *  
 * @author hc
 *
 */

public class DedaleAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	
	public static final int pingGap = 20;

	private MapRepresentation myMap;
	

	protected String[] idList;
	/**
	 * Nodes known but not yet visited
	 */
	private List<String> openNodes;
	
	/**
	 * Visited nodes
	 */
	private Set<String> closedNodes;
	
	private String position;
	
	private String nextNode;
	
	private int blockedSince;
	
	protected int priority;
	
	private List<String> closedTresor;
	
	private List<String> openTresor;
	
	private String targetNode;
	
	private List<String> tagetPath;
	
	protected List<String> objectives;

	protected int myLockPicking;
	
	protected int myStrengh;

	protected String type;
	
	protected Date lockingStart;
	
	protected boolean checkingBehaviourRunning;
	
	protected int actionsCpt;
	
	protected HashMap<String, Integer> lastPinged;
	
	protected DedaleSimpleBehaviour mainBehaviour;



	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected void setup(){

		super.setup();
		this.openNodes=new ArrayList<String>();
		this.closedNodes=new HashSet<String>();
		this.openTresor=new ArrayList<String>();
		this.closedTresor=new ArrayList<String>();
		this.blockedSince=0;
		this.lockingStart=null;
		this.priority=1;
		this.targetNode=null;
		this.tagetPath=new ArrayList<String>();
		this.objectives=new ArrayList<String>();
		this.myMap=new MapRepresentation();
		this.checkingBehaviourRunning=false;
		this.actionsCpt=0;
		this.lastPinged=new HashMap<String, Integer>();
		
		
		for(Couple<Observation, Integer> o : getMyExpertise()) {//add to agent directly
			if(o.getLeft().equals(Observation.LOCKPICKING)) {
				myLockPicking=o.getRight().intValue();
			}
			if(o.getLeft().equals(Observation.STRENGH)) {
				myStrengh=o.getRight().intValue();
			}
		}
		final Object[] args = getArguments();
		idList = (String[])args[2];
		
		for(String id:idList) {
			lastPinged.put(id, new Integer(-1*pingGap));
		}
		//myMap=new MapRepresentation();

		//List<Behaviour> lb=new ArrayList<Behaviour>();
		
		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/
		
		//lb.add(new ExploMultiBehaviour(this,this.myMap, idList));
		
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		
		/*addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");*/

	}
	
	
	public List<String> getOpenNodes() {
		return openNodes;
	}


	public void setOpenNodes(List<String> openNodes) {
		this.openNodes = openNodes;
	}


	public Set<String> getClosedNodes() {
		return closedNodes;
	}


	public void setClosedNodes(Set<String> closedNodes) {
		this.closedNodes = closedNodes;
	}


	public void setMap(MapRepresentation myMap) {
		this.myMap=myMap;
	}


	public MapRepresentation getMap() {
		return myMap;
	}


	public String getPosition() {
		return position;
	}


	public void setPosition(String position) {
		this.position = position;
	}


	public String getNextNode() {
		return nextNode;
	}


	public void setNextNode(String nextNode) {
		this.nextNode = nextNode;
	}


	public int getBlockedSince() {
		return blockedSince;
	}


	public void incBlockedSince() {
		this.blockedSince++;
	}
	
	public void resetBlockedSince() {
		this.blockedSince=0;
	}
	

	public String[] getIdList() {
		return idList;
	}


	public int getPriority() {
		return priority;
	}


	public void setPriority(int priority) {
		this.priority = priority;
	}


	public List<String> getClosedTresor() {
		return closedTresor;
	}


	public List<String> getOpenTresor() {
		return openTresor;
	}


	public String getTargetNode() {
		return targetNode;
	}


	public void setTargetNode(String targetNode) {
		this.targetNode = targetNode;
	}


	public List<String> getTagetPath() {
		return tagetPath;
	}


	public void setTagetPath(List<String> tagetPath) {
		this.tagetPath = tagetPath;
	}


	public int getMyLockPicking() {
		return myLockPicking;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Date getLockingStart() {
		return lockingStart;
	}


	public void setLockingStart(Date lockingStart) {
		this.lockingStart = lockingStart;
	}
	

	public boolean isCheckingBehaviourRunning() {
		return checkingBehaviourRunning;
	}


	public void setCheckingBehaviourRunning(boolean checkingBehaviourRunning) {
		this.checkingBehaviourRunning = checkingBehaviourRunning;
	}
	
	public void incActionsCpt() {
		actionsCpt++;
	}
	
	public int getActionsCpt() {
		return actionsCpt;
	}

	public int getLastPing(String id) {
		return lastPinged.get(id).intValue();
	}
	
	public void setLastPing(String id, int val) {
		lastPinged.put(id, new Integer(val));
	}


	public DedaleSimpleBehaviour getMainBehaviour() {
		return mainBehaviour;
	}


	public void setMainBehaviour(DedaleSimpleBehaviour mainBehaviour) {
		this.mainBehaviour = mainBehaviour;
	}


	public List<String> getObjectives() {
		return objectives;
	}
	
	public void generateObjectives() {
		if(this instanceof ExploreMultiAgent) {
			generateObjectiveOpen();
		}
		else if(this instanceof CollectMultiAgent) {
			generateObjectiveCollect();
		}
	}
	
	public void generateObjectiveCollect() {
		//liste objectifs et liste infos pour le tri
		ArrayList<String> obj = new ArrayList<String>();
		ArrayList<Couple<Integer, Integer>> tmpInfos = new ArrayList<Couple<Integer,Integer>>();
		
		//recuperation des forces des collectors
		ArrayList<Integer> agentsStrenghs=new ArrayList<Integer>();
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription () ;
		sd.setType( "collector" ); // name of the service
		dfd.addServices(sd) ;
		DFAgentDescription[] result =null;
		try {
			result = DFService.search( this , dfd) ;
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		
		
		//creation de liste triee des forces disponibles
		for(DFAgentDescription desc : result) {
			jade.util.leap.Iterator itServ = desc.getAllServices();
			while(itServ.hasNext()) {
				 sd = (ServiceDescription)itServ.next();
				 if(!sd.getName().equals(this.getLocalName())){
					 jade.util.leap.Iterator itProp = sd.getAllProperties();
					 while(itProp.hasNext()) {
						 Property p = (Property)itProp.next();
						 if(p.getName().equals("strengh")) {
							 int cpt=0;
							 int val = ((Integer)p.getValue()).intValue();
							 while(cpt<agentsStrenghs.size() && agentsStrenghs.get(cpt)<val) {
								 cpt++;
							 }
							 agentsStrenghs.add(cpt, new Integer(val));
						 }
					 }
				}
			}
		}
		
		//parcours des noeuds avec tresor
		for(Node n : myMap.getEachNode()) {
			int g = (int)n.getAttribute("gold");
			if(g>0) {
				Iterator<String> it = obj.iterator();
				int cpt=0;
				
				
				//nb agent
				int nbAgent=1;
				int strenghSum =myStrengh;
				int neededStrengh =(int)n.getAttribute("force");
	
				Iterator<Integer> itStre= agentsStrenghs.iterator();
				while(itStre.hasNext() && strenghSum<neededStrengh) {
					strenghSum+=itStre.next().intValue();
					nbAgent++;
				}
			
				//classe en fonction du nombre min d'agent necessaire (dont l'actuel) puis du gain
				if(strenghSum>=neededStrengh) {
					while(it.hasNext() && (tmpInfos.get(cpt).getLeft()>nbAgent ||tmpInfos.get(cpt).getRight()>g)) {
						it.next();
						cpt++;
					}
					obj.add(cpt, n.getId());
					tmpInfos.add(new Couple<Integer, Integer>(new Integer(nbAgent), new Integer(g)));
				}
				
			}
		}
		this.objectives=obj;
	}
	
	public void generateObjectiveOpen() {
		//liste objectifs et liste infos pour le tri
		ArrayList<String> obj = new ArrayList<String>();
		ArrayList<Couple<Integer, Integer>> tmpInfos = new ArrayList<Couple<Integer,Integer>>();
		
		//recuperation des forces des collectors
		ArrayList<Integer> agentsStrenghs=new ArrayList<Integer>();
		ArrayList<Integer> agentsLP=new ArrayList<Integer>();
		
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription () ;
		sd.setType( "collector" ); // name of the service
		dfd.addServices(sd) ;
		DFAgentDescription[] result =null;
		try {
			result = DFService.search( this , dfd) ;
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		
		
		//creation de liste triee des forces disponibles
		for(DFAgentDescription desc : result) {
			jade.util.leap.Iterator itServ = desc.getAllServices();
			while(itServ.hasNext()) {
				 sd = (ServiceDescription)itServ.next();
				 if(!sd.getName().equals(this.getLocalName())){
					 jade.util.leap.Iterator itProp = sd.getAllProperties();
					 while(itProp.hasNext()) {
						 Property p = (Property)itProp.next();
						 if(p.getName().equals("strengh")) {
							 int cpt=0;
							 int val = ((Integer)p.getValue()).intValue();
							 while(cpt<agentsStrenghs.size() && agentsStrenghs.get(cpt)<val) {
								 cpt++;
							 }
							 agentsStrenghs.add(cpt, new Integer(val));
						 }
						 if(p.getName().equals("lockPicking")) {
							 int cpt=0;
							 int val = ((Integer)p.getValue()).intValue();
							 while(cpt<agentsLP.size() && agentsLP.get(cpt)<val) {
								 cpt++;
							 }
							 agentsLP.add(cpt, new Integer(val));
						 }
					 }
				}
			}
		}
		
		//parcours des noeuds avec tresor
		for(Node n : myMap.getEachNode()) {
			int g = (int)n.getAttribute("gold");
			if(g>0) {
				Iterator<String> it = obj.iterator();
				int cpt=0;
				
				int nbAgent =1;
				//nb agent strengh
				int nbAgentStre=1;
				int strenghSum =myStrengh;
				int neededStrengh =(int)n.getAttribute("force");
	
				Iterator<Integer> itStre= agentsStrenghs.iterator();
				while(itStre.hasNext() && strenghSum<neededStrengh) {
					nbAgentStre++;
					strenghSum+=itStre.next().intValue();
				}
				nbAgent=nbAgentStre;
				
				//nb agentLP
				int nbAgentLP=1;
				int LPSum =myLockPicking;
				int neededLP =(int)n.getAttribute("lockPicking");
	
				Iterator<Integer> itLP= agentsLP.iterator();
				while(itLP.hasNext() && LPSum<neededLP) {
					nbAgentLP++;
					LPSum+=itLP.next().intValue();
				}
				if(nbAgentLP>nbAgent) {
					nbAgent=nbAgentLP;
				}
			
				//classe en fonction du nombre min d'agent necessaire (dont l'actuel) puis du gain
				if(strenghSum>=neededStrengh &&LPSum>=neededLP) {
					while(it.hasNext() && (tmpInfos.get(cpt).getLeft()>nbAgent ||tmpInfos.get(cpt).getRight()>g)) {
						it.next();
						cpt++;
					}
					obj.add(cpt, n.getId());
					tmpInfos.add(new Couple<Integer, Integer>(new Integer(nbAgent), new Integer(g)));

				}
				
			}
		}
		this.objectives=obj;
	}
	
}
