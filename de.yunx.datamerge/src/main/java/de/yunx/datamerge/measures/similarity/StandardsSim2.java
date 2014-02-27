package de.yunx.datamerge.measures.similarity;


public class StandardsSim2 extends Similarity {

	public StandardsSim2() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getSimilarity(String s1, String s2) {
		
		String nums1 = "";
		String strs1 = "";
		
		char[] s1a = s1.toCharArray();
		for(char c : s1a)
		{
		String cc = new Character(c).toString();
		if(cc.matches("[0-9]"))
			nums1+=cc;
		else if(cc.matches("[a-zA-Z]"))
			strs1+=cc;
		}
		
		if(nums1.length()>5)
			nums1 = nums1.substring(0,6);
		if(strs1.length()>5)
			strs1 = strs1.substring(0,6);
		
		
		
		
		String nums2 = "";
		String strs2 = "";
		
		char[] s2a = s2.toCharArray();
		for(char c : s2a)
		{
		String cc = new Character(c).toString();
		if(cc.matches("[0-9]"))
			nums2+=cc;
		else if(cc.matches("[a-zA-Z]"))
			strs2+=cc;
		}
		
		if(nums2.length()>5)
			nums2 = nums2.substring(0,6);
		if(strs2.length()>5)
			strs2 = strs2.substring(0,6);
		
//		System.out.println(nums2);
//		System.out.println(strs2);

		
		Levenshtein lev = new Levenshtein();
		//System.out.println(lev.getSimilarity(strs1, strs2));
		//if(lev.getSimilarity(nums1, nums2)==0 && lev.getSimilarity(strs1, strs2)<6){
		if(nums1.equals(nums2) && lev.getSimilarity(strs1, strs2)<6){
				return 1;}
		else return 0;
		
	}
}
