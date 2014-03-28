package OSI;

import java.io.IOException;

import util.RedacteurFichier;

import com.sun.xml.internal.ws.api.message.Packet;

import Paquet.Paquet;
import Paquet.PaquetCommunicationEtablie;
import Paquet.PaquetDonnee;
import java.util.Random;


public class Liaison {
	
	private static Liaison instance = null;
	
	protected Liaison() {
		
	}
	
	// Singleton
	public static Liaison getInstance() {
		if (instance == null) {
			instance = new Liaison();
		}
		
		return instance;
	}
	
	//inscrit tout les paquets venant de la couche reseau dans un fichier
	public void lireDeReseau(Paquet paquet) {
		//inscrire le paquet dans le fichier de sortie
		ecrireVersPhysique(paquet);
		
		//appel de la méthode simulant la réception
		lireDePhysique(paquet);
	}
	
	// Simule la reception des paquets du distant en generant les paquets de reponses
	public String lireDePhysique(Paquet paquet) {
		
		// Si le paquet est une demande de connection
		if (paquet instanceof PaquetCommunicationEtablie) {
			
			// Si l'adresse source est un multiple de 19, aucune reponse
			if ((((PaquetCommunicationEtablie) paquet).getAdresseSource() % 19) == 0)
				return null;
			// Si l'adresse source est un multiple de 13, refuser la connexion
			else if ((((PaquetCommunicationEtablie) paquet).getAdresseSource() % 13) == 0)
				return Constante.DISCONNECT_IND;
			// Sinon, accepter la connexion
			else 
				return Constante.CONNECT_CONF;
		}
		// Si paquet de donnees
		else if (paquet instanceof PaquetDonnee) {
			Random rand = new Random();
			int aleatoire = rand.nextInt(7);
			
			// Si le Ps du paquet est equivalent au numero tire aleatoirement, acquittement negatif
			if (paquet.getTypePaquet().getDecimalPs() == aleatoire)
				return null;
		}
		
		return null;
		
		
	}
	
	private void ecrireVersPhysique(Paquet paquet) {
		
		try {
			RedacteurFichier.ecrireFichier(Constante.L_ECR_NAME, paquet.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
