package OSI;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;

import util.LecteurFichier;

/*
 * Couche transport
 * 
 */
public class Transport extends Thread{
	
	PipedOutputStream transportOut;
	PipedInputStream transportIn;
	
	public static final String S_LEC_NAME = System.getProperty("user.dir") + "\\src\\fichiers\\S_lec.txt";
	
	//Table des connexions
	private TransportTableConnexion tableConnexion = new TransportTableConnexion();
	
	
	
	public Transport(PipedOutputStream transportOut, PipedInputStream transportIn)
	{
		this.transportOut = transportOut;
		this.transportIn = transportIn;
		
		
	}
	
	
	
	//Lit le fichier S_lec et traite chacunes des lignes
	public void readInputFile()
	{
		LecteurFichier lecteurFichier = new LecteurFichier(S_LEC_NAME);
		String ligne;
		
		//Ouvre le fichier de lecture
		if(lecteurFichier.open())
		{
			
			while((ligne = lecteurFichier.readLine()) != null)

			{
				//Sépare la chaine avec l'espace (donne le pid[0] et la commande[1])
				String s[] = ligne.split("\\s");
				
				//Exécute la commande contenue dans le fichier
				executerCommandeUtilisateur(Integer.parseInt(s[0]), s[1]);
			}
		}
		
		//Ferme le fichier
		lecteurFichier.close();
		
	}
	
	//Débute la lecture de la couche réseau (thread)
	@Override
	public void run()
	{

		lireDeReseau();
	}
	
	/*
	 * Exécute les commandes contenues dans le fichier S_lec
	 * 
	 * 3 choix possibles :
	 * 	   1. Ouverture de connexion
	 * 	   2. Envoie de données
	 *     3. Fermeture de connexion
	 *     
	 */
	private void executerCommandeUtilisateur(int applicationPid, String command)
	{
		
		//Ouverture de connexion
		if(command.equals("open"))
		{
			ouvrirConnexion(applicationPid);
			
			
		}
		//Fermeture de connexion
		else if (command.equals("close"))
		{
			
		}
		
		//Envoie de données
		else
		{
			
		}
	}
	
	//Ouverture d'une connexion
	private void ouvrirConnexion(int applicationPid)
	{
		//Marque la connexion comme ouverte dans la table
		tableConnexion.openConnection(applicationPid);
		
		int sourceAddress = tableConnexion.getSourceAddress(applicationPid);
		int destinationAddress = tableConnexion.getDestinationAddress(applicationPid);
		
		//Passe la requête à la couche réseau
		ecrireVersReseau(applicationPid + " N_CONNECT.req " + sourceAddress + " " + destinationAddress);
		
      
	}
	
	private void ecrireVersReseau(String chaine)
	{
		chaine += '|';	//Ajoute le délimiteur à la chaîne
		try {
			
			for(int i=0; i < chaine.length(); i++)
			{
				transportOut.write(chaine.charAt(i));
			}
          
			transportOut.flush();
			
			
		} catch(Exception e) {
        	
			throw new RuntimeException(e);
        }
	}
	
	//Lecture de la couche réseau
	private void lireDeReseau()
	{
		
		String command = "";
		try {
			char c;
			do{
				
				c = (char)transportIn.read();
				
				if(c == '|')
				{
					if(!command.equals("stop"))
					{
						recevoirCommandeReseau(command);
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
			
			//Demande d'arrêt par la couche réseau
			}while((int)c != 65535);
			//}while(true);
          
		} catch(Exception e) {
        	
			//throw new RuntimeException(e);
			System.out.println("Arret de lecture de la couche transport");
        }
	}
	
	private void recevoirCommandeReseau(String command)
	{
		System.out.println("Transport recois une commande de réseau : " + command);
	}

}
