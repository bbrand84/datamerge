package de.yunx.datamerge.initialization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.avro.util.Utf8;

import de.yunx.datamerge.measures.dictionary.CompanyThesaurus;
import de.yunx.datamerge.utils.Classification;

/**
 * This class helps to do sequence classification. it splits lists of names into
 * its single tokens and applies it to a suitable format for crf sequence
 * tagging (i used mallet). eventually it applies collections of words (lexica)
 * from files automatically to the matching tokens. the rest must be done
 * manually
 * 
 * 
 * @author BB
 * 
 */
public class PreTaggerByLexicon {

	static Level loglevel = Level.FINEST;
	static Logger log = Logger.getLogger(PreTaggerByLexicon.class.getName());

	static int OTHER = 0; // 0000000
	static int LEGAL = 1; // 0000001
	static int COMPANY = 2; // 0000010
	static int NAME1 = 4; // 0000100
	static int NAME2 = 8; // 0001000
	static int PREPOSITION = 16;
	static int CITY = 32;
	static int STREET = 64;
	static int STATE = 128;
	static int COUNTRY = 256;
	static int SPECIFICATION = 512; // german technical...
	static int TITLE = 1024; // prof dr ing etc

	public HashMap<String, Integer> dictionary = new HashMap<String, Integer>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PreTaggerByLexicon p = new PreTaggerByLexicon();
		try {
			// System.out.println(p.getFromLexiconAsString("company"));
			// System.out.println(p.getFromLexiconAsInt("company"));
			// System.out.println(p.getFromLexiconAsString("siemens"));
			// System.out.println(p.getFromLexiconAsString("OF"));
			// System.out.println(p.getFromLexiconAsInt("OF"));
			// System.out.println("--- "+p.dictionary.get("of"));
			// System.out.println(Classification.getAsString(1));
			// System.out.println(Classification.getAsString(2));
			// System.out.println(Classification.getAsString(3));
			// System.out.println(Classification.getAsString(8));
			// System.out.println(Classification.getAsString(16));
			// System.out.println(Classification.getAsString(24));
			// System.out.println(Classification.getAsString(26));

			// p.convertDocToMalletCRFFeed(
			// "data/companies as to declarations.csv",
			// "data/results/CRFFEED.txt");
			String doc_in = "data/companies as to declarations.csv";
			String doc_out = "data/results/company list out.csv";
			log.info("Converting document '" + doc_in + "', saving to '"
					+ doc_out + "'.");
			p.convertDocToMalletCRFFeed(doc_in, doc_out);
			log.info("Finished.");

		} catch (NoDictionaryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void convertDocToMalletCRFFeed(String infilename, String outfilename)
			throws NoDictionaryException, FileNotFoundException {

		if (dictionary == null)
			throw new NoDictionaryException("");

		File in = new File(infilename);
		File out = new File(outfilename);

		try (Scanner scanner = new Scanner(in);
				BufferedWriter br = new BufferedWriter(new FileWriter(out))) {

			if (!in.exists())
				throw new FileNotFoundException();

			while (scanner.hasNextLine()) {
				String line[] = scanner.nextLine().split("\\s");
				for (String token : line) {
					if (!token.equals("")) {
						token = token
								+ " "
								+ getFromLexiconAsString(token.replaceAll(
										"\\s", ""));
						token = token.trim();
						// replace omits exception because of % in string
						String output = String.format(token.replace("%", "%%")
								+ "%n");
						// %n system independent line end
						// http://stackoverflow.com/questions/207947/java-how-do-i-get-a-platform-independent-new-line-character

						br.write(output);
					}
				}

				br.write(String.format("%n"));
			}
		} catch (FileNotFoundException e) {
			log.throwing("PreTaggerByLexicon", "convertDocToMalletCRFFeed", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * Reads all files in a folder. As a result a dictionary is created which
	 * assigns string to a class. The filenames should ne in the following
	 * formatting:
	 * </p>
	 * <p>
	 * <i>somearbitrarystring classification.txt</i>
	 * </p>
	 * <p>
	 * e.g.: "german_names name1.txt"
	 * </p>
	 * 
	 * @param path
	 *            Path where dictionary files are stored
	 */
	private void readDictFromFolder(String path) {
		File dirs = new File(path);

		if (dirs.exists()) {
			File[] files = dirs.listFiles();
			for (File f : files) {
				log.info("Reading " + f.getAbsolutePath());
				BufferedReader br = null;
				try {
					// read second string from filename as filenames must be in
					// format e.g. "german company somemoreinfo.txt"
					String type = (String) f.getName().replaceAll(
							"^\\w* (\\w*).*$", "$1");
					log.finer("Classifying by '" + type + "'.");
					br = new BufferedReader(new InputStreamReader(
							new FileInputStream(f), "UTF8"));
					String line = "";
					while ((line = br.readLine()) != null) {
						line = thesaurus.use(new Utf8(line));
						int prior_type = 0;
						if (dictionary.get(line) != null)
							prior_type = dictionary.get(line);
						int newtype = (Classification.getAsInt(type) | prior_type);
						// if (line.equals("of"))
						// System.out.println(line + "    type   " +
						// type+", old: "+Classification.getAsString(prior_type)+" now "+Classification.getAsString(newtype));
						log.finer("putting " + line + " (" + newtype
								+ "), now: "
								+ Classification.getAsString(newtype));
						dictionary.put(line, newtype);

					}

				} catch (FileNotFoundException e) {
					log.throwing(EntityClassifyer.class.getName(),
							"PreTaggerByLexicon", e);
				} catch (UnsupportedEncodingException e) {
					log.throwing(EntityClassifyer.class.getName(),
							"PreTaggerByLexicon", e);
				} catch (IOException e) {
					log.throwing(EntityClassifyer.class.getName(),
							"PreTaggerByLexicon", e);
				} finally {
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private CompanyThesaurus thesaurus = new CompanyThesaurus();

	public int getFromLexiconAsInt(String name) throws NoDictionaryException {
		name = thesaurus.use(new Utf8(name));
		if (dictionary == null)
			throw new NoDictionaryException("");
		return dictionary.get(name);
	}

	public String getFromLexiconAsString(String name)
			throws NoDictionaryException {

		name = thesaurus.use(new Utf8(name));
		if (dictionary == null)
			throw new NoDictionaryException("");
		if (dictionary.get(name) == null)
			return "";
		return Classification.getAsString(dictionary.get(thesaurus
				.use(new Utf8(name))));
	}

	public PreTaggerByLexicon() {

		readDictFromFolder("data/classification/");

		// addToDictionaryFromFile(path + "name1.txt",
		// PreTaggerByLexicon.NAME1);
		// addToDictionaryFromFile(path + "name2.txt",
		// PreTaggerByLexicon.NAME2);
		// addToDictionaryFromFile(path + "place.txt",
		// PreTaggerByLexicon.COUNTRY);// TODO
		// // place
		// // gibbet
		// // nich
		// addToDictionaryFromFile(path + "company.txt",
		// PreTaggerByLexicon.COMPANY);
		// addToDictionaryFromFile(path + "preposition.txt",
		// PreTaggerByLexicon.PREPOSITION);
	}

	/**
	 * Adds values to dictionary HashMAp, read from a single file
	 * 
	 * @param filename
	 *            dictionary file
	 * @param type
	 *            like "STATE", "COUNTRY", etc , see static constants above
	 */
	private void addToDictionaryFromFile(String filename, int type) {
		try (BufferedReader in = new BufferedReader(new FileReader(new File(
				filename)))) {
			String line = "";
			while ((line = in.readLine()) != null) {
				// if(line.equals("und"))
				// System.out.println("und type "+type);
				// if(filename.equals("preposition.txt"))

				// System.out.println(filename+ "   "+line+" \t+++ "+type);

				int prior_type = 0;

				if (dictionary.get(line) != null)
					prior_type = dictionary.get(line);
				int fuck = type | prior_type;
				log.fine("putting " + line + " (" + fuck + ")");
				dictionary.put(line.toLowerCase(), type | prior_type);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public class NoDictionaryException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NoDictionaryException() {
		}

		// Constructor that accepts a message
		public NoDictionaryException(String message) {
			super("The Dictionary is still empty. " + message);
		}

	}

}
