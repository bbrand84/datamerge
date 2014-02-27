package de.yunx.datamerge.Start;

import de.yunx.datamerge.measures.dictionary.StandardsDict;
import de.yunx.datamerge.measures.similarity.Levenshtein;
import de.yunx.datamerge.measures.similarity.Ngram;
import de.yunx.datamerge.measures.similarity.StandardsSim3;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Levenshtein lev = new Levenshtein();
		System.out.println(lev.getSimilarity("Kartoffel", "kartoffel"));
		System.out.println(lev.getSimilarity("Kartoffel".toLowerCase(), "kartoffel".toLowerCase()));
		System.out.println(lev.getSimilarity("Kartofel", "kartoffel"));
		System.out.println(lev.getSimilarity("Kartoffele", "kartoffel"));
		System.out.println(lev.getSimilarity("ETSI TS 4567821 v 3", "TS 4567821 v 3"));
		
		System.out.println(lev.getSimilarityNorm("Kartoffel", "kartoffel"));
		System.out.println(lev.getSimilarityNorm("Kartoffel".toLowerCase(), "kartoffel".toLowerCase()));
		System.out.println(lev.getSimilarityNorm("Kartofel", "kartoffel"));
		System.out.println(lev.getSimilarityNorm("Kartoffele", "kartoffel"));
		System.out.println(lev.getSimilarityNorm("ETSI TS 4567821 v 3", "TS 4567821 v 3"));
		System.out.println("AB ELEKTRONIK GMBH");
		System.out.println(lev.getSimilarityNorm("AB", "ABB"));
		System.out.println(lev.getSimilarityNorm("ABENUM", "ABB"));
		System.out.println(lev.getSimilarityNorm("ABSOLENT", "ABB"));
		System.out.println();
		Ngram ngram = new Ngram();
		System.out.println(ngram.getSimilarity("AB", "ABB"));
		System.out.println(ngram.getSimilarity("ABABENUM", "ABB"));
		System.out.println(ngram.getSimilarity("ABSOLENT", "ABB"));
		

		String s = "assda 23423 655656 were".replaceAll("[0-9] [0-9]", "\\1\\2");
		System.out.println(s);
		
		
		System.out.println();
		
		StandardsSim3 ss3 = new StandardsSim3();
		
		int[] arr = ss3.getSimilarityArray("ETSI TS 123123 v1.4.5", "ETSI TS 123123 v1.4.5");

		for(int x : arr)
			System.out.println(x);


		StandardsDict sd = new StandardsDict();
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		sd.addToDictionary("Sack", 12);
	}

}
