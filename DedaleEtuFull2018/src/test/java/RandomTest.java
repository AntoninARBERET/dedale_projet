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
import eu.su.mas.dedale.mas.agent.behaviours.RandomWalkBehaviour;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.tools.Pair;

public class RandomTest {
	public static void main(String args[]) {
		
		ArrayList<String> a = new ArrayList<String>();
		a.add("rrrrrr");
		ArrayList<String> b = a;
		b.add("eeee");
		System.out.println(a);
		a.add(0,"ddd");
		System.out.println(a);

		b.remove(0);
		System.out.println(a.get(0));

	}
}
