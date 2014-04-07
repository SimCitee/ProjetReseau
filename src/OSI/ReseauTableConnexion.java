package OSI;

import java.util.ArrayList;

public class ReseauTableConnexion {

	private ArrayList<ReseauTableLigne> tableConnexionReseau = new ArrayList<ReseauTableLigne>();
	
	public ReseauTableConnexion()
	{
		
	}
	
	public void nouvelleConnexion(int noConnexion, int addSource, int addDest, int niec) {
		
		ReseauTableLigne ligne = new ReseauTableLigne(noConnexion, addSource, addDest, niec);
		tableConnexionReseau.add(ligne);
	}
	
	public int findNoConnexion(int niec) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNiec() == niec)
				return ligne.getNoConnexion();
		}
		return 0;
	}
	public int findNoPs(int noConnexion) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNoConnexion() == noConnexion)
				return ligne.getPs();
		}
		return 0;
	}
	public int findNoPr(int noConnexion) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNoConnexion() == noConnexion)
				return ligne.getPr();
		}
		return 0;
	}
	
	public void deleteLigne(int niec) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNiec() == niec) {
				tableConnexionReseau.remove(ligne);
				break;
			}
		}
	}
	public void connexionConfirmer(int niec) {
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNiec() == niec) {
				ligne.setConnectionEstablished(true);
				break;
			}
		}
	}
	public void augmenterPS(int noConnexion) {
		int currentPS;
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNoConnexion() == noConnexion) {
				currentPS = ligne.getPs();
				ligne.setPs(currentPS++);
				break;
			}
		}
	}
	public void augmenterPR(int noConnexion) {
		int currentPR;
		for(ReseauTableLigne ligne : tableConnexionReseau) {
			if (ligne.getNoConnexion() == noConnexion) {
				currentPR = ligne.getPr();
				ligne.setPr(currentPR++);
				break;
			}
		}
	}
	
	private class ReseauTableLigne {
		private int noConnexion;
		private int addSource;
		private int addDest;
		private boolean isConnectionEstablished;
		private int niec; // Identifiant d'extremite de connexion
		private int pr;
		private int ps;
		
		public ReseauTableLigne(int noConnexion, int addSource, int addDest, int niec) {
			this.noConnexion = noConnexion;
			this.addSource = addSource;
			this.addDest = addDest;
			this.isConnectionEstablished = false;
			this.niec = niec;
			this.pr = 0;
			this.ps = 0;
		}

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
