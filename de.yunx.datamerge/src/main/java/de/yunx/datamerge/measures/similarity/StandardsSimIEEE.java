package de.yunx.datamerge.measures.similarity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;

import de.yunx.datamerge.Start.StandardJoin;
import de.yunx.datamerge.measures.similarity.StandardsSim3.Entity;

/**
 * This class takes two csv files (tab " ; \) as input and compares its
 * contents. The putput is a csv file which connects similar entries.
 * 
 * this is a quite general approach as it simply extracts letters and number
 * parts of each entry and compares them.
 * 
 * Invoking this classes method getSimilarity(String s1, String s2) compares the
 * similarity of two strings.
 * 
 * @author Benjamin Brand
 */
public class StandardsSimIEEE extends Similarity {

	static Level loglevel = Level.INFO;
	static Logger log = Logger.getLogger(StandardsSimIEEE.class.getName());
	static ConsoleHandler consolehandler = new ConsoleHandler();

	private Levenshtein lev;

	Pattern pattern_numbers;

	public StandardsSimIEEE() {

		log.setUseParentHandlers(false);
		consolehandler.setLevel(loglevel);
		log.addHandler(consolehandler);
		log.setLevel(loglevel);

		lev = new Levenshtein();

		pattern_numbers = Pattern.compile("[0-9]{1,3}.*");
		// pattern2 = Pattern.compile("[a-zA-Z]*");
		// pattern3 = Pattern
		// .compile("[a-zA-Z]{0,1}[0-9]{1,2}[,\\.][0-9][,\\.][0-9]{1,2}");
		// pattern4 = Pattern.compile("\\([0-9\\.,]+\\)");
		// pattern5 = Pattern.compile("[0-9]{2,3}\\.[0-9]{2,3}");
		// pattern6 = Pattern.compile("[0-9\\-]{2,}");
	}

	/**
	 * Compares the similarity of two strings by tokenizing it, matching a class
	 * to every token and comparing.
	 * 
	 * @param string
	 *            to compare
	 * @return the likelihood of the string s matching. >0.5 can be considered
	 *         matching.
	 */
	@Override
	public double getSimilarity(String s1, String s2) {
		Matcher matcher;
		// s1 = s1.replaceAll("([0-9]) ([0-9])", "$1$2"); // TODO nicht elegant
		String[] s1a = s1.split(" ");
		String[] s2a = s2.split(" ");

		// maximum token (numbers and texts respectively) to store and compare
		int max_token = 3;

		String[] numbers1 = new String[max_token];
		String[] texts1 = new String[max_token];
		int num1_count = 0;
		int txt1_count = 0;

		String[] numbers2 = new String[max_token];
		String[] texts2 = new String[max_token];
		int num2_count = 0;
		int txt2_count = 0;

		for (int i = 0; i < s1a.length; i++) {
			matcher = pattern_numbers.matcher(s1a[i]);
			if (matcher.matches()) {
				if (num1_count < max_token) {
					log.finest("NUM1 " + s1a[i]);
					numbers1[num1_count] = s1a[i];
					num1_count++;
				}
			} else {
				if (txt1_count < max_token) {
					log.finest("TXT1 " + s1a[i]);
					texts1[txt1_count] = s1a[i];
					txt1_count++;
				}
			}
		}

		for (int i = 0; i < s2a.length; i++) {
			matcher = pattern_numbers.matcher(s2a[i]);
			if (matcher.matches()) {
				if (num2_count < max_token) {
					log.finest("NUM2 " + s2a[i]);
					numbers2[num2_count] = s2a[i];
					num2_count++;
				}
			} else {
				if (txt2_count < max_token) {
					log.finest("TXT2 " + s2a[i]);
					texts2[txt2_count] = s2a[i];
					txt2_count++;
				}
			}
		}

		int num_equal = 0;
		int txt_equal = 0;
		boolean num_pos1_equal = false;
		boolean num_pos1_similar = false;
		boolean txt_pos1_equal = false;

		for (int i = 0; i < max_token; i++)
			for (int j = 0; j < max_token; j++) {

				if (texts1[i] == null || texts2[j] == null)
					break;
				if (texts1[i].equals(texts2[j])) {
					txt_equal++;
					if (i == 0 || j == 0)
						txt_pos1_equal = true;
				}
			}

		for (int i = 0; i < max_token; i++)
			for (int j = 0; j < max_token; j++) {

				if (numbers1[i] == null || numbers2[j] == null)
					break;
				if (numbers1[i].length() > 3
						&& numbers2[j].length() > 3
						&& numbers1[i].subSequence(0, 3).equals(
								numbers2[j].subSequence(0, 3))
								&& lev.getSimilarityNorm(numbers1[i], numbers2[j])>0.6 // damit zu lange zahlen nicht gemathct werden wie 111978439243875 mit 1110000000
					num_pos1_similar = true;
				if (numbers1[i].equals(numbers2[j])) {
					num_equal++;
					if (i == 0 || j == 0)
						num_pos1_equal = true;

				}
			}

		double result = 0;

		if (num_pos1_equal)
			result += 0.3;
		else if (num_pos1_similar)
			result += 0.2;

		if (txt_pos1_equal)
			result += 0.1;

		result += 0.05 * num_equal;
		result += 0.05 * txt_equal;

		return result;
	}

	public static void main(String args[]) {

		String infile1 = "ieee_perinorm.txt";
		String infile2 = "ieee_dec.txt";
		String outfile = "ieee_out.txt";

		// String infile1 = "test1.txt";
		// String infile2 = "test2.txt";
		// String outfile = "test_out.txt";

		String path = "C:\\Users\\BB\\Dropbox\\Diplomarbeit\\standards matching\\";
		String results_path = path + "results/";

		StandardsSimIEEE stdsim = new StandardsSimIEEE();

		try {
			BufferedReader in1 = new BufferedReader(new FileReader(path
					+ infile1));
			BufferedReader in2 = new BufferedReader(new FileReader(path
					+ infile2));

			CSVReader cr1 = new CSVReader(in1, ';', '\"');
			List<String[]> list1 = cr1.readAll();
			cr1.close();
			Iterator<String[]> it1 = list1.iterator();
			CSVReader cr2 = new CSVReader(in2, ';', '\"');
			List<String[]> list2 = cr2.readAll();
			cr2.close();
			Iterator<String[]> it2 = list2.iterator();

			File f = new File(results_path + outfile);
			f.delete();//TODO
			log.info("Writing file '" + results_path + outfile + "'");
			PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(
					f, true)));
			pr.println("name1_original\tname2_original\tname1\tname2\tscore");

			int counter = 0;
			while (it1.hasNext()) {

				counter++;
				String s1 = it1.next()[0];
				if (counter % 200 == 0)
					log.info("#" + counter + ": Processing status: "
							+ ((float) counter / (float) list1.size()) + "%");
				it2 = list2.iterator();
				while (it2.hasNext()) {
					String s2 = it2.next()[0];
					String[] splitted1 = s1.split("\\*");
					String[] splitted2 = s2.split("\\*");
					for (String s1_single : splitted1)
						for (String s2_single : splitted2) {
							double simi = stdsim.getSimilarity(s1_single,
									s2_single);
							if (simi > 0) {
								pr.println(s1 + "\t" + s2 + "\t" + s1_single
										+ "\t" + s2_single + "\t" + simi);
								log.fine(s1 + "\t" + s2 + "\t" + s1_single
										+ "\t" + s2_single + "\t" + simi);
							}
						}
				}

				pr.flush();
			}

			pr.close();
			log.info("Process sucessfully completed. See file '" + results_path
					+ outfile + "' for results.");

		} catch (FileNotFoundException e) {
			log.throwing(StandardJoin.class.getName(), "main", e);
		} catch (IOException e) {
			log.throwing(StandardJoin.class.getName(), "main", e);
		}

	}

}
