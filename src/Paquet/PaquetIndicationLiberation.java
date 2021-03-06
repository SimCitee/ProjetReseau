package Paquet;

public class PaquetIndicationLiberation extends Paquet {
	private final String DISTANT = "00000001";
	private final String FOURNISSEUR = "00000010";
	private int adresseSource;
	private int adresseDestination;
	private String raison;
	
	public PaquetIndicationLiberation(int numeroConnexion, int adresseSource, int adresseDestination, String refu) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet("00010011");
		this.adresseSource = adresseSource;
		this.adresseDestination = adresseDestination;
		setRaison(refu);
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

	public void setRaison(String refu) {
		if(refu.equalsIgnoreCase("distant")) {
			this.raison = DISTANT;
		} else if (refu.equalsIgnoreCase("reseau")) {
			this.raison = FOURNISSEUR;
		}
	}
	
	public String getRaison() {
		return raison;
	}

	public void setAdresseDestination(int adresseDestination) {
		this.adresseDestination = adresseDestination;
	}

	@Override
	public String toString() {
		return "Voie logique: "+ this.numeroVoieLogique + " Type paquet: " + this.typePaquet.toString() + " Adresse source: " + adresseSource + " Adresse destination: " + adresseDestination + " Raison: " + raison + "\n";
	}
}
