package OSI;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;

import Paquet.Paquet;
import Paquet.PaquetAppel;
import Paquet.PaquetIndicationLiberation;

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
		
		
	}
	
	//Lecture de la couche transport
	private void lireDeTransport()
	{
		
		String command = "";
		try {
			char c;
			do{
				
				c = (char)reseauIn.read();
				
				if(c == '|')
				{
					if(!command.equals("stop"))
					{
						executerCommandeTransport(command);
						command = "";
					}
					else{
						
						//Envoi le signal d'arret de lecture à la couche transport
						ecrireVersTransport("stop");
						
						//Fermeture du tube de lecture
						reseauIn.close();
						
						//Arrêt de lecture de la couche réseau
						break;
					}
				}
				else
				{
					//Ajoute le charactère lu à la chaine
					command += c;
				}
			
			//Demande d'arrêt par le producteur
			//}while((int)c != 65535);
			}while(true);
          
		} catch(Exception e) {
			e.printStackTrace();
			//throw new RuntimeException(e);
			System.out.println("Arret de lecture de la couche réseau");
        }
	}
	
	//ecriture sur la couche transport
	public void ecrireVersTransport(String chaine)
	{
		chaine += '|';	//Ajoute le délimiteur à la chaîne
		try {
			
			for(int i=0; i < chaine.length(); i++)
			{
				//System.out.println("Ecriture de : " + chaine.charAt(i));
				reseauOut.write(chaine.charAt(i));
				//transportOut.write('c');
			}
          
			reseauOut.flush();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
        }
	}
	
	//Les commande complètes provenant de la couche transport sont ici!!!
	private void executerCommandeTransport(String command)
	{
		String[] commandArray = null;
		Paquet paquet = null;
		Paquet reponse = null;
		commandArray = command.split(" ");
		boolean versLiaison = true;
		
		// TODO
		//SI NIEC est dans TABLE CORRESPONDANCE
		// POGNER le # connexion correspondant
		// SINON
		// AJOUTER LE NIEC DANS LA TABLE et lui associer un numéro de connexion
		
		// 
		System.out.println("Réseau recois une commande de transport : " + command);
		
		switch (commandArray[1]) {
			case "N_CONNECT.req" : 
				int temp = Integer.parseInt(commandArray[2]);
				if (temp % 27 == 0) {
					paquet = new PaquetAppel(1, Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]));
				}
				else {
					versLiaison = false;
					ecrireVersTransport(commandArray[0] + " N_DISCONNECT.ind " + commandArray[3] + " Refus de connexion");
				} break;
			case "N_DATA.req" : break;
			case "N_DISCONNECT.req" : paquet = new PaquetIndicationLiberation(1, Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]), "Demande de transport"); break;
		}
		
		// Si retour == null -> temporisateur
		if (versLiaison == true)
			reponse = Liaison.getInstance().lireDeReseau(paquet);
		
		
		
		//TODO Joe et Phil, vous commencer ICI!!!! Point d'entré des données de la couche transport. 
		//ecrireVersTransport(commandArray[0] + " N_DISCONNECT.ind " + commandArray[3] + " Refus de connexion");
	}
	
	
}
