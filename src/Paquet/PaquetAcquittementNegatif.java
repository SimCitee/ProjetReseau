package Paquet;

public class PaquetAcquittementNegatif extends Paquet {
	public PaquetAcquittementNegatif(int numeroConnexion, String pr) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet(pr, "01001");
	}

}