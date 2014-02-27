package de.yunx.datamerge.measures.similarity;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.avro.util.Utf8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.yunx.datamerge.initialization.EntityClassifyer;
import de.yunx.datamerge.measures.dictionary.CompanyThesaurus;
import de.yunx.datamerge.measures.frequency.FrequencyDistribution;

//TODO w�rter in richtiger reihenfolge mit gleichem abstand

public class WeightedNGramSim extends Similarity {

	Logger logger = LoggerFactory.getLogger(WeightedNGramSim.class);
	private Ngram ngram;
	private FrequencyDistribution freq = null;
	private Connection connection = null;

	
	//TODO connecion hier in klasse überflüssig
	public WeightedNGramSim(Connection conn, FrequencyDistribution freqdist) {
		connection = conn;
		freq = freqdist;
		ngram = new Ngram();
		setThesaurus(new CompanyThesaurus());
		setTreshold(0.5);
	}

	public void close() throws SQLException {
		connection.close();
	}

	public void setFrequencyDistribution(FrequencyDistribution fd) {
		freq = fd;
	}

	public FrequencyDistribution getFrequencyDistribution(
			FrequencyDistribution fd) {
		return freq;
	}

	/**
	 * Removes double tokens from a string
	 * 
	 * @param s
	 *            String
	 * @return e.g. "a b a" -> "a b"
	 */
	public String removeDouble(String s) {
		String[] wordList = s.split(" ");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < wordList.length; i++) {
			boolean found = false;
			for (int j = i + 1; j < wordList.length; j++) {
				if (wordList[j].equals(wordList[i])) {
					found = true;
					break;
				}
			}
			if (!found) {
				if (sb.length() > 0)
					sb.append(' ');
				sb.append(wordList[i]);
			}
		}
		return sb.toString();
	}

	public double getSimilarity(String s1, String s2) {

		logger.debug("------------------------------------");
		logger.debug("COMPARING: "+s1);
		logger.debug("TO       : "+s2);
		
		s1 = getThesaurus().use(new Utf8(s1));
		s2 = getThesaurus().use(new Utf8(s2));

		s1 = removeDouble(s1);
		s2 = removeDouble(s2);

		String a1[] = s1.split("[^\\w]");
		String a2[] = s2.split("[^\\w]");
		int simcount = 0;

		double[][] s = new double[a1.length * a2.length][2];

		// set everything = 0
		for (int i = 0; i < a1.length * a2.length; i++) {
			// similarity
			s[i][0] = 0;
			// frequency weight
			s[i][1] = 0;
		}

		// for (String a1s : a1) {
		// for (String a2s : a2) {
		for (int i = 0; i < a1.length; i++) {
			String a1s = a1[i];
			for (int j = 0; j < a2.length; j++) {
				String a2s = a2[j];
				if (ngram.isSimilar(a1s, a2s)) {
					logger.debug("+ " + a1s + " - " + a2s + " -> "
							+ ngram.getSimilarity(a1s, a2s) + " freq "
							+ freq.getFrequency(a1s) + ", "
							+ freq.getFrequency(a2s));
					s[simcount][0] = ngram.getSimilarity(a1s, a2s);
					// in doubt, i assume that one word with regular writing is
					// compared to a word with spelling mistake, thus i take the
					// smaller weigt of both to not overestimate the importance
					s[simcount][1] = Math.min(freq.getFrequency(a1s),
							freq.getFrequency(a2s));
					simcount++;
				} 
				else
					logger.debug("- " + a1s + " - " + a2s + " -> "
							+ ngram.getSimilarity(a1s, a2s));
				

			}
		}
		double sum_weight = 0;
		double max_weight = 0;
		for (int j = 0; j < simcount; j++) {

			sum_weight += s[j][1] * s[j][0];
			if(max_weight<s[j][1])
				max_weight = s[j][1];
		}
		//weiging by max_weight
		logger.debug("### sum weight   " + sum_weight);
//		logger.debug("### max weight   "  + max_weight);
		if(simcount==0||Math.min(a1.length, a2.length)==0) return 0;
		sum_weight = (sum_weight/simcount + 3*simcount/Math.min(a1.length, a2.length))/4;
		logger.debug("### weight norm  " + sum_weight);
		return sum_weight;
		


//		if (simcount == 0) // division 0 avoid
//			return 0;
//		double norm_sum_freq_weight = sum_freq_weight
//				/ (a1.length < a2.length ? a1.length : a2.length);
//		System.out.println("### " + norm_sum_freq_weight);
//		return norm_sum_freq_weight;

	}

	// public void setTreshold(double treshold) {
	// ngram.setTreshold(treshold);
	// }
	//
	// public final double getTreshold() {
	// return ngram.getTreshold();
	// }

}
