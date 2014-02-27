package de.yunx.datamerge.Start;

import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.avro.util.Utf8;

import de.yunx.datamerge.data.CSVData.InconsistentCSVFormatException;
import de.yunx.datamerge.measures.dictionary.CompanyThesaurus;
import de.yunx.datamerge.measures.frequency.Patstat_411_Freq_IPLytics;
import de.yunx.datamerge.measures.frequency.Patstat_411_From_CSV;
import de.yunx.datamerge.measures.similarity.WeightedNGramSim;

public class IPLyticsStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("go");

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://192.168.53.179:3306/patstat413", "patstat",
					"#p8st8!");

			// WeightedNGramSim wngram = new WeightedNGramSim(con, new
			// Patstat_411_Freq_IPLytics(con));
			WeightedNGramSim wngram= new WeightedNGramSim(con, new Patstat_411_From_CSV());

			wngram.setThesaurus(new CompanyThesaurus());
			wngram.setTreshold(0.5);

			wngram.getSimilarity("KArtofffelsalat", "Krtofffelsalt");
			wngram.getSimilarity("Siemens", "Simens GmbH");
			wngram.getSimilarity("WESTAFLEX",
					"WESTAFLEX G.M.B.H., FABRIK VOLL-FLEXIBLER ROHRE");
			wngram.getSimilarity("NOKIA CORPORATION", "NOKIA CORPORATION");
			wngram.getSimilarity("NOKIA CORPORATION", "NOKIA CORPORATION");
			wngram.getSimilarity("NOKIA CORPORATION", "NOKIA CORPORATION");
			wngram.getSimilarity(
					"HAERZSCHEL, RUDOLF, DR., W-8263 BURGHAUSEN, DE",
					"WACKER-CHEMITRONIC GESELLSCHAFT FUER ELEKTRONIK-GRUNDSTOFFE MBH, 8263 BURGHAUSEN");
			wngram.getSimilarity(
					"INTERMETALL GESELLSCHAFT FUER METALLURGIE UND ELEKTRONIK M.B.H.",
					"WACKER-CHEMITRONIC GESELLSCHAFT FUER ELEKTRONIK-GRUNDSTOFFE MBH, 8263 BURGHAUSEN");

			wngram.getSimilarity(
					"SOLARZELLEN-GRUNDSTOFFE",
					"WACKER-CHEMITRONIC GESELLSCHAFT FUER ELEKTRONIK-GRUNDSTOFFE MBH, 8263 BURGHAUSEN");
			wngram.getSimilarity(
					"SOLARZELLEN GRUNDSTOFFE",
					"WACKER-CHEMITRONIC GESELLSCHAFT FUER ELEKTRONIK GRUNDSTOFFE MBH, 8263 BURGHAUSEN");

			// System.out.println("     ______________________");
			// System.out.println("    /\\                     \\");
			// System.out.println("   /  \\    _________________\\");
			// System.out.println("   \\   \\   \\                /");
			// System.out.println("    \\   \\   \\__________    /");
			// System.out.println("     \\   \\   \\    /   /   /");
			// System.out.println("      \\   \\   \\  /   /   /");
			// System.out.println("       \\   \\   \\/   /   /");
			// System.out.println("        \\   \\  /   /   /");
			// System.out.println("         \\   \\/   /   /");
			// System.out.println("          \\      /   /");
			// System.out.println("           \\    /   /");
			// System.out.println("            \\  /   /");
			// System.out.println("             \\/___/");

			// System.out.println(Charset.defaultCharset().toString());
			//
			// Utf8 text = new
			// Utf8("Siemens Gesellschaft bürgerlichen rechts g.m.b.h. sem$$el limited Liability Partnership Akcionarsko dru4vo");
			// CompanyThesaurus ct = new CompanyThesaurus();
			// System.out.println(ct.use(text));
			wngram = new WeightedNGramSim(con, new Patstat_411_From_CSV());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InconsistentCSVFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
