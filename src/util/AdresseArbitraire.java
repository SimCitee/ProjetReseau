package util;

import java.lang.reflect.Array;

/*
 * Cette classe sert à retourner des addresses pour le jeu d'essais.
 * Elle retourne les adresses pour lesquels il y a des traitement spéciaux,
 * par exemple 
 * 
 */


public class AdresseArbitraire {
	private int curseur = 0; 		//Conserve l'emplacement de la dernière addresse utilisé
	private int compteurAddresse = 0;	//Permet de savoir si l'adresse demandée est une source ou une destination
	private Integer adresses[];
	
	
	public AdresseArbitraire()
	{
		
		adresses = new Integer[]{38, 			//Multiple de 19
								 26,			//Multiple de 13
								 30,			//Multiple de 15
								 27};			//Multiple de 27
		
		
	}
	
	public Integer getNouvelleAddresse()
	{
		
		
		//S'il n'y a plus adresse dans le tableau
		if(curseur >= adresses.length)
			return null;
		
		//Si l'adresse demandee est pour la destination (on utilise une addresse random)
		if(compteurAddresse++ % 2 == 1)
			return null;
		
		return adresses[curseur++];
	}
	
	
}
