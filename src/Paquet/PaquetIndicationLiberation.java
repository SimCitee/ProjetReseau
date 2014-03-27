package Paquet;

public class PaquetIndicationLiberation extends Paquet {
	private final String DISTANT = "00000001";
	private final String FOURNISSEUR = "00000010";
	private String adresseSource;
	private String adresseDestination;
	private String raison;
	
	public PaquetIndicationLiberation(String numeroConnexion, String adresseSource, String adresseDestination, String refu) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet("00010011");
		this.adresseSource = adresseSource;
		this.adresseDestination = adresseDestination;
		setRaison(refu);
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

	public void setRaison(String refu) {
		if(refu == "distant") {
			this.raison = DISTANT;
		} else {
			this.raison = FOURNISSEUR;
		}
	}
	
	public String getRaison() {
		return raison;
	}

	public void setAdresseDestination(String adresseDestination) {
		this.adresseDestination = adresseDestination;
	}

	@Override
	public String toString() {
		return super.numeroConnexion + super.typePaquet.toString() + adresseSource + adresseDestination + raison;
	}
}
