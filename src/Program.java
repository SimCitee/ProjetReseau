import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import OSI.Reseau;
import OSI.Transport;


public class Program {

	public static void main(String[] args) {

		try
		{ 
			
			//Crée les pipes
			PipedOutputStream transportOut = new PipedOutputStream();
			PipedInputStream reseauIn = new PipedInputStream(transportOut);
			PipedOutputStream reseauOut = new PipedOutputStream();
			PipedInputStream transportIn = new PipedInputStream(reseauOut);
	
			//Crée les couches
			Transport coucheTransport = new Transport(transportOut, transportIn);
			Reseau coucheReseau = new Reseau(reseauOut, reseauIn);
	
			
			
			///Overture de la couche réseau
			coucheReseau.start();
			
			//La couche transport lit le réseau
			coucheTransport.start();
		
			//Lecture du fichier d'input
			coucheTransport.readInputFile();
			
			
			
			
			
			
			
			
			
			//Fermeture des pipes
			try {
				PipedOutputStream a = new PipedOutputStream();
				a.close();
				//transportOut.close();
				
				//reseauOut.close();
				
				
				
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
		
	}

}
