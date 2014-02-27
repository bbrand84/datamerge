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

public class SarasStart {
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
					"jdbc:mysql://192.168.53.179:3306/patstat413", "patstat",
					"#p8st8!");

			WeightedNGramSim wngram = new WeightedNGramSim(con,
					new Patstat_411_Freq_IPLytics(con));
			wngram.setThesaurus(new CompanyThesaurus());
			wngram.setTreshold(0.5);
			//order by to prevent sql deadlock
			
			ps1 = con.prepareStatement("select PERSON_NAME, PERSON_NAME2, PERSON_ID, PERSON_ID2 from " + table
					+ " where SCORE_WEIGHTED_NGRAM IS NULL ORDER BY PERSON_ID,PERSON_ID2 LIMIT 10000");
			ResultSet rs = ps1.executeQuery();
			SarasStart.print("read.");
			con.setAutoCommit(false);
			while (rs.next()) {
				// System.out.print(".");
				String pn = rs.getNString("PERSON_NAME");
				String pn2 = rs.getNString("PERSON_NAME2");
				int pid = rs.getInt("PERSON_ID");
				int pid2 = rs.getInt("PERSON_ID2");

				ps2 = con.prepareStatement("UPDATE " + table
						+ " SET SCORE_WEIGHTED_NGRAM="
						+ wngram.getSimilarity(pn, pn2) + " WHERE PERSON_ID="
						+ pid + " AND PERSON_ID2=\"" + pid2 + "\";");
				ps2.executeUpdate();
				
				// psc.query("INSERT INTO marisa_a (SCORE_NGRAM) VALUES ("+wngram.getSimilarity(ma,pn)+")");
				// count++;
				// if (wngram.isSimilar(ma, pn)){
				// System.out.print("#" + count + " : ");
				// sim(wngram, ma, pn);
				// SarasStart.print();
				// }

			}
			con.commit();
			SarasStart.print("written.");

		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			SarasStart.print("!!! " + e.getMessage());
			//e.printStackTrace();
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
		SarasStart.print("Starting");
		try {
			

			for (int i = 0; i < (12063095/10000); i++) {
				double percent = i * 10000 / 12063095 *100;
				SarasStart.go(args[0]);
				SarasStart.print("done: " + percent + "%");
			}
			SarasStart.print("using table " + args[0]);

		} catch (ArrayIndexOutOfBoundsException e) {
			SarasStart.print("using table control_group2_sqlmatch_single as default. to change, provide table name as parameter");
			for (int i = 0; i < (12063095/10000); i++) {
				double percent = i * 10000 / 12063095 * 100;
				SarasStart.go("control_group2_sqlmatch_single");
				SarasStart.print("done: " + percent + "%");
			}
		} catch (OutOfMemoryError e) {
			SarasStart.print("HeapSpace full. "+ e.getMessage());
			e.printStackTrace();
		}
		
		SarasStart.print("     ______________________");
		SarasStart.print("    /\\                     \\");
		SarasStart.print("   /  \\    _________________\\");
		SarasStart.print("   \\   \\   \\                /");
		SarasStart.print("    \\   \\   \\__________    /");
		SarasStart.print("     \\   \\   \\    /   /   /");
		SarasStart.print("      \\   \\   \\  /   /   /");
		SarasStart.print("       \\   \\   \\/   /   /");
		SarasStart.print("        \\   \\  /   /   /");
		SarasStart.print("         \\   \\/   /   /");
		SarasStart.print("          \\      /   /");
		SarasStart.print("           \\    /   /");
		SarasStart.print("            \\  /   /");
		SarasStart.print("             \\/___/");
		SarasStart.print("");
		SarasStart.print("       BERECHNUNG FERTIG!!");
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	static void sim(Similarity sim, String s1, String s2) {
		SarasStart.print(s1 + "  -  " + s2);
		System.out.print(sim.isSimilar(s1, s2));
		SarasStart.print("  " + sim.getSimilarity(s1, s2));
	}

	static void print(String s) {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");

		System.out.println(ft.format(dNow) + ": " + s);

	}

}
