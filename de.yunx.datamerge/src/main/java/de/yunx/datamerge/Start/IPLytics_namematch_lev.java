package de.yunx.datamerge.Start;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.yunx.datamerge.data.CSVData.CSVData;
import de.yunx.datamerge.data.CSVData.InconsistentCSVFormatException;
import de.yunx.datamerge.measures.frequency.Patstat_411_Freq_IPLytics;
import de.yunx.datamerge.measures.frequency.Patstat_411_From_CSV;
import de.yunx.datamerge.measures.similarity.Similarity;
import de.yunx.datamerge.measures.similarity.WeightedLevenshteinSim;
import de.yunx.datamerge.measures.similarity.WeightedNGramSim;

public class IPLytics_namematch_lev {

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// + ADJUST SETTINGS HERE
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	static double treshold = 0.5;
	//static String outfile = "results comp 0.7 new.csv";

	//static String infile1 = "cadre_comp_name_only.csv";
	static String infile1 = "cadre_subsidaries_name_only.csv";
	static String infile2 = "companies as to declarations.csv";
	static String outfile = "results "+infile1;
	static String path = "C:\\Users\\BB\\Dropbox\\Diplomarbeit\\standards matching\\company match\\";
	static String results_path = path;// + "results/";
	
	//infile1 num of row
	private static int fielda = 0;// 0=first row
	//infile2 num of row
	private static int fieldb = 0;

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// + SETTINGS END
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

	LogManager lm = LogManager.getLogManager();
	static Level loglevel = Level.FINEST;
	static Logger log = Logger
			.getLogger(IPLytics_namematch_lev.class.getName());
	static ConsoleHandler consolehandler = new ConsoleHandler();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		log.setUseParentHandlers(false);
		consolehandler.setLevel(loglevel);
		log.addHandler(consolehandler);
		log.setLevel(loglevel);
		log.info("Starting");

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://192.168.53.179:3306/patstat413", "patstat",
					"#p8st8!");
			// TODO Auto-generated catch block
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Similarity simi = new WeightedNGramSim(con, new
		// Patstat_411_Freq_IPLytics(con));
		// Similarity simi = new WeightedLevenshteinSim(con, new
		// Patstat_411_Freq_IPLytics(con));
		Similarity simi;
		try {
			simi = new WeightedLevenshteinSim(con, new Patstat_411_From_CSV());

			// int field1 = args[1];
			// int field1 = args[3];


			CSVData a = new CSVData(';', '\"', '\\');
			CSVData b = new CSVData(';', '\"', '\\');

			try {
				// Create file
				FileWriter fstream = new FileWriter(path + outfile);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write("similarity\t" + infile1 + "\t" + infile2);
				out.newLine();
				out.flush();

				try {
					a.readCSVFile(new File(path + infile1));
					b.readCSVFile(new File(path + infile2));
					a.getContent();

					for (int i = 1; i < a.getContent().length; i++) {
						for (int j = 1; j < b.getContent().length; j++) {
							double sim = simi.getSimilarity(
									a.getContent()[i][fielda],
									b.getContent()[j][fieldb]);
							// log.info("comparing:\t"+a.getContent()[i][fielda]
							// +"\t"+ b.getContent()[j][fieldb]);
							if (sim > treshold) {
								log.fine(sim + "\t" + a.getContent()[i][fielda]
										+ "\t" + b.getContent()[j][fieldb]);
								out.write(sim + "\t"
										+ a.getContent()[i][fielda] + "\t"
										+ b.getContent()[j][fieldb]);
								out.newLine();
								out.flush();
							}
						}
					}
					log.info("End.");

				} catch (InconsistentCSVFormatException e) { // TODO
					e.printStackTrace();
				}

				out.close();

			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InconsistentCSVFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
