package OSI;

import java.util.ArrayList;
import java.util.Random;
/*
 * Contient la table de connexions de la couche trnasport
 */
public class TransportTableConnexion {
	
	//Garde la trace du nb. d'adresse utilisées. On pourra déterminer quand la table d'adress est pleine.
	private int nbUsedAddress = 0;
	private static final int NB_ADDRESSE_DISPONIBLE = 250;
	//Adresse en cours d'utilisation (array = traitement + rapide que liste)
	boolean[] usedAddress = new boolean[NB_ADDRESSE_DISPONIBLE];
	
	private ArrayList<TransportTableLigne> tableConnexion = new ArrayList<TransportTableLigne>();
	
	
	public TransportTableConnexion()
	{
		
	}
	
	//Ouvre une connexion (status = "pending")
	public void openConnection(int applicationPid)
	{
		//Génère deux addresses
		int adresseSource = getUnusedAddress();
		int adresseDestination = getUnusedAddress();
		
		//Cas ou il n'y a plus d'addresse de disponible
		if(adresseSource == -1 || adresseDestination == -1)
		{
			System.out.println("Plus d'addresse de disponile!!! \nFermeture du programme...");
			System.exit(0);
		}
		
		//Crée une ligne de données de connexion et la met dans la table de connexion
		TransportTableLigne transportTableLigne = 
				new TransportTableLigne(applicationPid, adresseSource, adresseDestination);
		
		//System.out.println(transportTableLigne.toString());
		
		
		tableConnexion.add(transportTableLigne);
		
	}
	//Ouvre une connexion (status = "established")
	public void confirmConnection(int applicationPid)
	{
		
	}
	
	public void closeConnection(int applicationPid)
	{
		
	}
	
	//Retourne l'adresse source en fonction d'un PID
	public int getSourceAddress(int applicationPid)
	{
		return getTransportTableLigneByAppId(applicationPid).getSourceAddress();
		
	}
	
	//Retourne l'adresse source en fonction d'un PID
	public int getDestinationAddress(int applicationPid)
	{
		return getTransportTableLigneByAppId(applicationPid).getDestinationAddress();
	}
	
	//Retourne une ligne de la table de connexion en fonction du pid d'une application
	private TransportTableLigne getTransportTableLigneByAppId(int applicationPid)
	{
		for (TransportTableLigne ligne : this.tableConnexion) {
			if(ligne.getApplicationPid() == applicationPid)
			{
				return ligne;
			}
		}
		
		return null;
	}
		
	//Retourne une adresse aléatoire, non utilisée dans [0,249]
	//Retourne -1 si la table d'adresse est pleine
	private int getUnusedAddress()
	{
		Integer address = null;
		boolean newAddressFound = false;
		
		//Vérifie s'il reste de la place dans la table d'adresse
		if(nbUsedAddress == NB_ADDRESSE_DISPONIBLE)
		{
			//La table est pleine
			return -1;
		}
		
		//Randomise jusqu'à une adresse libre
		while(!newAddressFound)
		{
			Random rand = new Random();
			address = rand.nextInt(NB_ADDRESSE_DISPONIBLE);	//[0,249]
			
			//Si l'adresse n'est pas utilisée
			if(usedAddress[address] == false)
			{
				usedAddress[address] = true;	//Marque l'adresse comme indisponible
				nbUsedAddress++;				//Incrémente le nombre d'adresses utilisées
				newAddressFound = true;			//Sort de la boucle
			}
			
		}
		
		return address;
	}
	
	//Ligne de la table de connexion
	private class TransportTableLigne
	{
		private int applicationPid;
		private int sourceAddress;
		private int destinationAddress;
		private boolean isConnectionEstablished;
		
		
		public TransportTableLigne(int applicationPid, int sourceAddress, int destinationAddress) {
			this.applicationPid = applicationPid;
			this.sourceAddress = sourceAddress;
			this.destinationAddress = destinationAddress;
			this.isConnectionEstablished = false;	//Pending
		}
		
		
		public int getApplicationPid() {
			return applicationPid;
		}
		public void setApplicationPid(int applicationPid) {
			this.applicationPid = applicationPid;
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
			return "TransportTableLigne [applicationPid=" + applicationPid
					+ ", sourceAddress=" + sourceAddress
					+ ", destinationAddress=" + destinationAddress
					+ ", isConnectionEstablished=" + isConnectionEstablished
					+ "]";
		}
		
		
		
	}
	
	
}