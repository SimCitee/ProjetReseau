package OSI;

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
	
	public String traiter_paquet() {
		
		return "";
	}

}
