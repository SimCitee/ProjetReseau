package Paquet;

public abstract class Paquet {
	protected String numeroConnexion;
	protected TypePaquet typePaquet;
	
	public Paquet(String numeroConnexion) {
		this.numeroConnexion = numeroConnexion;
	}
	
	public abstract String getNumeroConnexion();
	public abstract void setNumeroConnexion(String numeroConnexion);
	
	public abstract TypePaquet getTypePaquet();
	public abstract void setTypePaquet(TypePaquet typePaquet);
	
	public abstract String toString();
}