package Paquet;

public class PaquetAcquittement extends Paquet {
	public PaquetAcquittement(String numeroConnexion, String pr) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet(pr, "00001");
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
