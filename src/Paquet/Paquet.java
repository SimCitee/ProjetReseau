package Paquet;

public class Paquet {
	protected int numeroConnexion;
	protected TypePaquet typePaquet;
	
	public Paquet(int numeroConnexion) {
		this.numeroConnexion = numeroConnexion;
	}
	
	public int getNumeroConnexion() {
		return numeroConnexion;
	}

	public void setNumeroConnexion(int numeroConnexion) {
		this.numeroConnexion = numeroConnexion;
	}

	public TypePaquet getTypePaquet() {
		return typePaquet;
	}

	public void setTypePaquet(TypePaquet typePaquet) {
		this.typePaquet = typePaquet;
	}

	public String toString() {
		return this.numeroConnexion + this.typePaquet.toString();
	}
	
}