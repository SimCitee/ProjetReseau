package OSI;

import java.util.ArrayList;

// Table de connexion de la couche Reseau
public class ReseauTableConnexion {

	// Table des connexions
	private ArrayList<ReseauTableLigne> tableConnexionReseau = new ArrayList<ReseauTableLigne>();
	
	// Constructeur vide de la table des connexions
	public ReseauTableConnexion()
	{
		
	}
	
	// Methode permettant d'ajouter une connexion a la table des connexions
	// Parametre : Numero de voie logique, Addresse source, Addresse destination, Identifiant d'extremite de connexion
	// Valeur de retour : Aucun
	public void nouvelleConnexion(int noConnexion, int addSource, int addDest, int niec) {
		
		ReseauTableLigne ligne = new ReseauTableLigne(noConnexion, addSource, addDest, niec);
		tableConnexionReseau.add(ligne);
	}
	
	// Methode permettant d'obtenir l'adresse source d'une connexion
	// Parametre : Identifiant d'extremite de connexion
	// Valeur de retour : Adresse source
	public int findAddSource(int niec) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNiec() == niec)
				return ligne.getAddSource();
		}
		return 0;
	}
	
	// Methode permettant d'obtenir l'adresse destination d'une connexion
	// Parametre : Identifiant d'extremite de connexion
	// Valeur de retour : Adresse destination
	public int findAddDest(int niec) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNiec() == niec)
				return ligne.getAddDest();
		}
		return 0;
	}
	
	// Methode permettant d'obtenir le numero de voie logique d'une connexion
	// Parametre : Identifiant d'extremite de connexion
	// Valeur de retour : Numero de voie logique
	public int findNoConnexion(int niec) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNiec() == niec)
				return ligne.getNoConnexion();
		}
		return 0;
	}
	
	// Methode permettant d'obtenir la valeur de P(S) d'une connexion
	// Parametre : Numero de voie logique
	// Valeur de retour : La valeur de P(S)
	public int findNoPs(int noConnexion) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNoConnexion() == noConnexion)
				return ligne.getPs();
		}
		return 0;
	}
	
	// Methode permettant d'obtenir la valeur de P(R) d'une connexion
	// Parametre : Numero de voie logique
	// Valeur de retour : La valeur de P(R)
	public int findNoPr(int noConnexion) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNoConnexion() == noConnexion)
				return ligne.getPr();
		}
		return 0;
	}
	
	// Methode permettant de retirer une connexion de la table des connexions
	// Parametre : Identifiant d'extremite de connexion
	// Valeur de retour : Aucun
	public void deleteLigne(int niec) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNiec() == niec) {
				tableConnexionReseau.remove(ligne);
				break;
			}
		}
	}
	
	// Methode permettant de changer le status d'une connexion (Confirmer une connexion)
	// Parametre : Identifiant d'extremite de connexion
	// Valeur de retour : Aucun
	public void connexionConfirmer(int niec) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNiec() == niec) {
				ligne.setConnectionEstablished(true);
				break;
			}
		}
	}
	
	// Methode permettant d'augmenter la valeur de P(S) d'une connexion
	// Parametre : Numero de voie logique
	// Valeur de retour : Aucun
	public void augmenterPS(int noConnexion) {
		int currentPS;
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNoConnexion() == noConnexion) {
				currentPS = ligne.getPs();
				if (currentPS == 7)
					ligne.setPs(0);
				else
					ligne.setPs(currentPS++);
				break;
			}
		}
	}
	
	// Methode permettant d'augmenter la valeur de P(R) d'une connexion
	// Parametre : Numero de voie logique
	// Valeur de retour : Aucun
	public void augmenterPR(int noConnexion) {
		int currentPR;
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNoConnexion() == noConnexion) {
				currentPR = ligne.getPr();
				if (currentPR == 7)
					ligne.setPr(0);
				else
					ligne.setPr(currentPR++);
				break;
			}
		}
	}
	
	// Classe representant une connexion de la table des connexion
	private class ReseauTableLigne {
		private int noConnexion;  // Numero de voie logique
		private int addSource;    // Addresse source
		private int addDest;      // Adresse destination
		private boolean isConnectionEstablished; // Vrai si la connexion a ete etablie
		private int niec; // Identifiant d'extremite de connexion
		private int pr;  // Numero du paquet que l'on envoie
		private int ps;  // Numero du prochain paquet que l'on attend du distant
		
		// Constructeur de la classe representant une une connexion de la table des connexions
		public ReseauTableLigne(int noConnexion, int addSource, int addDest, int niec) {
			this.noConnexion = noConnexion;
			this.addSource = addSource;
			this.addDest = addDest;
			this.isConnectionEstablished = false;
			this.niec = niec;
			this.pr = 0;
			this.ps = 0;
		}

		// Ci-dessous : Les getters et les setters de tout les attributs de la classe ReseauTableLigne
		public int getNoConnexion() {
			return noConnexion;
		}

		public void setNoConnexion(int noConnexion) {
			this.noConnexion = noConnexion;
		}

		public int getAddSource() {
			return addSource;
		}

		public void setAddSource(int addSource) {
			this.addSource = addSource;
		}

		public int getAddDest() {
			return addDest;
		}

		public void setAddDest(int addDest) {
			this.addDest = addDest;
		}

		public boolean isConnectionEstablished() {
			return isConnectionEstablished;
		}

		public void setConnectionEstablished(boolean isConnectionEstablished) {
			this.isConnectionEstablished = isConnectionEstablished;
		}

		public int getNiec() {
			return niec;
		}

		public void setNiec(int niec) {
			this.niec = niec;
		}
		public int getPr() {
			return pr;
		}

		public void setPr(int pr) {
			this.pr = pr;
		}

		public int getPs() {
			return ps;
		}

		public void setPs(int ps) {
			this.ps = ps;
		}
	}
}
