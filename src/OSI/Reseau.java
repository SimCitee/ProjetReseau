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
						//Arrêt de la couche transport
						break;
					}
				}
				else
				{
					//Ajoute le charactère lu à la chaine
					command += c;
				}
			
			//Demande d'arrêt par le producteur
			}while((int)c != 65535);
          
		} catch(Exception e) {
        	
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
        	
			throw new RuntimeException(e);
        }
	}
	
	//Les commande complètes provenant de la couche transport sont ici!!!
	private void executerCommandeTransport(String command)
	{
		System.out.println("Réseau recois une commande de transport : " + command);
		
		//TODO Joe et Phil, vous commencer ICI!!!! Point d'entré des données de la couche transport. 
		ecrireVersTransport("Données de réseau vers transport");	//Réécriture du "résultat" vers la couche Transport
	}
	
	
}
