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
		LecteurFichier lecteurFichier = new LecteurFichier(Constante.S_LEC_NAME);
		String ligne;
		
		//Ouvre le fichier de lecture
		if(lecteurFichier.open())
		{
			
			while((ligne = lecteurFichier.readLine()) != null)

			{
				//Separe la chaine avec l'espace (donne le pid[0] et la commande[1])
				String s[] = ligne.split("\\s");
				
				//Execute la commande contenue dans le fichier
				executerCommandeUtilisateur(Integer.parseInt(s[0]), s[1]);
				
				try {
					//Permet de mettre une certaine sequence dans l'envoi-reception de donnees vers/de
					//la couche reseau. Par exemple, il faut recevoir une confirmation de connexion avant
					//d'envoyer des donnees.
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
	
	//Debute la lecture de la couche reseau (thread)
	@Override
	public void run()
	{
		lireDeReseau();
	}
	
	/*
	 * Execute les commandes contenues dans le fichier S_lec
	 * 
	 * 3 choix possibles :
	 * 	   1. Ouverture de connexion
	 * 	   2. Envoie de donnees
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
		
		//Envoie de donnees
		else
		{
			//Dans ce cas, command == aux donnees à envoyer
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
		
		//Passe la requête à la couche reseau
		ecrireVersReseau(applicationPid + " " + Constante.CONNECT_REQ + " " + sourceAddress + " " + destinationAddress);
		
      
	}
	
	//Ferme une connexion en fonction du pid de l'application. Demande par l'utilisateur (fichier S_lec)
	private void fermerConnexion(int applicationPid)
	{
		tableConnexion.fermerConnexion(applicationPid);
	}
	
	private void envoyerDonnees(int pid, String donnees)
	{
		//Verifie si l'application est connecte
		if(tableConnexion.getEstConnecte(pid))
		{
			//Evoie les donnees � la couche reseau
			ecrireVersReseau(pid + " " + donnees);
		}
		else
		{
			//TODO afficher message d'erreur (envoi de donnees sans connexion)
		}
	}
	
	private void ecrireVersReseau(String chaine)
	{
		chaine += '|';	//Ajoute le delimiteur à la chaine
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
	
	//Lecture de la couche reseau
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
						
						//Le programme se termine apres la fin du thread de lecture du reseau par la couche transport
						System.out.println("Fin du programme");
						
						//Fermeture du tube de lecture
						transportIn.close();
						
						//Arrêt de lecture sur la couche reseau
						break;
					}
				}
				else
				{
					//Ajoute le charactere lu à la chaine
					command += c;
				}
			
			//Demande d'arrêt par la couche reseau
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
		
		System.out.println("Transport recoit une commande de reseau : " + command);
		
		if(command.equals("stop"))
		{
			
		}
		else
		{
			String splitCommand[] = command.split("\\s");
			int pid = Integer.parseInt(splitCommand[0]);
			String primitive = splitCommand[1];
			int adresseEnReponse = Integer.parseInt(splitCommand[2]);
			
			
			//Reception d'une indication de deconnexion (distant ou couche reseau)
			if(primitive.equals(Constante.DISCONNECT_IND))
			{
				String raison = splitCommand[3];
				
				fermerConnexionParReseau(pid, adresseEnReponse, raison);
			}
			//Reception d'une confirmation de connexion
			else if(primitive.equals(Constante.CONNECT_CONF))
			{
				confirmerConnexion(pid, adresseEnReponse);
			}
		}
		
		
		
	}
	
	//Marque une connexion comme confirmee
	private void confirmerConnexion(int pid, int adresseEnReponse)
	{
		tableConnexion.confirmerConnexion(pid);
	}
	
	//Ferme la connexion dans le cas ou la couche reseau ou le distant le decide
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
