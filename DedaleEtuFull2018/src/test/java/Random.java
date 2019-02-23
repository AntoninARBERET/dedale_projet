package test.java;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.graphstream.graph.Node;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.tools.Pair;

public class Random {
	public static void main(String args[]) {
		MapRepresentation map = new MapRepresentation();
		map.addNode("21");
		map.addEdge("21", "21");
		try {
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
			
			boolean b = map.getG().getEachNode() instanceof Set ;
			System.out.println(b);
			System.out.println(map.getStringListRepresentation().toString());
			//System.out.println(map.getG().getEdge(0).get.toString());
		    
		    
		}catch(Exception e) {
			
		}
	}
}
