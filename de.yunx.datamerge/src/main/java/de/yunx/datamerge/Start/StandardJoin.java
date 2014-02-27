package de.yunx.datamerge.Start;

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
import java.util.logging.LogManager;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVReader;
import de.yunx.datamerge.measures.similarity.StandardsSim3;

public class StandardJoin {

	LogManager lm = LogManager.getLogManager();
	static Level loglevel = Level.FINEST;

	static Logger log = Logger.getLogger(StandardJoin.class.getName());
	static ConsoleHandler consolehandler = new ConsoleHandler();

	static String outfile = "itu_perinorm_matched.csv";
	static String infile1 = "perinorm.txt";
	static String infile2 = "itu.txt";
	
	static String path = "C:\\Users\\BB\\Dropbox\\Diplomarbeit\\standards matching\\";
	static String results_path = path + "results/";

	public StandardJoin() {
		// try (BufferedInputStream bis = new BufferedInputStream(
		// new FileInputStream(new File("logging.conf")))) {
		// lm.readConfiguration(bis);
		// } catch (SecurityException | IOException e) {
		// log.throwing(StandardJoin.class.getName(), "StandardJoin", e);
		// }
	}

	public static void main(String[] args) {
		// String path = "C:\\Users\\BB\\tim\\";

		log.setUseParentHandlers(false);
		consolehandler.setLevel(loglevel);
		log.addHandler(consolehandler);
		log.setLevel(loglevel);
		log.info("Starting");

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

			StandardsSim3 stdsim = new StandardsSim3();

			File f = new File(results_path + outfile);
			log.info("Writing file '" + results_path + outfile + "'");
			PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(
					f, true)));
			pr.println("name1_original\tname2_original\tname1\tname2\tagency\tnorm\tnumber\tversion\tsubversion1\tsubversion2\tsubversion3");

			int counter = 0;
			while (it1.hasNext()) {

				counter++;
				String s1 = it1.next()[0];
				if (counter % 200 == 0)
					log.info("#" + counter + ": Processing status: "
							+ ((float)counter / (float)list1.size()) + "%");
				it2 = list2.iterator();
				while (it2.hasNext()) {
					String s2 = it2.next()[0];
					String[] splitted1 = s1.split("\\*");
					String[] splitted2 = s2.split("\\*");
					for (String s1_single : splitted1)
						for (String s2_single : splitted2) {
							int[] simi = stdsim.getSimilarityArray(s1_single,
									s2_single);

							if (simi[2] + simi[1] > 1) {
								
								if(simi[4]==1 && simi[5]==1)
									simi[5]=1;
								else
									simi[5]=0;
								
								if(simi[5]==1 && simi[6]==1)
									simi[6]=1;
								else
									simi[6]=0;
								
								pr.println(s1 + "\t" + s2 + "\t" + s1_single
										+ "\t" + s2_single + "\t" + simi[0]
										+ "\t" + simi[1] + "\t" + simi[2]
										+ "\t" + simi[3] + "\t" + simi[4]
										+ "\t" + simi[5] + "\t" + simi[6]);

								log.finer(s1_single + "\t" + s2_single + "\t"
										+ simi[0] + "\t" + simi[1] + "\t"
										+ simi[2] + "\t" + simi[3] + "\t"
										+ simi[4] + "\t" + simi[5] + "\t"
										+ simi[6]);
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
