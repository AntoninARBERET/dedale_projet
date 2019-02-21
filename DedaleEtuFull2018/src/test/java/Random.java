package test.java;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

public class Random {
	public static void main(String args[]) {
		MapRepresentation map = new MapRepresentation();
		map.addNode("21");
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("test.txt");
		    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		    objectOutputStream.writeObject(map);
		    
		    objectOutputStream.flush();
		    objectOutputStream.close();
		     
		    FileInputStream fileInputStream= new FileInputStream("yourfile.txt");
		    ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
		    MapRepresentation map2 = (MapRepresentation) objectInputStream.readObject();
		    objectInputStream.close(); 
		    map2.display();
		    
		}catch(Exception e) {
			
		}
	}
}
