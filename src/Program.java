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
	
			
			
			//Démarre le thread de la couche réseau
			coucheReseau.start();
			
			
			
			//Lecture du fichier de tests
			coucheTransport.readInputFile();

			coucheTransport.lireDeReseau();
			
			
			
			//Fermeture des pipes
			try {
				
				
				transportOut.close();
				
				reseauOut.close();
				
				
				
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
		System.out.println("Fin du programme!");
	}

}
