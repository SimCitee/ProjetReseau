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

/*
 * Singleton pour la couche réseau
 * 
 */
public class Reseau  extends Thread{
	
	//Pipes
	PipedOutputStream reseauOut;
	PipedInputStream reseauIn;
	private int compteurNoConnexion = 1;
	private ReseauTableConnexion tableConnexion = new ReseauTableConnexion();
	
	public Reseau(PipedOutputStream reseauOut, PipedInputStream reseauIn)
	{
		this.reseauOut = reseauOut;
		this.reseauIn = reseauIn;
	}
	
	//Début du thread
	public void run()
	{
		//Débute la lecture de la couche transport
		lireDeTransport();
		
		
	}
	
	//Lecture de la couche transport
	private void lireDeTransport()
	{
		
		String command = "";
		try {
			char c;
			do{
				
				c = (char)reseauIn.read();
				
				if(c == '|')
				{
					if(!command.equals("stop"))
					{
						executerCommandeTransport(command);
						command = "";
					}
					else{
						
						//Envoi le signal d'arret de lecture à la couche transport
						ecrireVersTransport("stop");
						
						//Fermeture du tube de lecture
						reseauIn.close();
						
						//Arrêt de lecture de la couche réseau
						break;
					}
				}
				else
				{
					//Ajoute le charactère lu à la chaine
					command += c;
				}
			
			//Demande d'arrêt par le producteur
			//}while((int)c != 65535);
			}while(true);
          
		} catch(Exception e) {
			e.printStackTrace();
			//throw new RuntimeException(e);
			System.out.println("Arret de lecture de la couche reseau");
        }
	}
	
	//ecriture sur la couche transport
	public void ecrireVersTransport(String chaine)
	{
		chaine += '|';	//Ajoute le délimiteur à la chaîne
		try {
			
			for(int i=0; i < chaine.length(); i++)
			{
				//System.out.println("Ecriture de : " + chaine.charAt(i));
				reseauOut.write(chaine.charAt(i));
				//transportOut.write('c');
			}
          
			reseauOut.flush();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
        }
	}
	
	//Les commande complètes provenant de la couche transport sont ici!!!
	private void executerCommandeTransport(String command)
	{
		String[] commandArray = null;
		ArrayList<Paquet> listePaquet = new ArrayList<Paquet>();
		Paquet paquet = null;
		Paquet reponse = null;
		commandArray = command.split(" ");
		boolean versLiaison = true;
		int noConnexion;
		

		System.out.println("Reseau recois une commande de transport : " + command);
		
		// ON ENVOIE A LIAISON
		
		switch (commandArray[1]) {
			case "N_CONNECT.req" : 
				int temp = Integer.parseInt(commandArray[2]);
				
				ajouterTableLigne(commandArray);
				noConnexion = tableConnexion.findNoConnexion(Integer.parseInt(commandArray[0]));
				
				//TODO : Il faudrait pas changer le code du if et du else ? Présentement on laisse passer la connexion seulement si elle est un multiple de 27.
				if (temp % 27 == 0) {
					paquet = new PaquetAppel(noConnexion, Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]));
					listePaquet.add(paquet);
				}
				else {
					versLiaison = false;
					ecrireVersTransport(commandArray[0] + " N_DISCONNECT.ind " + commandArray[2] + " " + commandArray[3] + " Refus de connexion");
					tableConnexion.deleteLigne(Integer.parseInt(commandArray[0]));
				} 
				break;
			case "N_DATA.req" :
				String niec = commandArray[0];
				String data = "";
				String dataTemp;
				int nbPaquet;
				int compteurPaquet = 0;
				int nops;
				int nopr;
				
				noConnexion = tableConnexion.findNoConnexion(Integer.parseInt(niec));
				nops = tableConnexion.findNoPs(noConnexion);
				nopr = tableConnexion.findNoPr(noConnexion);
				
				for (int i = 2; i < commandArray.length; i++) {
					data += commandArray[i];
					if (i < commandArray.length - 1)
						data += " ";
				}
				nbPaquet = (int)Math.ceil(data.length() / 128);
				
				do {
					if (data.length() < 128) {
						paquet = new PaquetDonnee(noConnexion, String.valueOf(nopr), "0", String.valueOf(nops), data);
						listePaquet.add(paquet);
					}
					else {
						dataTemp = data.substring(0, 128);
						data = data.substring(128);
						paquet = new PaquetDonnee(noConnexion, String.valueOf(nopr), "1", String.valueOf(nops), dataTemp);
						listePaquet.add(paquet);
					}
					compteurPaquet++;
				} while (compteurPaquet < nbPaquet);
				
			case "N_DISCONNECT.req" : 
				noConnexion = tableConnexion.findNoConnexion(Integer.parseInt(commandArray[0]));
				
				paquet = new PaquetIndicationLiberation(noConnexion, Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]), "distant");
				listePaquet.add(paquet);
				tableConnexion.deleteLigne(Integer.parseInt(commandArray[0]));
				break;
				// TODO
				// Manque la gestion de la reponse de liaison (Pas encore coder du cote Liaison)
		}
		
		
		// ON RECOIT DE LIAISON
		
		if (versLiaison == true) {
			for(int i = 0; i < listePaquet.size(); i++) {
				reponse = Liaison.getInstance().lireDeReseau(listePaquet.get(i));
			
				// Temporisateur
				if (reponse == null) {
					reponse = Liaison.getInstance().lireDeReseau(listePaquet.get(i));
				}
				if (reponse instanceof PaquetAcquittementNegatif) {
					tableConnexion.augmenterPR(reponse.getNumeroConnexion());
					augmenterPaquetPR(listePaquet, i+1);
					reponse = Liaison.getInstance().lireDeReseau(listePaquet.get(i));
					
				}
				if (reponse instanceof PaquetIndicationLiberation) {
					ecrireVersTransport(commandArray[0] + " N_DISCONNECT.ind " + commandArray[2] + " " + commandArray[3] + " Distant");
					tableConnexion.deleteLigne(Integer.parseInt(commandArray[0]));
				}
				else if (reponse instanceof PaquetCommunicationEtablie) {
					ecrireVersTransport(commandArray[0] + " N_CONNECT.conf " + commandArray[2] + " " + commandArray[3]);
					tableConnexion.connexionConfirmer(Integer.parseInt(commandArray[0]));
				}
				else if (reponse instanceof PaquetAcquittement) {
					// TODO On fait quoi quand on a un paquet d'acquittement positif
					
					tableConnexion.augmenterPR(reponse.getNumeroConnexion());
					tableConnexion.augmenterPS(reponse.getNumeroConnexion());
					augmenterPaquetPR(listePaquet, i+1);
					augmenterPaquetPS(listePaquet, i+1);
				}
			}
		}
	}

	private void augmenterPaquetPR(ArrayList<Paquet> listePaquet, int compteur) {
		int temp;
		for (int i = compteur; i < listePaquet.size(); i++) {
			temp = Integer.parseInt(listePaquet.get(i).getTypePaquet().getPr());
			temp++;
			listePaquet.get(i+1).getTypePaquet().setPr(String.valueOf(temp));
		}
	}
	
	private void augmenterPaquetPS(ArrayList<Paquet> listePaquet, int compteur) {
		int temp;
		for (int i = compteur; i < listePaquet.size(); i++) {
			temp = Integer.parseInt(listePaquet.get(i).getTypePaquet().getPs());
			temp++;
			listePaquet.get(i+1).getTypePaquet().setPs(String.valueOf(temp));
		}
	}

	private void ajouterTableLigne(String[] commandArray) {		
		tableConnexion.nouvelleConnexion(compteurNoConnexion++, Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]), Integer.parseInt(commandArray[0]));	
	}
	
	
}
