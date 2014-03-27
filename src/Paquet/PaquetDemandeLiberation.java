package Paquet;

public class PaquetDemandeLiberation extends Paquet {
	private String adresseSource;
	private String adresseDestination;
	
	public PaquetDemandeLiberation(String numeroConnexion, String adresseSource, String adresseDestination) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet("00010011");
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
		return super.numeroConnexion + super.typePaquet.toString() + adresseSource + adresseDestination;
	}
}
