package Paquet;

public class PaquetAppel extends Paquet {
	private int adresseSource;
	private int adresseDestination;
	
	public PaquetAppel(int numeroConnexion, int adresseSource, int adresseDestination) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet("00001011");
		this.adresseSource = adresseSource;
		this.adresseDestination = adresseDestination;
	}

	public void setAdresseSource(int adresseSource) {
		this.adresseSource = adresseSource;
	}

	public int getAdresseSource() {
		return adresseSource;
	}

	public int getAdresseDestination() {
		return adresseDestination;
	}

	public void setAdresseDestination(int adresseDestination) {
		this.adresseDestination = adresseDestination;
	}

	@Override
	public String toString() {
		return this.numeroConnexion + " " + this.typePaquet.toString() + " " + adresseSource + " " + adresseDestination + "\n";
	}
}
