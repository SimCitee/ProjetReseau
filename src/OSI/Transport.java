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
	
	//Pipes
	PipedOutputStream transportOut;
	PipedInputStream transportIn;
	
	//Fichier de lecture
	private static final String S_LEC_NAME = System.getProperty("user.dir") + "\\src\\fichiers\\S_lec.txt";
	
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
				
				try {
					//Permet de mettre une certaine séquence dans l'envoi-réception de données vers/de
					//la couche réseau. Par exemple, il faut recevoir une confirmation de connexion avant
					//d'envoyer des données.
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		//Ferme le fichier
		lecteurFichier.close();
		
		//Envoit une commande de fermeture des tubes
		ecrireVersReseau("stop");
		
		
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
			fermerConnexion(applicationPid);
		}
		
		//Envoie de données
		else
		{
			//Dans ce cas, command == aux données à envoyer
		}
		
		//TODO à enlever
		//afficherTable();
	}
	
	//Ouverture d'une connexion
	private void ouvrirConnexion(int applicationPid)
	{
		//Marque la connexion comme ouverte dans la table
		tableConnexion.openConnection(applicationPid);
		
		int sourceAddress = tableConnexion.getSourceAddress(applicationPid);
		int destinationAddress = tableConnexion.getDestinationAddress(applicationPid);
		
		//Passe la requête à la couche réseau
		ecrireVersReseau(applicationPid + " " + Constante.CONNECT_REQ + " " + sourceAddress + " " + destinationAddress);
		
      
	}
	
	//Ferme une connexion en fonction du pid de l'application. Demandé par l'utilisateur (fichier S_lec)
	private void fermerConnexion(int applicationPid)
	{
		tableConnexion.fermerConnexion(applicationPid);
	}
	
	private void envoyerDonnees(int pid, String donnees)
	{
		//Vérifie si l'application est connecté
		if(tableConnexion.getEstConnecte(pid))
		{
			//Evoie les données à la couche réseau
			ecrireVersReseau(pid + " " + donnees);
		}
		else
		{
			//TODO afficher message d'erreur (envoi de données sans connexion)
		}
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
			e.printStackTrace();
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
						
						//Le programme se termine après la fin du thread de lecture du réseau par la couche transport
						System.out.println("Fin du programme");
						
						//Fermeture du tube de lecture
						transportIn.close();
						
						//Arrêt de lecture sur la couche réseau
						break;
					}
				}
				else
				{
					//Ajoute le charactère lu à la chaine
					command += c;
				}
			
			//Demande d'arrêt par la couche réseau
			//}while((int)c != 65535);
			}while(true);
          
		} catch(Exception e) {
        	
			//throw new RuntimeException(e);
			e.printStackTrace();
			System.out.println("Arret de lecture de la couche transport");
        }
	}
	
	private void recevoirCommandeReseau(String command)
	{
		
		System.out.println("Transport reçoit une commande de réseau : " + command);
		
		if(command.equals("stop"))
		{
			
		}
		else
		{
			String splitCommand[] = command.split("\\s");
			int pid = Integer.parseInt(splitCommand[0]);
			String primitive = splitCommand[1];
			int adresseEnReponse = Integer.parseInt(splitCommand[2]);
			
			
			//Réception d'une indication de déconnexion (distant ou couche réseau)
			if(primitive.equals(Constante.DISCONNECT_IND))
			{
				String raison = splitCommand[3];
				
				fermerConnexionParReseau(pid, adresseEnReponse, raison);
			}
			//Réception d'une confirmation de connexion
			else if(primitive.equals(Constante.CONNECT_CONF))
			{
				confirmerConnexion(pid, adresseEnReponse);
			}
		}
		
		
		
	}
	
	//Marque une connexion comme confirmée
	private void confirmerConnexion(int pid, int adresseEnReponse)
	{
		tableConnexion.confirmerConnexion(pid);
	}
	
	//Ferme la connexion dans le cas où la couche réseau ou le distant le décide
	private void fermerConnexionParReseau(int pid, int adresseEnReponse, String raison)
	{
		tableConnexion.fermerConnexion(pid);
	}
	
	//TODO Effacer cette methode (tests uniquement)
	public void afficherTable()
	{
		tableConnexion.afficher();
		System.out.println("*******************************************************************");
		
	}
}
