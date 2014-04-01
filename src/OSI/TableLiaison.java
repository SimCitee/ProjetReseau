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
	
	public void retirerLigne(int noConnexion) {
		for (LiaisonTableLigne ligne : this.table) {
			if(ligne.getNoConnexion() == noConnexion)
			{
				this.table.remove(ligne);
			}
		}
	}
	
	//Retourne l'adresse source en fonction d'un PID
	public int getSourceAddress(int noConnexion)
	{
		return getLiaisonTableLigneByNoConnexion(noConnexion).getSourceAddress();	
	}
	
	//Retourne une ligne de la table de connexion en fonction du numero de connexion
	private LiaisonTableLigne getLiaisonTableLigneByNoConnexion(int noConnexion)
	{
		for (LiaisonTableLigne ligne : this.table) {
			if(ligne.getNoConnexion() == noConnexion)
			{
				return ligne;
			}
		}
		
		return null;
	}
		
	
	//Ligne de la table de connexion
	private class LiaisonTableLigne
	{
		private int noConnexion;
		private int sourceAddress;
		private int destinationAddress;
		
		public LiaisonTableLigne(int noConnexion, int sourceAddress, int destinationAddress) {
			this.noConnexion = noConnexion;
			this.sourceAddress = sourceAddress;
			this.destinationAddress = destinationAddress;
		}
		
		public int getNoConnexion() {
			return noConnexion;
		}
		public void setNoConnexion(int noConnexion) {
			this.noConnexion = noConnexion;
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
			return "LiaisonTableLigne [noConnexion=" + noConnexion
					+ ", sourceAddress=" + sourceAddress
					+ ", destinationAddress=" + destinationAddress
					+ "]";
		}
		
		
		
	}
	
	
}