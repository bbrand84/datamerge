package de.yunx.datamerge.measures.frequency;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import au.com.bytecode.opencsv.CSVReader;
import de.yunx.datamerge.data.CSVData.InconsistentCSVFormatException;

public class Patstat_411_From_CSV implements FrequencyDistribution {

	private char _DELIMITER = '\t';
	private char _ENCLOSED = '"';
	public int size = 0;
	private CSVReader cr = null;
	private HashMap<String, int[]> hashmap;
	public boolean hasmap_sucessfully_created = false;
	private int default_val = 1;
	private int max_frequency = 0;

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// + LOGGING
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private static String classname = Patstat_411_From_CSV.class.getName();
	private static Level loglevel_console = Level.INFO;
	private static Level loglevel_file = Level.FINEST;
	static Logger log = Logger.getLogger(classname);
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

	

	public Patstat_411_From_CSV(File csv_file) throws FileNotFoundException,
			InconsistentCSVFormatException {
		readCSV(csv_file);
	}

	public Patstat_411_From_CSV() throws FileNotFoundException,
			InconsistentCSVFormatException {
		readCSV(new File("data/patstat_411_pers_dist.csv"));
	}

	private void readCSV(File csv_file) throws FileNotFoundException,
			InconsistentCSVFormatException {

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// + LOGGING
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
		log.setUseParentHandlers(false);
		ConsoleHandler consolehandler = new ConsoleHandler();
		FileHandler filehandler;
		try {
			filehandler = new FileHandler("log/log%g.xml", 8000, 4, false);// throws
																			// IOException,
			// SecurityEx
			consolehandler.setLevel(loglevel_console);
			filehandler.setLevel(loglevel_file);
			consolehandler.setFormatter(new SimpleFormatter());
			filehandler.setFormatter(new SimpleFormatter());
			log.addHandler(consolehandler);
			log.addHandler(filehandler);
			log.setLevel(loglevel_console);
		} catch (SecurityException e1) {
			log.throwing(classname, "classname", e1);
			e1.printStackTrace();
		} catch (IOException e1) {
			log.throwing(classname, "classname", e1);
			e1.printStackTrace();
		}
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

		log.info("Reading person distribution file: '"
				+ csv_file.getAbsolutePath() + "' (~"
				+ (csv_file.length() / 1024000) + "MB). This may take a while.");

		if (!csv_file.exists() || !csv_file.canRead() || !csv_file.isFile()) {

			throw new FileNotFoundException(
					"CSV File containing Person Distribution could not be found!");
		}



		hashmap = new HashMap<String, int[]>();

		try {
			BufferedReader in = null;

			in = new BufferedReader(new FileReader(csv_file));

			cr = new CSVReader(in, _DELIMITER, _ENCLOSED);
			String[] line = {};

			cr.readNext();// IOException
			List<String[]> list = cr.readAll();
			Iterator<String[]> it = list.iterator();

			while (it.hasNext()) {// rows
				int[] values = new int[2];
				line = it.next();
				log.finest(line[0] + "   " + line[1] + "   " + line[2]);
				values[0] = Integer.parseInt(line[1]);// frequency
				values[1] = Integer.parseInt(line[2]);// classification
				if (values[0] > max_frequency)
					max_frequency = values[0];
				hashmap.put(line[0].toLowerCase(), values);
			}
			hasmap_sucessfully_created = true;
			log.info("Sucessfully created in-memory distribution index.");
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) { // ???
			log.throwing(Patstat_411_From_CSV.class.getName(),
					"Patstat_411_From_CSV", e);
			hashmap = null;
			throw new InconsistentCSVFormatException(
					"Messy CSV file. Should be 'NAME tab frequency tab classes'.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * Returns -1 if index couldn't be created;
	 * 
	 */
	public double getFrequency(String token) {
		if (!hasmap_sucessfully_created)
			return -1;
		int[] result = hashmap.get(token.toLowerCase());

		if (result == null) {
			result = new int[2];
			result[0] = default_val;// 1
			result[1] = 0;// no classification
			log.finest("existiert net");
		}
		log.finest("FREQUENCY\t" + result[0]);
		log.finest("CLASS    \t" + result[1]);
		log.finest("MAX      \t" + max_frequency);
		return 1 - Math.log(result[0]) / Math.log(max_frequency);// normalization
	}

	/**
	 * Returns -1 if index couldn't be created;
	 * 
	 */
	public double getClassification(String token) {
		if (!hasmap_sucessfully_created)
			return -1;
		int[] result = hashmap.get(token.toLowerCase());
		if (result == null)
			if (result == null) {
				result = new int[2];
				result[0] = default_val;// 1
				result[1] = 0;// no classification
			}
		return result[1];

	}

	public static void main(String args[]) {

		try {
			Patstat_411_From_CSV p = new Patstat_411_From_CSV(new File(
					"data/patstat_411_pers_dist.csv"));
			log.finest(String.valueOf(p.getFrequency("SIEMENS")));

			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.print("Enter String ('#END' to end.)");
			String s = "";
			while (s != "#END") {
				s = br.readLine();
				log.finest(String.valueOf(p.getFrequency(s)));
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InconsistentCSVFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
