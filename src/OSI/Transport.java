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
public class Transport{
	
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
				doUserCommand(Integer.parseInt(s[0]), s[1]);
			}
		}
		
		//Ferme le fichier
		lecteurFichier.close();
		
		//Envoie une commande de fermeture à la couche réseau
		ecrireVersReseau("stop");
		
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
	private void doUserCommand(int applicationPid, String command)
	{
		
		//Ouverture de connexion
		if(command.equals("open"))
		{
			openConnection(applicationPid);
			
			
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
	private void openConnection(int applicationPid)
	{
		//Marque la connexion comme ouverte dans la table
		tableConnexion.openConnection(applicationPid);
		
		int sourceAddress = tableConnexion.getSourceAddress(applicationPid);
		int destinationAddress = tableConnexion.getDestinationAddress(applicationPid);
		
		//Passe la requête à la couche réseau
		ecrireVersReseau("N_CONNECT.req " + sourceAddress + " " + destinationAddress);
		
      
	}
	
	private void ecrireVersReseau(String chaine)
	{
		chaine += '|';	//Ajoute le délimiteur à la chaîne
		try {
			
			for(int i=0; i < chaine.length(); i++)
			{
				//System.out.println("Ecriture de : " + chaine.charAt(i));
				transportOut.write(chaine.charAt(i));
				//transportOut.write('c');
			}
          
			transportOut.flush();
			
			
		} catch(Exception e) {
        	
			throw new RuntimeException(e);
        }
		
	}
	
	//Lecture de la couche réseau
	public void lireDeReseau()
	{
		String command = "";
		try {
			
			while(true){
				char c = (char)transportIn.read();
				if(c == '|')
				{
					if(!command.equals("stop"))
					{
						executerCommandeReseau(command);
						command = "";
					}
					else{
						
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
	
	private void executerCommandeReseau(String command)
	{
		System.out.println("Commande reçu du réseau : " + command);
	}

}
