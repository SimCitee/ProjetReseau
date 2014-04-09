package OSI;

import java.util.ArrayList;
import java.util.Random;

/*
 * Contient la table de connexions de la couche Liaison
 */
public class TableLiaison {
	
	private ArrayList<LiaisonTableLigne> table = new ArrayList<LiaisonTableLigne>();
	
	public TableLiaison()
	{
		
	}
	
	//ajouter une ligne dans la table
	public void ajoutLigne(int noConnexion, int adresseSource, int adresseDestination)
	{
		
		//Cree une ligne de données de connexion et la met dans la table de connexion
		LiaisonTableLigne transportTableLigne = 
				new LiaisonTableLigne(noConnexion, adresseSource, adresseDestination);		
		
		table.add(transportTableLigne);
		
	}
	
	public void retirerLigne(int noVoieLogique) {
		LiaisonTableLigne t = getLiaisonTableLigneByNoVoieLogique(noVoieLogique);
				
		if(t != null)
			table.remove(t);
	}
	
	//Retourne l'adresse source en fonction d'un PID
	public int getSourceAddress(int noVoieLogique)
	{
		return getLiaisonTableLigneByNoVoieLogique(noVoieLogique).getSourceAddress();	
	}
	
	//Retourne une ligne de la table de connexion en fonction du numero de connexion
	private LiaisonTableLigne getLiaisonTableLigneByNoVoieLogique(int noVoieLogique)
	{
		for (LiaisonTableLigne ligne : this.table) {
			if(ligne.getNoVoieLogique() == noVoieLogique)
			{
				return ligne;
			}
		}
		
		return null;
	}
		
	
	//Ligne de la table de connexion
	private class LiaisonTableLigne
	{
		private int noVoieLogique;
		private int sourceAddress;
		private int destinationAddress;
		
		public LiaisonTableLigne(int noVoieLogique, int sourceAddress, int destinationAddress) {
			this.noVoieLogique = noVoieLogique;
			this.sourceAddress = sourceAddress;
			this.destinationAddress = destinationAddress;
		}
		
		public int getNoVoieLogique() {
			return noVoieLogique;
		}
		public void setNoVoieLogique(int noVoieLogique) {
			this.noVoieLogique = noVoieLogique;
		}
		public int getSourceAddress() {
			return sourceAddress;
		}
		public void setSourceAddress(int sourceAddress) {
			this.sourceAddress = sourceAddress;
		}
		public int getDestinationAddress() {
			return destinationAddress;
		}
		public void setDestinationAddress(int destinationAddress) {
			this.destinationAddress = destinationAddress;
		}


		@Override
		public String toString() {
			return "LiaisonTableLigne [noVoieLogique=" + noVoieLogique
					+ ", sourceAddress=" + sourceAddress
					+ ", destinationAddress=" + destinationAddress
					+ "]";
		}
		
		
		
	}
	
	
}