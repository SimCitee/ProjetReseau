package Paquet;

public class PaquetDemandeLiberation extends Paquet {
	private String adresseSource;
	private String adresseDestination;
	
	public PaquetDemandeLiberation(int numeroConnexion, String adresseSource, String adresseDestination) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet("00010011");
		this.adresseSource = adresseSource;
		this.adresseDestination = adresseDestination;
	}
	
	public String getAdresseSource() {
		return adresseSource;
	}

	public void setAdresseSource(String adresseSource) {
		this.adresseSource = adresseSource;
	}

	public String getAdresseDestination() {
		return adresseDestination;
	}

	public void setAdresseDestination(String adresseDestination) {
		this.adresseDestination = adresseDestination;
	}

	@Override
	public String toString() {
		return super.toString() + adresseSource + adresseDestination;
	}
}
