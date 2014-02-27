import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.yunx.datamerge.measures.dictionary.CompanyThesaurus;
import de.yunx.datamerge.measures.frequency.Patstat_411_Insel;
import de.yunx.datamerge.measures.similarity.Similarity;
import de.yunx.datamerge.measures.similarity.WeightedNGramSim;

public class SoerenStart {
	static Connection con = null;

	// ALTER TABLE `soeren700b_a` ADD COLUMN `SCORE_WEIGHTED_NGRAM` DOUBLE NULL
	// DEFAULT NULL
	static void go(String table) {

		try {

			System.out.println("go");
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://130.149.207.137:3306/patstatApril2013",
					"root", "berlin030");

			WeightedNGramSim wngram = new WeightedNGramSim(con,
					new Patstat_411_Insel(con));
			wngram.setThesaurus(new CompanyThesaurus());
			wngram.setTreshold(0.5);
			PreparedStatement ps1 = con
					.prepareStatement("select * from " + table
							+ " where SCORE_WEIGHTED_NGRAM IS NULL LIMIT 50000");
			ResultSet rs = ps1.executeQuery();

			while (rs.next()) {
				// System.out.print(".");
				String firma = rs.getNString("FIRMA");
				String pn = rs.getNString("PERSON_NAME");
				int pid = rs.getInt("PERSON_ID");

				PreparedStatement ps2 = con.prepareStatement("UPDATE " + table
						+ " SET SCORE_WEIGHTED_NGRAM="
						+ wngram.getSimilarity(firma, pn) + " WHERE PERSON_ID="
						+ pid + " AND FIRMA=\"" + firma + "\";");
				ps2.executeUpdate();

				// psc.query("INSERT INTO marisa_a (SCORE_NGRAM) VALUES ("+wngram.getSimilarity(ma,pn)+")");
				// count++;
				// if (wngram.isSimilar(ma, pn)){
				// System.out.print("#" + count + " : ");
				// sim(wngram, ma, pn);
				// System.out.println();
				// }

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			for (int i = 0; i < 2500; i++) {
				System.out.println("using table " + args[0]);
				SoerenStart.go(args[0]);
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out
					.println("using table soeren700b_a as default. to change, provide table name as parameter");
			for (int i = 0; i < 2500; i++) {

				SoerenStart.go("soeren700b_a");
			}
		}
		System.out.println("BERECHNUNG FERTIG!!");

		// GUI gui = new GUI();
		/*
		 * CSVData c = new CSVData(';', '\"');
		 * 
		 * try { c.readCSVFile(new File("F:/test.csv")); c.readCSVFile(new
		 * File("F:/Hopp_suche.csv")); c.readCSVFile(new File(
		 * "Z:/bb/2013 06 25 Marisa/Aircraft Manufacturers_bearbeitet (Version 1).csv"
		 * )); } catch (InconsistentCSVFormatException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		// PatStatConnect pat = new PatStatConnect();
		/*
		 * Ngram ngram = new Ngram();
		 * System.out.println(ngram.getSimilarity("Applemania", "corporation"));
		 * System.out.println(ngram.getSimilarity("aticorpor", "corporation"));
		 * System.out.println(ngram.getSimilarity("Kartoffel", "Kartoffel"));
		 * System.out.println(ngram.getSimilarity("Siemes", "Siemens"));
		 */
		// WeightedNGramSim wngram = new WeightedNGramSim(con);
		// wngram.setThesaurus(new CompanyThesaurus());
		// wngram.setFrequencyDistribution(new AircraftCompanies_IDF());
		// System.out.println(wngram.removeDouble("reims-blubb reims bla"));
		/*
		 * System.out.println("TADAAAA   "+wngram.getSimilarity("Siemens ag corp"
		 * , "siemes ag corp"));
		 * System.out.println("TADAAAA   "+wngram.getSimilarity("Siemens AG",
		 * "Siemens Aktiengesellschaft"));
		 * System.out.println("TADAAAA   "+wngram
		 * .getSimilarity("Applemania Corp.", "Applemania corporation"));
		 * System.
		 * out.println("TADAAAA   "+wngram.getSimilarity("alpha industries",
		 * "alpha industries"));
		 * System.out.println("TADAAAA   "+wngram.getSimilarity
		 * ("sdafsdsadfsadfdsaf", "jkgjkjgkg"));
		 * System.out.println("TADAAAA   "+
		 * wngram.getSimilarity("sdafsdsadfsadfdsaf", "sdafsdsadfsadfdsaf"));
		 * System.out.println("TADAAAA   "+wngram.getSimilarity("sdafsdsadf",
		 * "sdafsdsadfsadfdsaf"));
		 * System.out.println("TADAAAA   "+wngram.getSimilarity
		 * ("sdafsdsadfsadfdsaf", "sdafsdsadf"));
		 * System.out.println("TADAAAA   "+wngram.getSimilarity("fafafafafa",
		 * "fafafafafa"));
		 * System.out.println("TADAAAA   "+wngram.getSimilarity("f��i� Incorporated"
		 * ,"kansd����"));
		 */

		// Start.sim(wngram,"Siemens AG", "Siemens Aktiengesellschaft");
		// Start.sim(wngram,"Siemens AG", "Albert Siemes Aktiengesellschaft");
		// Start.sim(wngram,"Siemens AG", "Xian Wuia Ling");
		// Start.sim(wngram,"Siemens AG", "Serimon AG");
		// Start.sim(wngram,"Bosch AG", "Robert Bosch AG");
		// Start.sim(wngram,"AG Bosch", "Bosch Robert AG");
		// Start.sim(wngram,"ROCKWELL COMMANDER",
		// "ROCKWELL AUTOMATION ZWEIGNIEDERLASSUNG DER ROCKWELL INTERNATIONAL GMBH");
		// SoerenStart.sim(wngram, "Apex Aircraft",
		// "APEX VALVES LIMITED (FORMERLY APEX ENGINEERING LTD)");
		// SoerenStart.sim(wngram, "NORTH AMERICAN ROCKWELL CORP.",
		// "NORTH AMERICAN ROCKWEEL CORP");
		// SoerenStart.sim(wngram, "AERONCA AIRCRAFT CORPORATION",
		// "ROGERSON AIRCRAFT CORPORATION");
		// SoerenStart.sim(wngram, "AERONCA CHAMPION",
		// "Champion Brass Manufacturing dba Champion Irrigation Products");
		// SoerenStart.sim(wngram, "RAYTHEON AIRCRAFT COMPANY",
		// "LINDER, Lloyd F. c/o Raytheon Company");
		// SoerenStart.sim(
		// wngram,
		// "RAYTHEON AIRCRAFT COMPANY",
		// "SHANGHAI AIRCRAFT DESIGN & RESEARCH INSTITUTE OF COMMERCIAL AIRCRAFT CORPORATION OF CHINA LTD.");
		//
		// SoerenStart.sim(wngram, "AERO VODOCHODY AERO. WORKS",
		// "AERO VODOCHODY AS");

		// Ngram ng = new Ngram();
		// System.out.println(ng.getSimilarity("model", "vodochody"));

		// ALTER TABLE `marisa_a`
		// ADD COLUMN `SCORE_NGRAM` DOUBLE NULL DEFAULT NULL AFTER `SCORE`;

		// PatStatConnect psc = new PatStatConnect();

		// Patstat_411_IDF idf = new Patstat_411_IDF();
		//
		// System.out.println("idf  "+idf.getFrequency("gmbh"));
		// System.out.println("idf  "+idf.getFrequency("Inc"));
		// System.out.println("idf  "+idf.getFrequency("Siemens"));
		// System.out.println("idf  "+idf.getFrequency("Siemes"));

		/*
		 * ResultSet rs = psc.query("SELECT * FROM tls206_person limit 666");
		 * try { while (rs.next() != false) {
		 * System.out.print(rs.getString(1).trim()+"\t");
		 * System.out.print(rs.getString(4).trim().replaceAll("[^\\w]",
		 * "")+"\t");
		 * System.out.println(rs.getString(5).trim().replaceAll("[^\\w ]", ""));
		 * } } catch (SQLException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// idf.close();
		// psc.close();
	}

	static void sim(Similarity sim, String s1, String s2) {
		System.out.println(s1 + "  -  " + s2);
		System.out.print(sim.isSimilar(s1, s2));
		System.out.println("  " + sim.getSimilarity(s1, s2));
	}

}
