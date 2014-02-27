package de.yunx.datamerge.measures.similarity;

/**
 * Calculates the dice coefficient of two strings 
 * @author BB
 *
 */
public class Ngram extends Similarity {

	
	private int ngramsize = 2;
	
	public Ngram(){		
	}
	
	public Ngram(int ngramsize){
		this.ngramsize = ngramsize;
	}
	
	/**
	 * Returns Dice coefficient based on N-Gram comparison 
	 */
	public double getSimilarity(String s1, String s2){

		// word length diff should not be too much, also no empty strings
		if(s1.length()==0 ||s2.length()==0 || Math.abs(s1.length()-s2.length()) / Math.min(s1.length(),s2.length()) > 0.4)
				return 0;
		s1="$"+s1+"$";
		s2="$"+s2+"$";

		
		double matches = 0;
		for (int i = 0; i < s1.length() - ngramsize + 1; i++) {
			for (int j = 0; j < s2.length() - ngramsize + 1; j++) {
				if (s1.regionMatches(true, i, s2, j, ngramsize)) {
					matches++;
					break; // to avoid double counting like in corporation (2x "or" per word) -> score > 1
				}
			}
		}		
		//dice coefficient
		return (2*matches/(s1.length()+s2.length()-2));

	}

	

}
