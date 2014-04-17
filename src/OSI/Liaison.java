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


public class Liaison {
	
	private static Liaison instance = null;
	private static TableLiaison table;
	
	protected Liaison() {
	}
	
	// Singleton
	public static Liaison getInstance() {
		if (instance == null) {
			instance = new Liaison();
			table = new TableLiaison();
		}
		
		return instance;
	}
	
	//inscrit tout les paquets venant de la couche reseau dans un fichier
	public Paquet lireDeReseau(Paquet paquet) {
				
		//inscrire le paquet dans le fichier de sortie
		ecrireVersFichier(Constante.L_ECR_NAME, paquet);
		
		//appel de la m�thode simulant la r�ception
		return lireDePhysique(paquet);
	}
	
	// Simule la reception des paquets du distant en generant les paquets de reponses
	private Paquet lireDePhysique(Paquet paquet) {
		
		Paquet reponse = null;
		int noVoieLogique = paquet.getNumeroVoieLogique();
		
		// Si le paquet est une demande de connection
		if (paquet instanceof PaquetAppel) {
			
			int adresseSource = ((PaquetAppel) paquet).getAdresseSource();
			int adresseDestination = ((PaquetAppel) paquet).getAdresseDestination();
			
			// Si l'adresse source est un multiple de 19, aucune reponse
			if ((((PaquetAppel) paquet).getAdresseSource() % 19) == 0) {
				
			// Si l'adresse source est un multiple de 13, refuser la connexion
			} else if ((((PaquetAppel) paquet).getAdresseSource() % 13) == 0) {
				
				table.retirerLigne(noVoieLogique);
				
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
			int aleatoire = rand.nextInt(7);
			
			// A confirmer!!
			int pr = Integer.parseInt(((PaquetDonnee) paquet).getTypePaquet().getPs());
			
			int noConnexion = ((PaquetDonnee) paquet).getNumeroVoieLogique();
			int adresseSource = table.getSourceAddress(noConnexion);
			
			// Si adresse source est un multiple de 15, ne recoit pas d'acquittement
			if ((adresseSource % 15) == 0) { 
				return null;
			// Si le Ps du paquet est equivalent au numero tire aleatoirement, acquittement negatif
			} else if (Integer.parseInt(paquet.getTypePaquet().getPs()) == aleatoire) {
				reponse = new PaquetAcquittementNegatif(noConnexion, String.valueOf(pr));
			} else {
				// Incrementer le Pr pour indiquer la prochaine trame attendue
				pr++;
				reponse = new PaquetAcquittement(noConnexion, String.valueOf(pr));
			}
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
