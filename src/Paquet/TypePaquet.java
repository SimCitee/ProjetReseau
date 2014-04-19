package Paquet;

public class TypePaquet {
	private String valeur = "";
	private String pr = "";
	private String m = "";
	private String ps = "";
	private String last = "";
	
	public TypePaquet(String valeur) {
		this.valeur = valeur;
	}
	
	public TypePaquet(String pr, String valeur) {
		this.pr = pr;
		this.valeur = valeur;
	}
	
	public TypePaquet(String pr, String m, String ps, String last) {
		this.pr = pr;
		this.m = m;
		this.ps = ps;
		this.last = last;
	}

	public String getValeur() {
		return valeur;
	}

	public void setValeur(String valeur) {
		this.valeur = valeur;
	}

	public String getPr() {
		return pr;
	}

	public void setPr(String pr) {
		this.pr = pr;
	}

	public String getM() {
		return m;
	}

	public void setM(String m) {
		this.m = m;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}
	
	public String getLast() {
		return ps;
	}

	public void setLast(String last) {
		this.last = last;
	}
	
	public String toString() {
		if (this.pr.equalsIgnoreCase("") && this.last.equalsIgnoreCase(""))
			return "[VAL:" + valeur + "]";
		else if (this.last.equalsIgnoreCase(""))
			return "[VAL:" + valeur + ", PR:" + pr +  "]";
		else
			return "[PR:"+ pr + ", M:" + m + ", PS:" + ps + ", FBIT:" + last + "]";
	}
}
