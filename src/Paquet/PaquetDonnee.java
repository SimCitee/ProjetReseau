package Paquet;

public class PaquetDonnee extends Paquet {
	private String donnee;
	
	public PaquetDonnee(String numeroConnexion, String pr, String m, String ps, String donnee) {
		super(numeroConnexion);
		super.typePaquet = new TypePaquet(pr, m, ps, "0");
		this.donnee = donnee;
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
		return super.numeroConnexion + typePaquet.toString() + donnee;
	}
}
