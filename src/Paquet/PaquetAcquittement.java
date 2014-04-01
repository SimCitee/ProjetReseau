package Paquet;

public class PaquetAcquittement extends Paquet {
	public PaquetAcquittement(int numeroConnexion, String pr) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet(pr, "00001");
	}

}
