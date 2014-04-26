package OSI;

import java.io.IOException;

import util.RedacteurFichier;

import com.sun.xml.internal.ws.api.message.Packet;

import Paquet.Paquet;
import Paquet.PaquetAcquittement;
import Paquet.PaquetAcquittementNegatif;
import Paquet.PaquetAppel;
import Paquet.PaquetCommunicationEtablie;
import Paquet.PaquetDonnee;
import Paquet.PaquetIndicationLiberation;

import java.util.Random;

//Singleton
public class Liaison {
	
	private static Liaison instance = null;
	private static TableLiaison table;						// table contenant les numeros de voie logique pour chaque connexion
	
	private final int _PAQUETAPPEL_MULT_NOANSWER = 19;		// multiple pour paquet appel (aucune reponse si multiple)
	private final int _PAQUETAPPEL_MULT_REFUSE = 13;		// multiple pour paquet appel (paquet refus de connexion si multiple)
	private final int _PAQUETDONNEES_MULT_NOANSWER = 15;	// multiple pour paquet donnees (aucune reponse si multiple)
	private final int _MAX_RAND = 7;						// definit le maximum pour les nombres generes aleatoirement
	
	protected Liaison() {
	}
	
	// constructeur
	public static Liaison getInstance() {
		if (instance == null) {
			instance = new Liaison();
			table = new TableLiaison();
		}
		
		return instance;
	}
	
	/*
	 * Inscrit tout les paquets venant de la couche reseau dans un fichier et fait appel a
	 * une simulation d'une lecture de la couche physique
	 * Parametre: paquet sortant
	 * Valeur de retour: paquet entrant (reponse)
	 */
	public Paquet lireDeReseau(Paquet paquet) {
				
		//inscrire le paquet dans le fichier de sortie
		ecrireVersFichier(Constante.L_ECR_NAME, paquet);
		
		//appel de la méthode simulant la réception
		return lireDePhysique(paquet);
	}
	
	/*
	 * Simule la reception des paquets du distant en generant les paquets de reponses
	 * Parametre: paquet sortant
	 * Valeur de retour: paquet entrant (reponse)
	 */
	private Paquet lireDePhysique(Paquet paquet) {
		
		Paquet reponse = null;
		int noVoieLogique = paquet.getNumeroVoieLogique();
		
		// Si le paquet est une demande de connection
		if (paquet instanceof PaquetAppel) {
			
			// Obtenir les adresses source et destination du paquet
			int adresseSource = ((PaquetAppel) paquet).getAdresseSource();
			int adresseDestination = ((PaquetAppel) paquet).getAdresseDestination();
			
			// Si l'adresse source est un multiple de 19, aucune reponse
			if ((((PaquetAppel) paquet).getAdresseSource() % _PAQUETAPPEL_MULT_NOANSWER) == 0) {
				System.out.println("Couche Liaison: Paquet appel, aucune reponse, adresse source : " + ((PaquetAppel) paquet).getAdresseSource());
			// Si l'adresse source est un multiple de 13, refuser la connexion
			} else if ((((PaquetAppel) paquet).getAdresseSource() % _PAQUETAPPEL_MULT_REFUSE) == 0) {
				System.out.println("Couche Liaison: Paquet appel, refu de connexion, adresse source : " + ((PaquetAppel) paquet).getAdresseSource());
				// retirer la connexion de la table
				table.retirerLigne(noVoieLogique);
				//preparer un paquet d'indication de liberation
				reponse = new PaquetIndicationLiberation(noVoieLogique, adresseSource, adresseDestination, "distant");
				
			// Sinon, accepter la connexion
			} else {
				table.ajoutLigne(noVoieLogique, adresseSource, adresseDestination);
				reponse = new PaquetCommunicationEtablie(noVoieLogique, adresseSource, adresseDestination);
			}
		}
		// Si paquet de donnees
		else if (paquet instanceof PaquetDonnee) {
			Random rand = new Random();
			int aleatoire = rand.nextInt(_MAX_RAND);
			int pr = Integer.parseInt(((PaquetDonnee) paquet).getTypePaquet().getPs());
			
			// obtenir l'adresse source du paquet a l'aide de la table de voie logique
			int adresseSource = table.getSourceAddress(noVoieLogique);
			
			// Si adresse source est un multiple de 15, ne recoit pas d'acquittement
			if ((adresseSource % _PAQUETDONNEES_MULT_NOANSWER) == 0) { 
				System.out.println("Couche Liaison: Paquet donnees, aucune reponse, adresse source " + adresseSource);
			// Si le Ps du paquet est equivalent au numero tire aleatoirement, acquittement negatif
			} else if (Integer.parseInt(paquet.getTypePaquet().getPs()) == aleatoire) {
				System.out.println("Couche Liaison: Paquet donnees, acquittement negatif, Ps=" + adresseSource +", Rand="+aleatoire);
				reponse = new PaquetAcquittementNegatif(noVoieLogique, String.valueOf(pr));
			} else {
				// Incrementer le Pr pour indiquer la prochaine trame attendue
				pr++;
				reponse = new PaquetAcquittement(noVoieLogique, String.valueOf(pr));
			}
		// Si paquet d'indication de liberation, retirer la connexion de la table
		} else if (paquet instanceof PaquetIndicationLiberation) {
			table.retirerLigne(noVoieLogique);
		}
		
		// si un paquet a ete creer, ecrire dans le fichier de sortie
		if (reponse != null) {
			//inscrire le paquet dans le fichier de sortie
			ecrireVersFichier(Constante.L_LEC_NAME, reponse);
		}
		
		return reponse;
	}
	
	/*
	 * Ecrit le contenu d'un paquet dans un fichier
	 * Parametres: nom du fichier, paquet
	 * Valeur de retour: aucune
	 */
	private void ecrireVersFichier(String nomFichier, Paquet paquet) {
				
		try {
			RedacteurFichier.ecrireFichier(nomFichier, paquet.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
