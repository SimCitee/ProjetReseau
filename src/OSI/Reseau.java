package OSI;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;

/*
 * Singleton pour la couche réseau
 * 
 */
public class Reseau  extends Thread{
	
	//Pipes
	PipedOutputStream reseauOut;
	PipedInputStream reseauIn;
	
	public Reseau(PipedOutputStream reseauOut, PipedInputStream reseauIn)
	{
		this.reseauOut = reseauOut;
		this.reseauIn = reseauIn;
	}
	
	//Début du thread
	public void run()
	{
		//Débute la lecture de la couche transport
		lireDeTransport();
		
		//Tests d'écriture vers la couche transport
		//TODO à enlever
		ecrireVersTransport("Commande #1");
		ecrireVersTransport("Commande #2");
		ecrireVersTransport("Commande #3");
		
		//TODO L'aisser ici, il faut une facon de dire à la couche transport quelle arrête sa lecture
		ecrireVersTransport("stop");
	}
	
	//Lecture de la couche reseau
	private void lireDeTransport()
	{
		String command = "";
		try {
			
			while(true){
				char c = (char)reseauIn.read();
				if(c == '|')
				{
					if(!command.equals("stop"))
					{
						doUserCommand(command);
						command = "";
					}
					else{
						//Arrêt de la couche transport
						break;
					}
				}
				else
				{
					//Ajoute le charactère lu à la chaine
					command += c;
				}

			}
          
		} catch(Exception e) {
        	
			throw new RuntimeException(e);
        }
	}
	
	//ecriture sur la couche transport
	private void ecrireVersTransport(String chaine)
	{

		chaine += '|';	//Ajoute le délimiteur à la chaîne
		try {
			
			for(int i=0; i < chaine.length(); i++)
			{
				reseauOut.write(chaine.charAt(i));
			}
          
			reseauOut.flush();
			
		} catch(Exception e) {
        	
			throw new RuntimeException(e);
        }
		
	}
	
	//Les commande complètes provenant de la couche transport sont ici!!!
	private void doUserCommand(String command)
	{
		System.out.println("Commande provenant de la couche transport : " + command);
	}
}
