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
import eu.su.mas.dedaleEtu.mas.behaviours.common.BlockingSendMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.DedaleSimpleBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.RandomStepsBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.common.SendMapBehaviour;
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
	
	private int blockSentAt;
	
	private static int delai = 5;
	
	private String previousPos;



	
	protected void setup(){

		super.setup();
		//noeuds ouverts
		this.openNodes=new ArrayList<String>();
		//noeud fermés
		this.closedNodes=new HashSet<String>();
		this.openTresor=new ArrayList<String>();
		this.closedTresor=new ArrayList<String>();
		//nombre de tentative de deplacement depuis blocage
		this.blockedSince=0;
		this.lockingStart=null;
		//priorite
		this.priority=1;
		//noeud cible
		this.targetNode=null;
		this.tagetPath=new ArrayList<String>();
		//list objectifs
		this.objectives=new ArrayList<String>();
		//map
		this.myMap=new MapRepresentation();
		this.checkingBehaviourRunning=false;
		//compte les actions
		this.actionsCpt=0;
		//liste de ping
		this.lastPinged=new HashMap<String, Integer>();
		this.blockSentAt=-1*delai;
		this.previousPos=null;
		
		//caracteristique de l'agent
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
		

	}
	
	//appelle par les agents en fin d'action impliquant un deplacement pour gere le blocage
	public void onBlock(String nextNode) {

		this.incBlockedSince();
		//premier blocage, envoie de map
		System.out.println(getLocalName() + " since "+blockedSince +"blockat"+this.blockSentAt);
		if(this.getBlockedSince()<2) {
			
			this.addBehaviour(new SendMapBehaviour(this, "-1"));
			//this.setBlockSentAt();
		}
		else if(this.getBlockedSince()>15) {
			this.addBehaviour(new RandomStepsBehaviour(this, 2, false));
		}
		//deuxieme blocage, envoie message
		else if(this.isBlockDelayExpired()){
			this.setBlockSentAt();
			this.addBehaviour(new BlockingSendMessageBehaviour(this, "-1", this.getPriority(), nextNode, this.getTargetNode()));
		}
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
		this.blockSentAt=-1*delai;
	}
	
	public int getBlockSentAt(){
		return blockSentAt;
	}
	
	public void setBlockSentAt(){
		blockSentAt = blockedSince;
	}
	
	public boolean isBlockDelayExpired() {
		return (blockedSince-blockSentAt)>delai;
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

	public String getPreviousPos() {
		return previousPos;
	}


	public void setPreviousPos(String previousPos) {
		this.previousPos = previousPos;
	}

	//genere la liste d'objectif de l'agent
	public void generateObjectives() {
		if(this instanceof ExploreMultiAgent) {
			generateObjectiveOpen();
		}
		else if(this instanceof CollectMultiAgent) {
			generateObjectiveCollect();
		}
	}
	
	
	

	//genere la liste d'objecti des collecteurs
	public void generateObjectiveCollect() {
		//liste objectifs et liste infos pour le tri
		ArrayList<String> obj = new ArrayList<String>();
		ArrayList<Couple<Integer, Integer>> tmpInfos = new ArrayList<Couple<Integer,Integer>>();
		
		//recuperation des forces des collectors
		ArrayList<Integer> agentsStrenghs=new ArrayList<Integer>();
		ArrayList<Integer> agentsLP=new ArrayList<Integer>();
		
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription () ;
		sd.setType( "explorer" ); // name of the service
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
							 int val = (Integer.valueOf((String)p.getValue())).intValue();//TODO changed
							 while(cpt<agentsStrenghs.size() && agentsStrenghs.get(cpt)<val) {
								 cpt++;
							 }
							 agentsStrenghs.add(cpt, new Integer(val));
						 }
						 if(p.getName().equals("lockPicking")) {
							 int cpt=0;
							 int val = (Integer.valueOf((String)p.getValue())).intValue();
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
				
				int nbAgent =0;
				//nb agent strengh
				int nbAgentStre=0;
				int strenghSum =0;
				int neededStrengh =(int)n.getAttribute("force");
	
				Iterator<Integer> itStre= agentsStrenghs.iterator();
				while(itStre.hasNext() && strenghSum<neededStrengh) {
					nbAgentStre++;
					strenghSum+=itStre.next().intValue();
				}
				nbAgent=nbAgentStre;
				
				//nb agentLP
				int nbAgentLP=0;
				int LPSum =0;
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
					boolean over =false;
					while(it.hasNext() && !over) {
						it.next();
						if(tmpInfos.get(cpt).getLeft()>=nbAgent && tmpInfos.get(cpt).getRight()<=g) {
							over=true;
						}
						else {
							cpt++;
						}
					}
					
					
					
					obj.add(cpt, n.getId());
					tmpInfos.add(cpt,new Couple<Integer, Integer>(new Integer(nbAgent), new Integer(g)));

				}
				
			}
		}
		System.out.println(this.getLocalName()+ " -----> obj "+obj+" tmpinfo : " +tmpInfos.toString());
		this.objectives=obj;
	}
	
	//genere la liste d'objecti des ramasseurs
	public void generateObjectiveOpen() {
		//liste objectifs et liste infos pour le tri
		ArrayList<String> obj = new ArrayList<String>();
		ArrayList<Couple<Integer, Integer>> tmpInfos = new ArrayList<Couple<Integer,Integer>>();
		
		//recuperation des forces des collectors
		ArrayList<Integer> agentsStrenghs=new ArrayList<Integer>();
		ArrayList<Integer> agentsLP=new ArrayList<Integer>();
		
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription () ;
		sd.setType( "explorer" ); // name of the service
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
							 int val = (Integer.valueOf((String)p.getValue())).intValue();//TODO changed
							 while(cpt<agentsStrenghs.size() && agentsStrenghs.get(cpt)<val) {
								 cpt++;
							 }
							 agentsStrenghs.add(cpt, new Integer(val));
						 }
						 if(p.getName().equals("lockPicking")) {
							 int cpt=0;
							 int val = (Integer.valueOf((String)p.getValue())).intValue();
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
					boolean over =false;
					while(it.hasNext() && !over) {
						it.next();
						if(tmpInfos.get(cpt).getLeft()>=nbAgent && tmpInfos.get(cpt).getRight()<=g) {
							over=true;
						}
						else {
							cpt++;
						}
					}
					
					
					
					obj.add(cpt, n.getId());
					tmpInfos.add(cpt,new Couple<Integer, Integer>(new Integer(nbAgent), new Integer(g)));
				}
				
			}
		}
		this.objectives=obj;
	}
	
	
	
}
