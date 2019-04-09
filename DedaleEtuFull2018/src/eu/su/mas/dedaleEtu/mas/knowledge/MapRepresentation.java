package eu.su.mas.dedaleEtu.mas.knowledge;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
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
		closed,open,agent
	}
	


	private static final long serialVersionUID = -1333959882640838272L;

	private Graph g; //data structure
	private Viewer viewer; //ref to the display
	private Integer nbEdges;//used to generate the edges ids
	
	/*********************************
	 * Parameters for graph rendering
	 ********************************/
	
	private String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
	private String nodeStyle_open = "node.agent {"+"fill-color: forestgreen;"+"}";
	private String nodeStyle_agent = "node.open {"+"fill-color: blue;"+"}";
	private String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_open;
	//private Hashtable<String, String[]> nodes_informations;

	
	public MapRepresentation() {
		System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		this.g= new SingleGraph("My world vision");
		this.g.setAttribute("ui.stylesheet",nodeStyle);
		this.viewer = this.g.display();
		this.nbEdges=0;
		//this.nodes_informations=new Hashtable<String, String[]>();
	}

	public void display() {
		this.viewer = this.g.display();
	}
	


	
	public void addNode(String id,boolean open, int gold, boolean tresor_open, int force, boolean wumpus, Date date){
		Node n;
		if (this.g.getNode(id)==null){
			n=this.g.addNode(id);
		}else{
			n=this.g.getNode(id);
		}
		
		
		n.clearAttributes();
		n.addAttribute("ui.label",id);
		n.addAttribute("open", open);
		n.addAttribute("gold", gold);
		n.addAttribute("tresor_open", tresor_open);
		n.addAttribute("force", force);
		n.addAttribute("wumpus", wumpus);
		n.addAttribute("date", date);
		
	}
	
	
	
	public void addNode(String id,boolean open, int gold, boolean tresor_open, int force, boolean wumpus,  List<Couple<Observation,Integer>> mapRess){
		Date date = new Date();
		addNode(id, open, gold, tresor_open, force, wumpus, date);
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

	/**
	 * Compute the shortest Path from idFrom to IdTo. The computation is currently not very efficient
	 * 
	 * @param idFrom id of the origin node
	 * @param idTo id of the destination node
	 * @return the list of nodes to follow
	 */
	public List<String> getShortestPath(String idFrom,String idTo){
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
		List<String> shortestPath=new ArrayList<String>();

		Dijkstra dijkstra = new Dijkstra();//number of edge
		dijkstra.init(g);
		dijkstra.setSource(g.getNode(idFrom));
		dijkstra.compute();//compute the distance to all nodes from idFrom
		int minDist=-1;
		List<Node> minPath=null;
		for(String id : idTo) {
			List<Node> path=dijkstra.getPath(g.getNode(id)).getNodePath(); //the shortest path from idFrom to idTo
			int dist = path.size();
			if(minDist==-1 || dist<minDist) {
				minDist=dist;
				minPath=path;
			}

		}
		Iterator<Node> iter=minPath.iterator();
		while (iter.hasNext()){
			shortestPath.add(iter.next().getId());
		}
		dijkstra.clear();
		shortestPath.remove(0);//remove the current position
		return shortestPath;
	}
	
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
			int force = n.getAttribute("force");
			boolean wumpus = n.getAttribute("wumpus");
			Date date = n.getAttribute("date");
			
			Case c = new Case(id, open, gold, open_tresor, force, wumpus, date);
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
	
	public SendableMap getSendableMap(){
		ArrayList<Case> cases= this.getCases();
		ArrayList<Couple<String,String>> edges = this.getListEdges();
		return new SendableMap(cases, edges);
	}
	
	public static void MergeWithSendableMap(DedaleAgent myDedaleAgent, Object recMap) {
		int nbnode =0, nbarrete=0 ,nodemaj=0;
		
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
		
		//ajout des noeuds
		for(Case c : recieved.getCases()) {
			
			String cId = c.getId();
			boolean cOpen = c.is_Open();//mis a true car si on cree la case c'est que le noeud est ouvert
			Date cDate= c.getDate();
			int cGold = c.getGold();
			int cForce = c.getForce();
			boolean cTresor_ouvert= c.is_TresorOuvert();
			boolean cWumpus = c.isWumpus();
			
			Node currentNode = myDedaleAgent.getMap().getNode(cId);
			
			//si le noeud n'existe pas
			if(currentNode==null) {
				
				myDedaleAgent.getMap().addNode(cId, cOpen, cGold, cTresor_ouvert, cForce, cWumpus, cDate);
				if(cOpen){
					myDedaleAgent.getOpenNodes().add(cId);
				}else{
					myDedaleAgent.getClosedNodes().add(cId);
				}
				nbnode++;
				
			//si le noeud existe deja
			}else {
				//identifiant
				
				//attribut ouvert ferme
				boolean mergeOpen;
				
				if((boolean)currentNode.getAttribute("open") && cOpen) {
					mergeOpen=true;
				}else {
					mergeOpen=false;
				}
				
				Date localDate=(Date)currentNode.getAttribute("date");
				
				//si le noeud recu est plus recent
				if(cDate.after(localDate)) {
					myDedaleAgent.getMap().addNode(cId, mergeOpen, cGold, cTresor_ouvert, cForce, cWumpus, cDate);
					nodemaj++;
				}else{
					currentNode.setAttribute("open", mergeOpen);
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
			nbarrete++;
		}
		
		System.out.println(myDedaleAgent.getLocalName()+" merged maps : maj ou add "+ nbnode + " noeud et "+ nbarrete+" arretes et maj node "+ nodemaj);
		System.out.println(myDedaleAgent.getLocalName()+" open "+myDedaleAgent.getOpenNodes().toString());
		System.out.println(myDedaleAgent.getLocalName()+" closed "+myDedaleAgent.getClosedNodes().toString());


	}
	

	
	
	
	/*public Couple<ArrayList<ArrayList<Object>>,ArrayList<Couple<String,String>>> getFullRepresentation(){
		ArrayList<ArrayList<Object>> nodes = new ArrayList<ArrayList<Object>>();
		
		for(Node n : g.getEachNode()) {
			String id, open, date, agnt;
			List<Couple<Observation, Integer>> obs;
			
			id=n.toString();
			open =n.getAttribute("ui.class");

			date=n.getAttribute("ui.date");
			obs =n.getAttribute("ui.obs");
			agnt =n.getAttribute("ui.agent");
			//ajouter ressources et agent
			ArrayList<Object> desc = new ArrayList<Object>();
			desc.add(id);
			desc.add(open);
			desc.add(date);
			desc.add(obs);
			desc.add(agnt);
			nodes.add(desc);
			
			
		}
			
		
		ArrayList<Couple<String,String>> edges= new ArrayList<Couple<String,String>>();
		for(Edge e : g.getEachEdge()) {
			edges.add(new Couple<String,String>(e.getNode0().toString(), e.getNode1().toString()));
		}
		
		return new Couple<ArrayList<ArrayList<Object>>,ArrayList<Couple<String,String>>>(nodes, edges);
	}
	
	public static void MergeFullMaps(DedaleAgent myDedaleAgent, Object recMap) {
		
		Couple<ArrayList<ArrayList<Object>>,ArrayList<Couple<String,String>>>  newMap = (Couple<ArrayList<ArrayList<Object>>,ArrayList<Couple<String,String>>>) recMap;
		if(myDedaleAgent.getMap()==null) {
			System.out.println(myDedaleAgent.getLocalName()+" ----> MAP NULL");
			try {
				myDedaleAgent.doWait(250);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//ajout des noeuds
		for(ArrayList<Object> noeud : newMap.getLeft()) {
			Node currentNode = myDedaleAgent.getMap().getNode((String)noeud.get(0));
			MapAttribute attr;
			//si le noeud n'existe pas
			if(currentNode==null) {
				attr = MapAttribute.valueOf((String)noeud.get(1));
				myDedaleAgent.getMap().addNode((String)noeud.get(0), attr, (List<Couple<Observation, Integer>>)noeud.get(3), MapAgent.valueOf((String)noeud.get(4)));
				//myDedaleAgent.getOpenNodes().add(noeud);
			//si le noeud existe deja
			}else {
				//identifiant
				String id;
				
				//attribut ouvert ferme
				
				id = (String)noeud.get(0);
				if(currentNode.getAttribute("ui.class").equals("open") && noeud.get(1).equals("open")) {
					attr=MapAttribute.open;
				}else {
					attr=MapAttribute.closed;
				}
				
				
				
				long dcurrent = Long.parseLong(currentNode.getAttribute("ui.date"));
				long dnew = Long.parseLong((String)noeud.get(2));
				String date;
				MapAgent agent;
				//si le noeud recu est plus recent
				if(dnew>dcurrent) {
					List<Couple<Observation, Integer>> obs = (List<Couple<Observation, Integer>>)noeud.get(3);
					date = (String)noeud.get(2);
					agent=MapAgent.valueOf((String)noeud.get(4));
					myDedaleAgent.getMap().addNode(id, attr, date, obs, agent);
				}else if(dnew<dcurrent) {
					date = currentNode.getAttribute("ui.date");
					
					agent=MapAgent.valueOf(currentNode.getAttribute("ui.agent"));
					List<Couple<Observation, Integer>> obs = currentNode.getAttribute("ui.obs");
					myDedaleAgent.getMap().addNode(id, attr, date, obs, agent);
				}
				if(attr==MapAttribute.open) {
					myDedaleAgent.getOpenNodes().add((String)noeud.get(0));
				}else {
					myDedaleAgent.getClosedNodes().add((String)noeud.get(0));
					myDedaleAgent.getOpenNodes().remove((String)noeud.get(0));
				}
				
			}
		}
		

		
		//Ajout des arcs
		for(Couple<String,String> arc : newMap.getRight()) {
			myDedaleAgent.getMap().addEdge(arc.getLeft(), arc.getRight());
		}
		
		System.out.println(myDedaleAgent.getLocalName()+" merged maps");

	}*/
	
	
	
}
