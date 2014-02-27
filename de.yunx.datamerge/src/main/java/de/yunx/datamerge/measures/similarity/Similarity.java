package de.yunx.datamerge.measures.similarity;

import de.yunx.datamerge.measures.dictionary.Thesaurus;

public abstract class Similarity {

	public abstract double getSimilarity(String s1, String s2);		
	
	private double tresh = 0.6;
	private Thesaurus thesa = null;

	public boolean isSimilar(String s1, String s2) {
		if (getSimilarity(s1, s2) > tresh)
			return true;
		return false;
	}

	public void setTreshold(double treshold) {
		if (treshold > 1)
			this.tresh = 1;
		else
			this.tresh = treshold;
	}

	public double getTreshold() {
		return this.tresh;
	}
	
	public void setThesaurus(Thesaurus thesaurus){
		thesa = thesaurus;
	}
	
	public Thesaurus getThesaurus(){
		return thesa;
	}
	
	
}
