package test.java;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.tools.Pair;

public class RandomTest {
	public static void main(String args[]) {
		
		MapRepresentation m = new MapRepresentation();
		Graph g =m.getG();
		g.addNode("A");
		g.addNode("B");
		g.addNode("C");
		g.addNode("D");
		g.addNode("E");
		g.addNode("F");
		g.addEdge("1", "A", "B");
		g.addEdge("2", "A", "C");
		g.addEdge("3", "A", "D");
		g.addEdge("4", "A", "E");
		g.addEdge("5", "E", "B");
		g.addEdge("6", "D", "B");
		g.addEdge("7", "C", "B");
		g.display();
		System.out.println(m.calculateSilloSpot());
	}
}
