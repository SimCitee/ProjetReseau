package OSI;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Random;

import Paquet.Paquet;
import Paquet.PaquetAcquittement;
import Paquet.PaquetAcquittementNegatif;
import Paquet.PaquetAppel;
import Paquet.PaquetCommunicationEtablie;
import Paquet.PaquetDonnee;
import Paquet.PaquetIndicationLiberation;

// Classe Reseau
public class Reseau  extends Thread{
	
	PipedOutputStream reseauOut;                   // Tube sortant
	PipedInputStream reseauIn;                 	   // Tube entrant
	private int compteurNoConnexion = 1;           // Compteur permettant d'assigner les numeros de voies logiques
	private ReseauTableConnexion tableConnexion = new ReseauTableConnexion(); // Table des connexions
	private final int _LONGUEUR_PAQUET = 128;      // Taille maximale d'un paquet de données
	
	// Constructeur de la classe Reseau
	public Reseau(PipedOutputStream reseauOut, PipedInputStream reseauIn)
	{
		this.reseauOut = reseauOut; // Initialisation du tube sortant
		this.reseauIn = reseauIn;   // Initialisation du tube entrant
	}
	
	//Debut du thread
	public void run()
	{
		//Debute la lecture de la couche transport
		lireDeTransport();
	}
	
	// Methode permettant d'effectuer la lecture de ce que Transport nous envoie via le tube
	// Parametre : Aucun
	// Valeur de retour : Aucun
	private void lireDeTransport()
	{
		
		String command = "";
		try {
			char c;
			do{
				
				c = (char)reseauIn.read(); // Lecture du tube
				
				if(c == '|') // Permet de verifier s'il s'agit du dernier caractere de la commande
				{
					if(!command.equals("stop"))
					{
						executerCommandeTransport(command); // Execution de la commande
						command = "";
					}
					else{
						
						//Envoi le signal d'arret de lecture a la couche transport
						ecrireVersTransport("stop");
						
						//Fermeture du tube de lecture
						reseauIn.close(); 
						
						//Arret de lecture de la couche reseau
						break;
					}
				}
				else
				{
					//Ajoute le charactere lu a la chaine
					command += c;
				}
			
			//Demande d'arret par le producteur
			}while(true);
          
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Arret de lecture de la couche reseau");
        }
	}
	
	// Methode permettant d'écrire vers la couche Transport via le tube sortant
	// Parametre : Ce que l'on veut envoyé à Transport
	// Valeur de retour : Aucun
	public void ecrireVersTransport(String chaine)
	{
		chaine += '|';	//Ajoute le delimiteur a la chaine
		try {
			
			for(int i=0; i < chaine.length(); i++)
				reseauOut.write(chaine.charAt(i)); // Écrire vers transport
          
			reseauOut.flush(); // Vider le tampon
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
        }
	}

	
	// Methode permettant de traiter les commandes provenant de la couche Transport et effectue le traitement correspondant
	// Parametre : Commande de la couche transport
	// Valeur de retour: Aucun
	private void executerCommandeTransport(String command)
	{
		String[] commandArray = null;                             // Tableau de commandes
		ArrayList<Paquet> listePaquet = new ArrayList<Paquet>();  // Liste de paquets
		Paquet paquet = null;                      // Paquet que l'on envoie
		Paquet reponse = null;                     // Paquet que l'on reçoit
		commandArray = command.split(" ");         // Tableau permettant de décomposer les commandes
		boolean versLiaison = true;                // Permet de vérifier si l'on envoie un paquet au distant
		int noConnexion;                           // Numero de voie logique
		
		System.out.println("Reseau recois une commande de transport : " + command);
		
		// On verifie quelle est la primitive recu de la part de transport
		switch (commandArray[1]) {
			case "N_CONNECT.req" : // Primitive de demande de connexion
				int temp = Integer.parseInt(commandArray[2]);
				
				ajouterTableLigne(commandArray); // On ajoute la connexion dans la table de connexion
				noConnexion = tableConnexion.findNoConnexion(Integer.parseInt(commandArray[0]));
				
				// Si l'adresse source est un multiple de 27, le fournisseur de service refuse la connexion
				if ((temp % 27) != 0) {			
					// On construit un paquet d'appel a envoyé au distant via la liaison de données
					paquet = new PaquetAppel(noConnexion, Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]));
					listePaquet.add(paquet); // On ajoute le paquet a la liste des paquets
				}
				else {
					versLiaison = false; // On envoie rien au distant
					// On envoie une primitive d'indication de liberation a la couche Transport
					ecrireVersTransport(commandArray[0] + " N_DISCONNECT.ind " + commandArray[2] + " " + commandArray[3] + " Refus de connexion");
					// On supprime la connexion de la table des connexions
					tableConnexion.deleteLigne(Integer.parseInt(commandArray[0]));
				} 
				break;
			case "N_DATA.req" : // Il s'agit d'un paquet de données
				String niec = commandArray[0]; // Identifiant d'extremite de connexion
				String data = "";              // Donnees a envoye au distant
				String dataTemp;               // Donnees temporaires a envoye au distant
				int nbPaquet;                  // Nombre de paquet necessaires
				int compteurPaquet = 0;        // Compteur de paquet
				int nops;                      // Numero de P(S)
				int nopr;                      // Numero de P(R)
				
				// Recuperation du numero de voie logique, du P(S) et du P(R)
				noConnexion = tableConnexion.findNoConnexion(Integer.parseInt(niec));
				nops = tableConnexion.findNoPs(noConnexion);
				nopr = tableConnexion.findNoPr(noConnexion);
				
				// On reconstruit les donnees en une seule chaine de caractere
				for (int i = 2; i < commandArray.length; i++) {
					data += commandArray[i];
					if (i < commandArray.length - 1)
						data += " ";
				}
				
				// Évaluation du nombre de paquets nécessaires
				nbPaquet = (int)Math.ceil((double)data.length() / _LONGUEUR_PAQUET);
				
				// Segmentation des données pour faire plusieurs paquets, si necessaire
				do {
					nops = (nops < 8) ? nops : 0;
					
					if (data.length() < _LONGUEUR_PAQUET) { // Si plus petit que 128 caracteres
						// Construction du paquet de donnees
						paquet = new PaquetDonnee(noConnexion, String.valueOf(nopr), "0", String.valueOf(nops), data);
						listePaquet.add(paquet); // Ajout du paquet a la liste des paquets
					}
					else { // Si plus long que 128 caracteres
						dataTemp = data.substring(0, _LONGUEUR_PAQUET);
						data = data.substring(_LONGUEUR_PAQUET);
						// Construction du paquet de donnees
						paquet = new PaquetDonnee(noConnexion, String.valueOf(nopr), "1", String.valueOf(nops), dataTemp);
						listePaquet.add(paquet); // Ajout du paquet a la liste des paquets
					} 
					compteurPaquet++; // On incrémente le compteur de paquet
					nops++;	// incrementer le Ps
					// Tant que le compteur de paquet est plus petit que le nombre de paquet necessaire
				} while (compteurPaquet < nbPaquet);
				break;
			case "N_DISCONNECT.req" : // Il s'agit d'un paquet de demande de libération		
				// Recuperation du numero de voie logique dans la table de connexion
				noConnexion = tableConnexion.findNoConnexion(Integer.parseInt(commandArray[0]));
				// Construction du paquet d'indication de liberation
				paquet = new PaquetIndicationLiberation(noConnexion, Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]), "reseau");
				listePaquet.add(paquet); // Ajout du paquet a la liste des paquets
				// Suppression de la connexion dans la table de connexion
				tableConnexion.deleteLigne(Integer.parseInt(commandArray[0]));
				break;
		}
		
		// Si on envoie un paquet a la liaison de donnees
		if (versLiaison == true) {
			
			// Pour chacun des paquets de la liste des paquets
			for(int i = 0; i < listePaquet.size(); i++) {
				reponse = Liaison.getInstance().lireDeReseau(listePaquet.get(i)); // Envoie du paquet...
			
				// Declenchement du temporisateur
				if ((reponse == null)  && (!(listePaquet.get(i) instanceof PaquetIndicationLiberation))) {
					// Deuxieme tentative d'envoie du paquet
					reponse = Liaison.getInstance().lireDeReseau(listePaquet.get(i));
					if (reponse == null) { // Si toujours pas de reponse, on libere la connexion
						int addSource = tableConnexion.findAddSource(Integer.parseInt(commandArray[0]));
						int addDest = tableConnexion.findAddDest(Integer.parseInt(commandArray[0]));
						// On voie une indication d'indication de liberation a Transport
						ecrireVersTransport(commandArray[0] + " N_DISCONNECT.ind " + addSource + " " + addDest + " Pas de reponse");
						// On supprime la connexion de la table de connexion
						tableConnexion.deleteLigne(Integer.parseInt(commandArray[0]));
						break;
					}
				}
				// Paquet d'acquittement negatif recu de liaison de donnees
				if (reponse instanceof PaquetAcquittementNegatif) {
					tableConnexion.augmenterPR(reponse.getNumeroVoieLogique()); // Augmenter la valeur de P(R) 
					augmenterPaquetPR(listePaquet, i+1); // Augmenter la valeur de P(R)
					// Seconde tentative d'envoie du paquet
					reponse = Liaison.getInstance().lireDeReseau(listePaquet.get(i));
					
					if (reponse instanceof PaquetAcquittementNegatif) { // Si toujours pas de reponse, on libere la connexion
						int addSource = tableConnexion.findAddSource(Integer.parseInt(commandArray[0]));
						int addDest = tableConnexion.findAddDest(Integer.parseInt(commandArray[0]));
						// On voie une indication d'indication de liberation a Transport
						ecrireVersTransport(commandArray[0] + " N_DISCONNECT.ind " + addSource + " " + addDest + " Pas de reponse");
						// On supprime la connexion de la table de connexion
						tableConnexion.deleteLigne(Integer.parseInt(commandArray[0]));
						break;
					}
					
				}
				// Paquet d'indication de liberation recu de liaison de donnees
				if (reponse instanceof PaquetIndicationLiberation) {
					// On envoie un indication de liberation a Transport
					ecrireVersTransport(commandArray[0] + " N_DISCONNECT.ind " + commandArray[2] + " " + commandArray[3] + " Distant libere la connexion");
					// On supprime la connexion de la table des connexions
					tableConnexion.deleteLigne(Integer.parseInt(commandArray[0]));
				}
				// Paquet de communication etablie recu de liaison de donnees
				else if (reponse instanceof PaquetCommunicationEtablie) {
					// Envoie de la primitive de confirmation de connexion a Transport
					ecrireVersTransport(commandArray[0] + " N_CONNECT.conf " + commandArray[2] + " " + commandArray[3]);
					// Mettre l'etat de la connexion a Etablie dans la table de connexion
					tableConnexion.connexionConfirmer(Integer.parseInt(commandArray[0]));
				}
				// Paquet d'acquittemetn recu de liaison de donnees
				else if (reponse instanceof PaquetAcquittement) {
					// Augmenter les valeurs de P(S) et de P(R) dans les paquets et dans la table de connexion
					tableConnexion.augmenterPR(reponse.getNumeroVoieLogique());
					tableConnexion.augmenterPS(reponse.getNumeroVoieLogique());
					//augmenterPaquetPR(listePaquet, i+1);
					//augmenterPaquetPS(listePaquet, i+1);
				}
			}
		}
	}

	// Methode permettant d'augmenter la valeur de P(R) dans un paquet
	// Parametre : la liste des paquets, le paquet que l'on veut changer la valeur
	// Valeur de retour : Aucun
	private void augmenterPaquetPR(ArrayList<Paquet> listePaquet, int compteur) {
		int temp;
		for (int i = compteur; i < listePaquet.size()-1; i++) {
			temp = Integer.parseInt(listePaquet.get(i).getTypePaquet().getPr());
			temp++;
			listePaquet.get(i+1).getTypePaquet().setPr(String.valueOf(temp));
		}
	}
	
	// Methode permettant d'augmenter la valeur de P(S) dans un paquet
	// Parametre : la liste des paquets, le paquet que l'on veut changer la valeur
	// Valeur de retour : Aucun
	private void augmenterPaquetPS(ArrayList<Paquet> listePaquet, int compteur) {
		int temp;
		for (int i = compteur; i < listePaquet.size()-1; i++) {
			temp = Integer.parseInt(listePaquet.get(i).getTypePaquet().getPs());
			temp++;
			listePaquet.get(i+1).getTypePaquet().setPs(String.valueOf(temp));
		}
	}
	
	// Methode permettant d'ajouter une connexion a la table des connexions
	// Parametre : Une commande provenant de Transport
	// Valeur de retour : Aucun
	private void ajouterTableLigne(String[] commandArray) {		
		tableConnexion.nouvelleConnexion(compteurNoConnexion++, Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]), Integer.parseInt(commandArray[0]));	
	}
	
	
}
