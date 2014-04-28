package OSI;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;

import Paquet.Paquet;
import util.LecteurFichier;
import util.RedacteurFichier;

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
				String s[] = ligne.split("\\s", 2);
				
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
			
			if(tableConnexion.getEstConnecte(applicationPid))
			{
				int sourceAddress = tableConnexion.getSourceAddress(applicationPid);
				int destinationAddress = tableConnexion.getDestinationAddress(applicationPid);
				ecrireVersReseau(applicationPid + " " + Constante.DISCONNECT_REQ + " " + sourceAddress + " " + destinationAddress);
			
				fermerConnexion(applicationPid);
			}
			
		}
		
		//Envoie de donnees
		else
		{
			//Dans ce cas, command == aux donnees à envoyer
			envoyerDonnees(applicationPid, command);
		}
		
		
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
		//ecrireVersReseau(pid + " " + Constante.DATA_REQ + " " + donnees);
		//Verifie si l'application est connecte
		if(tableConnexion.getEstConnecte(pid))
		{
			//Evoie les donnees vers la couche reseau
			ecrireVersReseau(pid + " " + Constante.DATA_REQ + " " + donnees);
		}
		
	}
	
	private void ecrireVersReseau(String chaine)
	{
		chaine += '|';	//Ajoute le delimiteur a la chaine
		
		//System.out.println("Ecrireversreseau : " + chaine);
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
			String splitCommand[] = command.split("\\s", 3);
			int pid = Integer.parseInt(splitCommand[0]);
			String primitive = splitCommand[1];
			String primitiveParam = splitCommand[2];
			
			
			//Reception d'une indication de deconnexion (distant ou couche reseau)
			if(primitive.equals(Constante.DISCONNECT_IND))
			{
				String splitCommand2[] = primitiveParam.split("\\s", 3);
				int adresseSource = Integer.parseInt(splitCommand2[0]);
				int adresseDestination = Integer.parseInt(splitCommand2[1]);
				String raison = splitCommand2[2];
				
				fermerConnexionParReseau(pid, adresseSource, adresseDestination, raison);
			}
			//Reception d'une confirmation de connexion
			else if(primitive.equals(Constante.CONNECT_CONF))
			{
				String splitCommand2[] = primitiveParam.split("\\s", 2);
				int adresseSource = Integer.parseInt(splitCommand2[0]);
				int adresseDestination = Integer.parseInt(splitCommand2[1]);
				confirmerConnexion(pid, adresseSource, adresseDestination);
			}
		}
		
		
		
	}
	
	//Marque une connexion comme confirmee
	private void confirmerConnexion(int pid, int adresseSource, int adresseDestination)
	{
		tableConnexion.confirmerConnexion(pid);
		System.out.println("Connexion établie");
		ecrireVersFichier("Confirmation de connexion! Application # " + pid + " Adresse source : " + adresseSource +
				" Adresse destination : " + adresseDestination);
	}
	
	//Ferme la connexion dans le cas ou la couche reseau ou le distant le decide
	private void fermerConnexionParReseau(int pid, int adresseSource, int adresseDestination, String raison)
	{
		tableConnexion.fermerConnexion(pid);
		
		ecrireVersFichier("Le reseau ferme la connexion! Application # " + pid + " Adresse source : " + adresseSource +
				" Adresse destination : " + adresseDestination + " Raison : " + raison);
		
	}
	
	
	public void afficherTable()
	{
		tableConnexion.afficher();
		System.out.println("*******************************************************************");
		
	}
	
	private void ecrireVersFichier(String ligne) {
		
		try {
			RedacteurFichier.ecrireFichier(Constante.S_ECR_NAME, ligne + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
