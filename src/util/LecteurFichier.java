package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;


/*
 * Contient des méthodes pour le travail sur des fichiers
 * 
 */
public class LecteurFichier {

	private String filepath;
	private FileInputStream fis = null;
    private BufferedReader reader = null;

	
	public LecteurFichier(String filepath)
	{
		this.filepath = filepath;
	}
	
	
	public boolean open()
	{
		try
		{
			fis = new FileInputStream(filepath);
			reader = new BufferedReader(new InputStreamReader(fis));
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}


	}
	
	public String readLine()
	{
		try
		{
			return reader.readLine();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "null";
		}
	}
	
	
	public void close()
	{
		try
		{
			reader.close();
			fis.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
