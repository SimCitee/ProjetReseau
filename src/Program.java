import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import util.RedacteurFichier;
import OSI.Constante;
import OSI.Reseau;
import OSI.Transport;


public class Program {

	public static void main(String[] args) {

		try
		{ 
			// Suppression des fichiers anterieurs
			RedacteurFichier.supprimerFichier(Constante.L_ECR_NAME);
			RedacteurFichier.supprimerFichier(Constante.L_LEC_NAME);
			RedacteurFichier.supprimerFichier(Constante.S_ECR_NAME);
			
			//Cree les pipes
			PipedOutputStream transportOut = new PipedOutputStream();
			PipedInputStream reseauIn = new PipedInputStream(transportOut);
			PipedOutputStream reseauOut = new PipedOutputStream();
			PipedInputStream transportIn = new PipedInputStream(reseauOut);
	
			//Cree les couches
			Transport coucheTransport = new Transport(transportOut, transportIn);
			Reseau coucheReseau = new Reseau(reseauOut, reseauIn);
			
			///Ouverture de la couche reseau
			coucheReseau.start();
			
			//La couche transport lit le reseau
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
