package Paquet;

public class PaquetCommunicationEtablie extends Paquet {
	private int adresseSource;
	private int adresseDestination;
	
	public PaquetCommunicationEtablie(String numeroConnexion, int adresseSource, int adresseDestination) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet("00001111");
		this.adresseSource = adresseSource;
		this.adresseDestination = adresseDestination;
	}
	
	@Override
	public String getNumeroConnexion() {
		return super.numeroConnexion;
	}

	@Override
	public void setNumeroConnexion(String numeroConnexion) {
		super.numeroConnexion = numeroConnexion;
	}

	@Override
	public TypePaquet getTypePaquet() {
		return super.typePaquet;
	}

	@Override
	public void setTypePaquet(TypePaquet typePaquet) {
		super.typePaquet = typePaquet;
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
		return super.numeroConnexion + super.typePaquet.toString() + adresseSource + adresseDestination;
	}
	
}
