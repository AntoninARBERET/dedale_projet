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

import org.graphstream.graph.Node;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.yours.DedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.tools.Pair;

public class Random {
	public static void main(String args[]) {
		Observation o = Observation.GOLD;
		System.out.println("Test de ouf " + (o instanceof Serializable));
		Date d1 = new Date();
		System.out.println(d1.getTime());
		MapRepresentation map = new MapRepresentation();
		MapRepresentation map2 = new MapRepresentation();
		map.addNode("21", MapRepresentation.MapAttribute.open,"21", MapRepresentation.MapRessources.gold, MapRepresentation.MapAgent.none );
		map.addNode("20", MapRepresentation.MapAttribute.open, MapRepresentation.MapRessources.gold, MapRepresentation.MapAgent.none );

		map.addEdge("21", "21");
		map.addEdge("20", "21");
		
		for(int i=0; i<1000000000; i++) {
			int j = 10;
			j=j*i;
		}
		
		map2.addNode("21", MapRepresentation.MapAttribute.open,"9", MapRepresentation.MapRessources.gold, MapRepresentation.MapAgent.none );
		map2.addNode("22", MapRepresentation.MapAttribute.open, MapRepresentation.MapRessources.gold, MapRepresentation.MapAgent.none );

		map2.addEdge("21", "21");
		map2.addEdge("21", "22");
		try {
			
			/*DedaleAgent ag = new DedaleAgent();
			ag.setMap(map);
			System.out.println("Try merge");
			System.out.println(map.getFullRepresentation().toString());
			System.out.println(map2.getFullRepresentation().toString());
			
			MapRepresentation.MergeFullMaps(ag, map2.getFullRepresentation());

			System.out.println(ag.getMap().getFullRepresentation().toString());
			/*FileOutputStream fileOutputStream = new FileOutputStream("test.txt");
		    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		    objectOutputStream.writeObject(map);
		    
		    objectOutputStream.flush();
		    objectOutputStream.close();
		     
		    FileInputStream fileInputStream= new FileInputStream("yourfile.txt");
		    ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
		    MapRepresentation map2 = (MapRepresentation) objectInputStream.readObject();
		    objectInputStream.close(); 
		    map2.display();*/
			
			/*boolean b = map.getG().getEachNode() instanceof Set ;
			System.out.println(b);
			System.out.println(map.getFullRepresentation().toString());
			System.out.println("done");*/
			//System.out.println(map.getG().getEdge(0).get.toString());
			System.out.println(MapRepresentation.MapAgent.valueOf("none"));
			
		    
		}catch(Exception e) {
			
		}
	}
}
