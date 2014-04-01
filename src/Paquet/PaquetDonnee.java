package Paquet;

public class PaquetDonnee extends Paquet {
	private String donnee;
	
	public PaquetDonnee(int numeroConnexion, String pr, String m, String ps, String donnee) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet(pr, m, ps, "0");
		this.donnee = donnee;
	}

	@Override
	public String toString() {
		return this.numeroConnexion + " " + this.typePaquet.toString() + donnee + "\n";
	}
}
