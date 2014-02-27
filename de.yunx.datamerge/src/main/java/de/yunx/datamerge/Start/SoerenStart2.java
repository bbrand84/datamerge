package de.yunx.datamerge.Start;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.yunx.datamerge.measures.dictionary.CompanyThesaurus;
import de.yunx.datamerge.measures.frequency.Patstat_411_Freq_IPLytics;
import de.yunx.datamerge.measures.frequency.Patstat_411_Insel;
import de.yunx.datamerge.measures.similarity.Similarity;
import de.yunx.datamerge.measures.similarity.WeightedNGramSim;


/*
 * database access optimization
 * http://www.precisejava.com/javaperf/j2ee/JDBC.htm#JDBC104
 */

public class SoerenStart2 {
	static Connection con = null;

	// ALTER TABLE `soeren700b_a` ADD COLUMN `SCORE_WEIGHTED_NGRAM` DOUBLE NULL
	// DEFAULT NULL

	// UPDATE control_group2_sqlmatch SET SCORE_WEIGHTED_NGRAM=NULL WHERE
	// SCORE_WEIGHTED_NGRAM is not NULL
	static void go(String table) {

		PreparedStatement ps2 = null;
		PreparedStatement ps1 = null;
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://130.149.207.137:3306/patstatApril2013",
					"root", "berlin030");


			WeightedNGramSim wngram = new WeightedNGramSim(con,
					new Patstat_411_Insel(con));
			wngram.setThesaurus(new CompanyThesaurus());
			wngram.setTreshold(0.5);
			//order by to prevent sql deadlock
			
			ps1 = con.prepareStatement("select FIRMA,PERSON_NAME,PERSON_ID from " + table
					+ " where SCORE_WEIGHTED_NGRAM IS NULL ORDER BY FIRMA,PERSON_NAME,PERSON_ID LIMIT 10000");
			ResultSet rs = ps1.executeQuery();
			SoerenStart2.print("read.");
			con.setAutoCommit(false);
			while (rs.next()) {
				// System.out.print(".");
				String firma = rs.getNString("FIRMA");
				String pn = rs.getNString("PERSON_NAME");
				int pid = rs.getInt("PERSON_ID");

				ps2 = con.prepareStatement("UPDATE " + table
						+ " SET SCORE_WEIGHTED_NGRAM="
						+ wngram.getSimilarity(firma, pn) + " WHERE PERSON_ID="
						+ pid + " AND FIRMA=\"" + firma + "\";");
				ps2.executeUpdate();
				
				// psc.query("INSERT INTO marisa_a (SCORE_NGRAM) VALUES ("+wngram.getSimilarity(ma,pn)+")");
				// count++;
				// if (wngram.isSimilar(ma, pn)){
				// System.out.print("#" + count + " : ");
				// sim(wngram, ma, pn);
				// SoerenStart2.print();
				// }

			}
			con.commit();
			SoerenStart2.print("written.");

		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			SoerenStart2.print("!!! " + e.getMessage());
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				if (ps2 != null) {
					ps2.close();
				}
				if (ps1 != null) {
					ps1.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SoerenStart2.print("Starting");
		try {
			

			for (int i = 0; i < (25000000/10000); i++) {
				double percent = i * 10000 / 25000000 *100;
				SoerenStart2.go(args[0]);
				SoerenStart2.print("done: " + percent + "%");
			}
			SoerenStart2.print("using table " + args[0]);

		} catch (ArrayIndexOutOfBoundsException e) {
			SoerenStart2.print("using table soeren700b_a as default. to change, provide table name as parameter");
			for (int i = 0; i < (25000000/10000); i++) {
				double percent = i * 10000 / 25000000 * 100;
				SoerenStart2.go("soeren700b_a");
				SoerenStart2.print("done: " + percent + "%");
			}
		} catch (OutOfMemoryError e) {
			SoerenStart2.print("HeapSpace full. "+ e.getMessage());
			e.printStackTrace();
		}
		
		SoerenStart2.print("     ______________________");
		SoerenStart2.print("    /\\                     \\");
		SoerenStart2.print("   /  \\    _________________\\");
		SoerenStart2.print("   \\   \\   \\                /");
		SoerenStart2.print("    \\   \\   \\__________    /");
		SoerenStart2.print("     \\   \\   \\    /   /   /");
		SoerenStart2.print("      \\   \\   \\  /   /   /");
		SoerenStart2.print("       \\   \\   \\/   /   /");
		SoerenStart2.print("        \\   \\  /   /   /");
		SoerenStart2.print("         \\   \\/   /   /");
		SoerenStart2.print("          \\      /   /");
		SoerenStart2.print("           \\    /   /");
		SoerenStart2.print("            \\  /   /");
		SoerenStart2.print("             \\/___/");
		SoerenStart2.print("");
		SoerenStart2.print("       BERECHNUNG FERTIG!!");
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	static void sim(Similarity sim, String s1, String s2) {
		SoerenStart2.print(s1 + "  -  " + s2);
		System.out.print(sim.isSimilar(s1, s2));
		SoerenStart2.print("  " + sim.getSimilarity(s1, s2));
	}

	static void print(String s) {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");

		System.out.println(ft.format(dNow) + ": " + s);

	}

}
