package de.yunx.datamerge.measures.similarity;

import java.util.StringTokenizer;

public class StandardsSim extends Similarity {

	public StandardsSim() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getSimilarity(String s1, String s2) {
		int num_count = 0;
		int str_count = 0;
		String[] num_arr = new String[5];
		String[] str_arr = new String[5];
		double[] weights = { 0.4, 0.4, 0.1, 0.05, 0.05 };
		StringTokenizer tok1 = new StringTokenizer(s1, " *");
		while (tok1.hasMoreElements()) {
			String nxt = tok1.nextToken();
			if (nxt.matches("[a-zA-Z]{0,1}[0-9\\.,]+")) {
//				System.out.print(nxt+" # ");
				if (num_count < num_arr.length) { // 5 char maximum
					num_arr[num_count] = nxt;
					num_count++;
				}
			} else {
				if (str_count < str_arr.length) {
					str_arr[str_count] = nxt;
					str_count++;
				}
			}
		}

		double str_score = 0;
		double num_score = 0;

		StringTokenizer tok2 = new StringTokenizer(s2, " *");
		while (tok2.hasMoreElements()) {
			String nxt = tok2.nextToken();
			for (int i = 0; i < str_arr.length; i++) {
				if (nxt.equals(str_arr[i])) {
					str_score += weights[i];
//					System.out.println("str "+nxt);
				}
			}
			for (int i = 0; i < num_arr.length; i++) {
				if (nxt.equals(num_arr[i])) {
					num_score += weights[i];
					System.out.println("num "+nxt);
				}
			}
		}

		return 0.4 * str_score + 0.6 * num_score;
	}
}
