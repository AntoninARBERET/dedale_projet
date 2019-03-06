package eu.su.mas.dedaleEtu.mas.knowledge;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import dataStructures.tuple.Couple;
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
	
	public enum MapRessources{
		gold, none
	}
	
	public enum MapAgent {
		explo, collect, sillo, wumpus, none
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
	
	/**
	 * Associate to a node an attribute in order to identify them by type. 
	 * @param id
	 * @param mapAttribute
	 */
	public void addNode(String id,MapAttribute mapAttribute){
		Node n;
		if (this.g.getNode(id)==null){
			n=this.g.addNode(id);
		}else{
			n=this.g.getNode(id);
		}
		
		n.clearAttributes();
		n.addAttribute("ui.class", mapAttribute.toString());
		n.addAttribute("ui.label",id);
	}
	
	/**
	 * Associate to a node an attribute in order to identify them by type. 
	 * @param id
	 * @param mapAttribute
	 */
	public void addNode(String id,MapAttribute mapAttribute, String date, MapRessources mapRess, MapAgent mapAgent){
		Node n;
		if (this.g.getNode(id)==null){
			n=this.g.addNode(id);
		}else{
			n=this.g.getNode(id);
		}
		

		
		n.clearAttributes();
		n.addAttribute("ui.class", mapAttribute.toString());
		n.addAttribute("ui.label",id);
		n.addAttribute("ui.date", date);
		n.addAttribute("ui.ress", mapRess.toString());
		n.addAttribute("ui.ress", mapAgent.toString());
	}
	
	public void addNode(String id,MapAttribute mapAttribute, MapRessources mapRess, MapAgent mapAgent){
		Node n;
		if (this.g.getNode(id)==null){
			n=this.g.addNode(id);
		}else{
			n=this.g.getNode(id);
		}
		
		Date date = new Date();
		
		n.clearAttributes();
		n.addAttribute("ui.class", mapAttribute.toString());
		n.addAttribute("ui.label",id);
		n.addAttribute("ui.date", ""+date.getTime());
		n.addAttribute("ui.ress", mapRess.toString());
		n.addAttribute("ui.agent", mapAgent.toString());
	}

	/**
	 * Add the node id if not already existing
	 * @param id
	 */
	public void addNode(String id){
		Node n=this.g.getNode(id);
		if(n==null){
			n=this.g.addNode(id);
		}else{
			n.clearAttributes();
		}
		n.addAttribute("ui.label",id);
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
	
	public Graph getG() {
		return g;
	}
	
	public Iterable<? extends Node> getEachNode() {
		return g.getEachNode();
	}
	
	public Node getNode(String id) {
		return g.getNode(id);
	}
	
	//Peut etre plus efficace
	public Couple<Couple<ArrayList<String>,ArrayList<String>>,ArrayList<Couple<String,String>>> getStringListRepresentation(){ 
		ArrayList<String> openNodes = new ArrayList<String>();
		ArrayList<String> closedNodes = new ArrayList<String>();

		for(Node n : g.getEachNode()) {
			if(n.getAttribute("ui.class")==MapAttribute.open.toString()) {
				openNodes.add(n.toString());
			}else {
				closedNodes.add(n.toString());
			}
		}
		Couple<ArrayList<String>,ArrayList<String>> nodes = new Couple<ArrayList<String>,ArrayList<String>>(openNodes, closedNodes);
		
		
		ArrayList<Couple<String,String>> edges= new ArrayList<Couple<String,String>>();
		for(Edge e : g.getEachEdge()) {
			edges.add(new Couple<String,String>(e.getNode0().toString(), e.getNode1().toString()));
		}
		
		return new Couple<Couple<ArrayList<String>,ArrayList<String>>,ArrayList<Couple<String,String>>>(nodes, edges);
	}
	
	public static void MergeMaps(DedaleAgent myDedaleAgent, Object recMap) {
		Couple<Couple<ArrayList<String>,ArrayList<String>>,ArrayList<Couple<String,String>>>  newMap = (Couple<Couple<ArrayList<String>,ArrayList<String>>,ArrayList<Couple<String,String>>>) recMap;
		if(myDedaleAgent.getMap()==null) {
			System.out.println(myDedaleAgent.getLocalName()+" ----> MAP NULL");
			try {
				myDedaleAgent.doWait(250);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int o=0, f=0, a=0;
		//Ajout des noeuds ouverts
		for(String noeud : newMap.getLeft().getLeft()) {
			if(myDedaleAgent.getMap().getNode(noeud)==null) {
				myDedaleAgent.getMap().addNode(noeud, MapAttribute.open);
				myDedaleAgent.getOpenNodes().add(noeud);
				o++;
			}
		}
		
		//Ajout des noeuds fermes
		for(String noeud : newMap.getLeft().getRight()) {
			myDedaleAgent.getMap().addNode(noeud);
			myDedaleAgent.getClosedNodes().add(noeud);
			myDedaleAgent.getOpenNodes().remove(noeud);
			f++;
		}
		
		//Ajout des arcs
		for(Couple<String,String> arc : newMap.getRight()) {
			myDedaleAgent.getMap().addEdge(arc.getLeft(), arc.getRight());
			a++;
		}
		
		System.out.println(myDedaleAgent.getLocalName()+" merged maps - o = " +o+" f = "+f+" a = "+a );

	}
	
	public Couple<ArrayList<ArrayList<String>>,ArrayList<Couple<String,String>>> getFullRepresentation(){
		ArrayList<ArrayList<String>> nodes = new ArrayList<ArrayList<String>>();
		
		for(Node n : g.getEachNode()) {
			String id, open, date, ress, agnt;
			
			id=n.toString();
			open =n.getAttribute("ui.class");

			date=n.getAttribute("ui.date");
			ress =n.getAttribute("ui.ress");
			agnt =n.getAttribute("ui.agent");
			//ajouter ressources et agent
			ArrayList<String> desc = new ArrayList<String>();
			desc.add(id);
			desc.add(open);
			desc.add(date);
			desc.add(ress);
			desc.add(agnt);
			nodes.add(desc);
			
			
		}
			
		
		ArrayList<Couple<String,String>> edges= new ArrayList<Couple<String,String>>();
		for(Edge e : g.getEachEdge()) {
			edges.add(new Couple<String,String>(e.getNode0().toString(), e.getNode1().toString()));
		}
		
		return new Couple<ArrayList<ArrayList<String>>,ArrayList<Couple<String,String>>>(nodes, edges);
	}
	
	public static void MergeFullMaps(DedaleAgent myDedaleAgent, Object recMap) {
		
		Couple<ArrayList<ArrayList<String>>,ArrayList<Couple<String,String>>>  newMap = (Couple<ArrayList<ArrayList<String>>,ArrayList<Couple<String,String>>>) recMap;
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
		for(ArrayList<String> noeud : newMap.getLeft()) {
			System.out.println("MERGE ----> nouv noeud");
			Node currentNode = myDedaleAgent.getMap().getNode(noeud.get(0));
			
			//si le noeud n'existe pas
			if(currentNode==null) {
				myDedaleAgent.getMap().addNode(noeud.get(0), MapAttribute.valueOf(noeud.get(1)), MapRessources.valueOf(noeud.get(3)), MapAgent.valueOf(noeud.get(4)));
				//myDedaleAgent.getOpenNodes().add(noeud);
			//si le noeud existe deja
			}else {
				//identifiant
				String id = noeud.get(0);
				
				//attribut ouvert ferme
				MapAttribute attr;
				id = noeud.get(0);
				if(currentNode.getAttribute("ui.class").equals("open") && noeud.get(1).equals("open")) {
					attr=MapAttribute.open;
				}else {
					attr=MapAttribute.closed;
				}
				
				//attribut ressource
				MapRessources ress = MapRessources.valueOf(noeud.get(3));
				
				long dcurrent = Long.parseLong(currentNode.getAttribute("ui.date"));
				long dnew = Long.parseLong(noeud.get(2));
				String date;
				MapAgent agent;
				System.out.println("MERGE ----> test date");
				//si le noeud recu est plus recent
				if(dnew>dcurrent) {
					date = noeud.get(2);
					agent=MapAgent.valueOf(noeud.get(4));
					myDedaleAgent.getMap().addNode(id, attr, date, ress, agent);
				}else {
					date = currentNode.getAttribute("ui.date");
					
					agent=MapAgent.valueOf(currentNode.getAttribute("ui.agent"));

					
					myDedaleAgent.getMap().addNode(id, attr, date, ress, agent);
				}
				
			}
		}
		

		
		//Ajout des arcs
		for(Couple<String,String> arc : newMap.getRight()) {
			myDedaleAgent.getMap().addEdge(arc.getLeft(), arc.getRight());
		}
		
		System.out.println(myDedaleAgent.getLocalName()+" merged maps");

	}
	
}
