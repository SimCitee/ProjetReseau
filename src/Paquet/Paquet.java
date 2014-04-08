package Paquet;

public class Paquet {
	protected int numeroVoieLogique;
	protected TypePaquet typePaquet;
	
	public Paquet(int numeroConnexion) {
		this.numeroVoieLogique = numeroConnexion;
	}
	
	public int getNumeroConnexion() {
		return numeroVoieLogique;
	}

	public void setNumeroConnexion(int numeroConnexion) {
		this.numeroVoieLogique = numeroConnexion;
	}

	public TypePaquet getTypePaquet() {
		return typePaquet;
	}

	public void setTypePaquet(TypePaquet typePaquet) {
		this.typePaquet = typePaquet;
	}

	public String toString() {
		return "Voie logique: "+ this.numeroVoieLogique + " Type paquet: " + this.typePaquet.toString() + "\n";
	}
	
}