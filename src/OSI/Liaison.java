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
	
	public void lireDeReseau(PaquetCommunicationEtablie packet) {
		//inscrire le packet dans le fichier de sortie
		ecrireVersPhysique((Paquet) packet);
		
		//appel de la méthode simulant la réception
		lireDePhysique(packet);
	}
	
	public void lireDeReseau(PaquetDonnee packet) {
		//inscrire le packet dans le fichier de sortie
		ecrireVersPhysique((Paquet) packet);
		
		//appel de la méthode simulant la réception
		lireDePhysique(packet);
	}
	
	public String lireDePhysique(PaquetCommunicationEtablie packet) {
		
		if ((packet.getAdresseSource() % 19) == 0)
			return null;
		else if ((packet.getAdresseSource() % 13) == 0)
			return Constante.DISCONNECT_IND;
		else 
			return Constante.CONNECT_CONF;
	}
	
	public String lireDePhysique(PaquetDonnee packet) {
		
		Random rand = new Random();
		int aleatoire = rand.nextInt(7);
		
		if (packet.getTypePaquet().getDecimalPs() == aleatoire) {
			return null;
		}
		return null;
	}
	
	private void ecrireVersPhysique(Paquet packet) {
		
		try {
			RedacteurFichier.ecrireFichier(Constante.L_ECR_NAME, packet.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
