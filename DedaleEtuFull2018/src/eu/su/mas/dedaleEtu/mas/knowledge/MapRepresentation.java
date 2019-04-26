package eu.su.mas.dedaleEtu.mas.knowledge;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Hashtable;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.yours.TankerAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.tools.Pair;

/**
 * This simple topology representation only deals with the graph, not its content.</br>
 * The knowledge representation is not well written (at all), it is just given as a minimal example.</br>
 * The viewer methods are not independent of the data structure, and the dijkstra is recomputed every-time.
 * 
 * @author hc
 */
public class MapRepresentation implements Serializable {

	public enum MapAttribute {
		open,agent,tresor_open, tresor_closed,wumpus
	}
	


	private static final long serialVersionUID = -1333959882640838272L;

	private Graph g; //data structure
	private Viewer viewer; //ref to the display
	private Integer nbEdges;//used to generate the edges ids
	private HashMap<String, Couple<String,String>> agentsInfo;
	private Graph gPrime;
	private String silloSpot;
	
	/*********************************
	 * Parameters for graph rendering
	 ********************************/
	
	private String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
	private String nodeStyle_open = "node.agent {"+"fill-color: forestgreen;"+"}";
	private String nodeStyle_agent = "node.open {"+"fill-color: blue;"+"}";
	private String nodeStyle_wumpus = "node.wumpus {"+"fill-color: red;"+"}";
	private String nodeStyle_tresor_open = "node.tresor_open {"+"fill-color: yellow;"+"}";
	private String nodeStyle_tresor_closed = "node.tresor_closed {"+"fill-color: orange;"+"}";
	
	private String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_open+nodeStyle_wumpus+nodeStyle_tresor_open+nodeStyle_tresor_closed;
	//private Hashtable<String, String[]> nodes_informations;

	
	public MapRepresentation() {
		System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		this.g= new SingleGraph("My world vision");
		this.g.setAttribute("ui.stylesheet",nodeStyle);
		this.viewer = this.g.display();
		this.nbEdges=0;
		agentsInfo=new  HashMap<String, Couple<String,String>>();
		this.gPrime=null;
		this.silloSpot=null;
		//this.nodes_informations=new Hashtable<String, String[]>();
	}

	public void display() {
		this.viewer = this.g.display();
	}
	


	
	public void addNode(DedaleAgent myDedaleAgent, String id,boolean node_open,int gold,boolean tresor_open,int lockPicking,int force, boolean wumpus, Couple<String,String> agent, Date date){
		Node n;
		if (this.g.getNode(id)==null){
			n=this.g.addNode(id);
		}
		n=this.g.getNode(id);
		
		if(tresor_open && gold>0) {
			myDedaleAgent.getClosedTresor().remove(id);
			if(!myDedaleAgent.getOpenTresor().contains(id)) {
				myDedaleAgent.getOpenTresor().add(id);
			}
			
		}else if(tresor_open && gold==0){
			if(!myDedaleAgent.getOpenTresor().contains(id)) {
				myDedaleAgent.getOpenTresor().add(id);
			}
			if(!myDedaleAgent.getClosedTresor().contains(id)) {
				myDedaleAgent.getClosedTresor().add(id);
			}
	    }else if(gold!=-1){
			if(!myDedaleAgent.getClosedTresor().contains(id)) {
				myDedaleAgent.getClosedTresor().add(id);
			}
			

		}
		
		n.clearAttributes();
		n.addAttribute("ui.label",id);
		n.addAttribute("open", node_open);
		n.addAttribute("gold", gold);
		n.addAttribute("tresor_open", tresor_open);
		n.addAttribute("lockPicking", lockPicking);
		n.addAttribute("force", force);
		n.addAttribute("wumpus", wumpus);
		n.addAttribute("agent", agent);
		n.addAttribute("date", date);
		setColor(n);
		
		/*if(wumpus) {
			n.addAttribute("ui.class", MapAttribute.wumpus.toString());
		}else if(agent!=null) {
			n.addAttribute("ui.class", MapAttribute.agent.toString());
		}else if (node_open){
			n.addAttribute("ui.class", MapAttribute.open.toString());
		}else if(gold>0) {
			if(tresor_open) {
				n.addAttribute("ui.class", MapAttribute.tresor_open.toString());
			}else {
				n.addAttribute("ui.class", MapAttribute.tresor_closed.toString());
			}
		}*/
		
		
	}
	
	
	
	public void updateNode(String id,Boolean node_open,Integer gold,Boolean tresor_open,Integer lockPicking, Integer force, Boolean wumpus, Couple<String,String> agent, Date date){
		Node n;
		if (this.g.getNode(id)==null){
			n=this.g.addNode(id);
		}
		n=this.g.getNode(id);
		
		
		
		n.clearAttributes();
		n.addAttribute("ui.label",id);
		n.addAttribute("open", node_open);
		n.addAttribute("gold", gold);
		n.addAttribute("tresor_open", tresor_open);
		n.addAttribute("lockPicking", lockPicking);
		n.addAttribute("force", force);
		n.addAttribute("wumpus", wumpus);
		n.addAttribute("agent", agent);
		n.addAttribute("date", date);
		
		setColor(n);
		
		/*if(wumpus) {
			n.addAttribute("ui.class", MapAttribute.wumpus.toString());
		}else if(agent!=null) {
			n.addAttribute("ui.class", MapAttribute.agent.toString());
		}else if (node_open){
			n.addAttribute("ui.class", MapAttribute.open.toString());
		}else if(gold>0) {
			if(tresor_open) {
				n.addAttribute("ui.class", MapAttribute.tresor_open.toString());
			}else {
				n.addAttribute("ui.class", MapAttribute.tresor_closed.toString());
			}
		}*/
		
		
	}
	
	public void setColor(Node n) {
		boolean wumpus = n.getAttribute("wumpus");
		Couple<String, String> agent = n.getAttribute("agent");
		boolean node_open = n.getAttribute("open");
		int gold = n.getAttribute("gold");
		boolean tresor_open = n.getAttribute("tresor_open");
		
		if(wumpus) {
			n.addAttribute("ui.class", MapAttribute.wumpus.toString());
		}else if(agent!=null) {
			n.addAttribute("ui.class", MapAttribute.agent.toString());
		}else if (node_open){
			n.addAttribute("ui.class", MapAttribute.open.toString());
		}else if(gold>0) {
			if(tresor_open) {
				n.addAttribute("ui.class", MapAttribute.tresor_open.toString());
			}else {
				n.addAttribute("ui.class", MapAttribute.tresor_closed.toString());
			}
		}
	}
	
	//calcul le coefficient de clustering d'un noeud
	public float clusteringCoeff(Node n) {
		Iterator<Node>it=n.getNeighborNodeIterator();
		int connexions = 0;
		
		ArrayList<Node> voisins=new ArrayList<Node>();
		while(it.hasNext()) {
			voisins.add(it.next());
		}
		int degree =voisins.size();
		if(degree<=1) {
			return 0;
		}
		for(Node v1 : voisins) {
			for(Node v2 : voisins) {
				if(v1!=v2 && v1.hasEdgeToward(v2)) {
					connexions++;
				}
			}
		}
		
		return(connexions/(degree*(degree-1)));
	}
	
	public float meanDist(Node n) {
		List<String> shortestPath=new ArrayList<String>();
		float meanDist=0;
		int nbNode=this.g.getNodeCount();
		String idFrom=n.getId();
		Dijkstra dijkstra = new Dijkstra();//number of edge
		dijkstra.init(g);
		dijkstra.setSource(g.getNode(idFrom));
		dijkstra.compute();//compute the distance to all nodes from idFrom
		
		for(Node idTo : g.getEachNode()) {
			if(!idTo.equals(idFrom)) {
				meanDist += dijkstra.getPath(idTo).size()-1;
			}
		}
		dijkstra.clear();
		return (meanDist/nbNode);
	}
	
	public String calculateSilloSpot() {
		int maxNodeSpot=(int)(this.g.getNodeCount()*0.2)+1;
		ArrayList<Couple<String, float[]>> listed = new ArrayList<Couple<String, float[]>>();
		
		//classement des noeud par dist moyenne aux autres puis coeff de clustering
		for(Node n: g.getEachNode()) {
			String label = n.getId();
			float meanDist = meanDist(n);
			float clustCoeff = clusteringCoeff(n);
			float[] values = {meanDist,clustCoeff};
			int insertInd = 0;
			Iterator<Couple<String, float[]>> it = listed.iterator();
			boolean over=false;
			while(it.hasNext()&&!over) {
				Couple<String, float[]> tmp = it.next();
				if(tmp.getRight()[0]<=meanDist && tmp.getRight()[1]<=clustCoeff) {
					over = true;
				}
				else {
					insertInd++;
				}
			}
			listed.add(insertInd, new Couple<String, float[]>(label, values));
		}
		
		float maxCoeff=-1;
		String selectedNode=null;
		for(int i=0; i<=maxNodeSpot; i++) {
			float coeff = listed.get(i).getRight()[1];
			if(coeff>maxCoeff) {
				maxCoeff=coeff;
				selectedNode=listed.get(i).getLeft();
			}
		}
		this.silloSpot=selectedNode;
		this.gPrime=Graphs.clone(this.g);
		gPrime.removeNode(silloSpot);
		return selectedNode;		
	}

	


	

   /**
    * Add the edge if not already existing.
    * @param idNode1
    * @param idNode2
    */
	public void addEdge(String idNode1,String idNode2){
		try {
			this.nbEdges++;
			this.g.addEdge(this.nbEdges.toString(), idNode1, idNode2);
		}catch (EdgeRejectedException e){
			//Do not add an already existing one
			this.nbEdges--;
		}
		
	}
	
	

	public HashMap<String, Couple<String, String>> getAgentsInfo() {
		return agentsInfo;
	}
	
	public Couple<List<String>,List<String>> getPosByType(String type){
		System.out.println(agentsInfo);
		ArrayList<String> listId = new ArrayList<String>();
		ArrayList<String> listPos = new ArrayList<String>();
		for(String k:agentsInfo.keySet()) {
			if(agentsInfo.get(k).getLeft().equals(type)) {
				listId.add(k);
				listPos.add(agentsInfo.get(k).getRight());
			}
		}
		return new Couple<List<String>,List<String>>(listId,listPos);
	}

	/**
	 * Compute shortest path from idForm to idTo on gPrime if it exists, on g if not
	 * @param idFrom
	 * @param idTo
	 * @return
	 */
	public List<String> getShortestPath(String idFrom,String idTo){
		List<String> shortestPath=null;
		if(gPrime!=null) {
			shortestPath=shortestPathOnGraph(idFrom, idTo, gPrime);
		}
		if(shortestPath==null || shortestPath.size()==0) {
			shortestPath=shortestPathOnGraph(idFrom, idTo, this.g);
		}
		return shortestPath;
	}
	
	/**
	 * Compute the shortest Path from idFrom to IdTo on given graph. The computation is currently not very efficient
	 * 
	 * @param idFrom id of the origin node
	 * @param idTo id of the destination node
	 * @return the list of nodes to follow
	 */
	public List<String> shortestPathOnGraph(String idFrom,String idTo, Graph g){
		List<String> shortestPath=new ArrayList<String>();

		Dijkstra dijkstra = new Dijkstra();//number of edge
		dijkstra.init(g);
		dijkstra.setSource(g.getNode(idFrom));
		dijkstra.compute();//compute the distance to all nodes from idFrom
		List<Node> path=dijkstra.getPath(g.getNode(idTo)).getNodePath(); //the shortest path from idFrom to idTo
		Iterator<Node> iter=path.iterator();
		while (iter.hasNext()){
			shortestPath.add(iter.next().getId());
		}
		dijkstra.clear();
		shortestPath.remove(0);//remove the current position
		return shortestPath;
	}
	
	public List<String> getShortestPathOpenNodes(String idFrom,List<String> idTo){
		Graph g;
		if(this.gPrime!=null) {
			g=this.gPrime;
		}
		else {
			g=this.g;
		}
		List<String> shortestPath = null;
		boolean over=false;
		while(!over) {
			//second tour, recherche sur g initial
			if(shortestPath!=null) {
				g=this.g;
			}
			
			shortestPath=new ArrayList<String>();
	
			Dijkstra dijkstra = new Dijkstra();//number of edge
			dijkstra.init(g);
			dijkstra.setSource(g.getNode(idFrom));
			dijkstra.compute();//compute the distance to all nodes from idFrom
			int minDist=-1;
			List<Node> minPath=null;
			for(String id : idTo) {
				List<Node> path=dijkstra.getPath(g.getNode(id)).getNodePath(); //the shortest path from idFrom to idTo
				int dist = path.size();
				if((minDist==-1 || dist<minDist)&&dist>1) {
					minDist=dist;
					minPath=path;
				}
	
			}
			if(minPath==null) {
				System.out.println(idFrom +" -> "+idTo + " minpath "+minPath);
				return null;
			}
			
			Iterator<Node> iter=minPath.iterator();
			while (iter.hasNext()){
				shortestPath.add(iter.next().getId());
			}
			dijkstra.clear();
			if(shortestPath.size()>0) {
				try {
					shortestPath.remove(0);//remove the current position bug
					return shortestPath;
					}
					catch(Exception e){
						e.printStackTrace();
						System.out.println(idFrom +" -> "+idTo + " minpath "+minPath);
						return null;
					}
				
			}
			over=true;
		}
		return null;
	}
	
	//not changed
	public String getRandomNodeWhitout(String idFrom, String idWithout){
		Iterator<Node> it = getNode(idFrom).getNeighborNodeIterator();
		ArrayList<Node> candidates = new ArrayList<Node>();
		Node current;
		while(it.hasNext()){
			current = it.next();
			if(!current.getAttribute("ui.label").equals(idWithout)){
				candidates.add(current);
			}
		}
		return(candidates.get(ThreadLocalRandom.current().nextInt(0, candidates.size())).getAttribute("ui.label"));		
	}
	
	public Graph getG() {
		return g;
	}
	
	public Iterable<? extends Node> getEachNode() {
		return g.getEachNode();
	}
	
	public Node getNode(String id) {
		return g.getNode(id);
	}
	
	
	// FONCTIONS DE TRANSMISSIONS
	// ET FUSIONS
	// SUR LES MAPS
	
	public ArrayList<Case> getCases(){
		ArrayList<Case> cases = new ArrayList<Case>();
		
		for(Node n : g.getEachNode()) {
			
			String id = n.getAttribute("ui.label");
			boolean open = n.getAttribute("open");
			int gold = n.getAttribute("gold");
			boolean open_tresor = n.getAttribute("tresor_open");
			int lockPicking = n.getAttribute("lockPicking");
			int force = n.getAttribute("force");
			boolean wumpus = n.getAttribute("wumpus");
			Couple<String, String> agent = n.getAttribute("agent");
			Date date = n.getAttribute("date");
			
			Case c = new Case(id, open, gold, open_tresor, lockPicking, force, wumpus, agent, date);
			cases.add(c);
			
		}
			
		return cases;
	}
	
	public ArrayList<Couple<String,String>> getListEdges(){
		ArrayList<Couple<String,String>> edges= new ArrayList<Couple<String,String>>();
		for(Edge e : g.getEachEdge()) {
			edges.add(new Couple<String,String>(e.getNode0().toString(), e.getNode1().toString()));
		}
		return edges;
	}
	
	public SendableMap getSendableMap(DedaleAgent myDedaleAgent){
		ArrayList<Case> cases= this.getCases();
		ArrayList<Couple<String,String>> edges = this.getListEdges();
		String sender = myDedaleAgent.getLocalName();
		String pos;
		if(myDedaleAgent instanceof TankerAgent) {
			pos = ((TankerAgent) myDedaleAgent).getMySpot();
		}
		else {
			pos = myDedaleAgent.getPosition();
		}
		
		String type = myDedaleAgent.getType();
		return new SendableMap(cases, edges, agentsInfo, sender, type, pos);
	}
	
	//A passer en non static
	public static void MergeWithSendableMap(DedaleAgent myDedaleAgent, Object recMap) {
		
		
		SendableMap recieved = (SendableMap)recMap;
		
		if(myDedaleAgent.getMap()==null) {
			System.out.println(myDedaleAgent.getLocalName()+" ----> MAP NULL");
			try {
				myDedaleAgent.doWait(250);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Gestion de la hashMap
		myDedaleAgent.getMap().getAgentsInfo().putAll(recieved.getAgentsInfo());
				
		//Gestion de l'agent envoyeur
		myDedaleAgent.getMap().getAgentsInfo().put(recieved.getSender(), new Couple<String, String>(recieved.getType(), recieved.getSendingPos()));
		
		
		
		//ajout des noeuds
		for(Case c : recieved.getCases()) {
			
			String cId = c.getId();
			boolean cOpen = c.is_Open();//mis a true car si on cree la case c'est que le noeud est ouvert
			Date cDate= c.getDate();
			int cGold = c.getGold();
			int cForce = c.getForce();
			int cLockPicking = c.getLockPicking();
			boolean cTresor_ouvert= c.is_TresorOuvert();//
			Couple<String, String> cAgent = c.getAgent();
			boolean cWumpus = c.isWumpus();
			
			Node currentNode = myDedaleAgent.getMap().getNode(cId);
			
			//si le noeud n'existe pas
			if(currentNode==null) {
				
				myDedaleAgent.getMap().addNode(myDedaleAgent, cId, cOpen, cGold, cTresor_ouvert, cLockPicking, cForce, cWumpus, cAgent, cDate);
				if(cOpen){
					myDedaleAgent.getOpenNodes().add(cId);
				}else{
					myDedaleAgent.getClosedNodes().add(cId);
				}
				
			//si le noeud existe deja
			}else {
				//identifiant
				
				//attribut ouvert ferme
				boolean mergeOpen;
				boolean localOpen = (boolean)currentNode.getAttribute("open");
				if(localOpen && cOpen) {
					mergeOpen=true;
				}else {
					mergeOpen=false;
				}
				
				Date localDate=(Date)currentNode.getAttribute("date");
				
				//si le noeud recu est plus recent
				if(cDate.after(localDate)) {
					myDedaleAgent.getMap().addNode(myDedaleAgent, cId, cOpen, cGold, cTresor_ouvert, cLockPicking, cForce, cWumpus, cAgent, cDate);
					
				}else{
					if(localOpen != mergeOpen) {
						int localGold = (int)currentNode.getAttribute("gold");
						boolean localTresor_ouvert = (boolean)currentNode.getAttribute("tresor_open");
						int localLockPicking = (int)currentNode.getAttribute("lockPicking");
						int localForce = (int)currentNode.getAttribute("force");
						boolean localWumpus =(boolean)currentNode.getAttribute("wumpus");
						Couple<String, String> localAgent =(Couple<String, String>)currentNode.getAttribute("agent");
						
						myDedaleAgent.getMap().addNode(myDedaleAgent, cId, mergeOpen, localGold, localTresor_ouvert, localLockPicking, localForce, localWumpus, localAgent, localDate);
					}
				}
				
				if(mergeOpen && !myDedaleAgent.getOpenNodes().contains(cId)) {
					myDedaleAgent.getOpenNodes().add(cId);
				}else if(!mergeOpen) {
					if(!myDedaleAgent.getClosedNodes().contains(cId)){
						myDedaleAgent.getClosedNodes().add(cId);
					}
					myDedaleAgent.getOpenNodes().remove(cId);
				}
			}
		}
		
		//ajout des arretes
		for(Couple<String,String> a : recieved.getEdges()){
			String n1=a.getLeft();
			String n2=a.getRight();
			myDedaleAgent.getMap().addEdge(n1, n2);
		}
		
		/*System.out.println(myDedaleAgent.getLocalName()+" merged maps : maj ou add "+ nbnode + " noeud et "+ nbarrete+" arretes et maj node "+ nodemaj);
		System.out.println(myDedaleAgent.getLocalName()+" open "+myDedaleAgent.getOpenNodes().toString());
		System.out.println(myDedaleAgent.getLocalName()+" closed "+myDedaleAgent.getClosedNodes().toString());*/


	}
	
	public static void updateMapWithObs(DedaleAgent myDedaleAgent, String myPosition ,List<Couple<String,List<Couple<Observation,Integer>>>> lobs) {
		Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
		
		//1) remove the current node from openlist and add it to closedNodes.
		myDedaleAgent.getClosedNodes().add(myPosition);
		myDedaleAgent.getOpenNodes().remove(myPosition);
		//incomplet
		Couple<String, List<Couple<Observation, Integer>>> tmp = iter.next();
		List<Couple<Observation, Integer>> obs =tmp.getRight();
		//System.out.println("Courant "+myPosition+" iterator "+tmp.getLeft());
		
		myDedaleAgent.getMap().getAgentsInfo().put(myDedaleAgent.getLocalName(), new Couple<String, String>(myDedaleAgent.getType(), myPosition));
		
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
		myDedaleAgent.getMap().addNode(myDedaleAgent, myPosition, false, obsGold, obsTresorOpen, obsLockPicking, obsForce, obsWumpus, null, new Date());
		
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
					myDedaleAgent.getMap().addNode(myDedaleAgent, nodeId, true, obsGold, obsTresorOpen, obsLockPicking, obsForce, obsWumpus, null, new Date());
					
					
					myDedaleAgent.getMap().addEdge(myPosition, nodeId);	
				}else{
					//the node exist, but not necessarily the edge
					myDedaleAgent.getMap().addEdge(myPosition, nodeId);
				}
				
			}
		}
	}
	

	
	
	

	
	
}
