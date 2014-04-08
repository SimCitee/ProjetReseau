package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RedacteurFichier {

	/*
	 * Ecrit le contenu dans le fichier specifier
	 * Parametres: chemin vers le fichier, contenu du fichier
	 * Valeur de retour: aucune
	 */
	public static void ecrireFichier(String path, String contenu) throws IOException {
		try {
			File fichier = new File(path);
			
			if (!fichier.exists()) {
				fichier.createNewFile();
			}
						
			FileWriter fw = new FileWriter(fichier.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(contenu);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
