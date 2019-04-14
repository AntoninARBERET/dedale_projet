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

import org.graphstream.graph.Node;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.tools.Pair;

public class RandomTest {
	public static void main(String args[]) {
		Date d1 = new Date();
		for(int i=0; i<2000000;i++) {
			System.out.print('A');
		}
		Date d2 = new Date();

		
		
		
		System.out.println(d1+" "+d2+" "+(d2.getTime()-d1.getTime()));
		System.out.println(d1+" "+d2+" "+d1.compareTo(d2));

		
	}
}
