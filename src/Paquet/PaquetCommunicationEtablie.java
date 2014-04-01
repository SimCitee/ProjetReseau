package Paquet;

public class PaquetCommunicationEtablie extends Paquet {
	private int adresseSource;
	private int adresseDestination;
	
	public PaquetCommunicationEtablie(int numeroConnexion, int adresseSource, int adresseDestination) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet("00001111");
		this.adresseSource = adresseSource;
		this.adresseDestination = adresseDestination;
	}

	public int getAdresseSource() {
		return adresseSource;
	}

	public void setAdresseSource(int adresseSource) {
		this.adresseSource = adresseSource;
	}

	public int getAdresseDestination() {
		return adresseDestination;
	}

	public void setAdresseDestination(int adresseDestination) {
		this.adresseDestination = adresseDestination;
	}

	@Override
	public String toString() {
		return super.toString() + adresseSource + adresseDestination;
	}
	
}
