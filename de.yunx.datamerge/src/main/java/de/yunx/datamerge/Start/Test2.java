package de.yunx.datamerge.Start;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test2 {

	public Test2() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	

	public static void main(String[] args) {
		
		Pattern pat = Pattern.compile("[^a-zA-Z0-9]");
		Matcher matcher1 = pat.matcher(",,");
		//matcher1 = pat.matcher("x.p");
		 
		 while(matcher1.find())
			 System.out.println("si");
		 
		 if(matcher1.matches())
			 System.out.println("mactch");
		 
		 
		 int a = 25;
		 int b = 100;
		 System.out.println(""+((float)a/(float)b));
	}

}
