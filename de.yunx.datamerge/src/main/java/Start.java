import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.yunx.datamerge.measures.Patstat_411_IDF;
import de.yunx.datamerge.measures.dictionary.CompanyThesaurus;
import de.yunx.datamerge.measures.frequency.AircraftCompanies_IDF;
import de.yunx.datamerge.measures.similarity.Ngram;
import de.yunx.datamerge.measures.similarity.Similarity;
import de.yunx.datamerge.measures.similarity.WeightedNGramSim;
import de.yunx.datamerge.mySQL.PatStatConnect;

public class Start {
	static Connection con = null;
	static void go() {

		// ALTER TABLE `marisa_a`
		// ADD COLUMN `SCORE_NGRAM` DOUBLE NULL DEFAULT NULL AFTER `SCORE`;


		


		int count = 0;
		PatStatConnect psc = new PatStatConnect("192.168.53.179",
				"patstatApril2013", "patstat", "#");
		ResultSet rs = psc
				.query("select * from marisax_a where SCORE_NGRAM2 IS NULL");

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://130.149.207.137:3306/patstatApril2013",
					"root", "berlin030");
			
			WeightedNGramSim wngram = new WeightedNGramSim(con);
			wngram.setThesaurus(new CompanyThesaurus());
			wngram.setFrequencyDistribution(new Patstat_411_IDF(con));
			wngram.setTreshold(0.5);
			
			while (rs.next()) {

				String ma = rs.getNString("MATCHED_AGAINST");
				String pn = rs.getNString("PERSON_NAME");
				int pid = rs.getInt("PERSON_ID");

				PreparedStatement statement = con
						.prepareStatement("UPDATE marisax_a SET SCORE_NGRAM2="
								+ wngram.getSimilarity(ma, pn)
								+ " WHERE PERSON_ID=" + pid
								+ " AND MATCHED_AGAINST=\"" + ma + "\";");
				statement.executeUpdate();

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

		Start.sim(new WeightedNGramSim(con), "Fuck", "bla");
		
		// Start.go();

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
		
		

		
	}

	static void sim(Similarity sim, String s1, String s2) {
		System.out.println(s1 + "  -  " + s2);
		System.out.print(sim.isSimilar(s1, s2));
		System.out.println("  " + sim.getSimilarity(s1, s2));
	}

}
