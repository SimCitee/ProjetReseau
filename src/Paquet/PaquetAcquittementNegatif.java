package Paquet;

public class PaquetAcquittementNegatif extends Paquet {
	public PaquetAcquittementNegatif(String numeroConnexion, String pr) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet(pr, "01001");
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

	@Override
	public String toString() {
		return super.numeroConnexion + super.typePaquet.toString();
	}
}