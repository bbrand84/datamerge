package de.yunx.datamerge.initialization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyOfPatstatDistributionCreator {

	Hashtable<String, Integer> ht = new Hashtable<String, Integer>();

	public CopyOfPatstatDistributionCreator(Connection con) {

		Logger logger = LoggerFactory
				.getLogger(CopyOfPatstatDistributionCreator.class);
		logger.info("Hello World");

		PreparedStatement statement;
		try {

			statement = con
					.prepareStatement("DROP TABLE IF EXISTS TLS206_PERSON_DISTRIBUTION");
			statement.executeUpdate();
			statement.close();

			statement = con
					.prepareStatement("CREATE TABLE IF NOT EXISTS `TLS206_PERSON_DISTRIBUTION` (`TOKEN` VARCHAR(300) NULL DEFAULT NULL,`NUM` INT(11) NULL DEFAULT NULL,FULLTEXT INDEX `tokenx` (`TOKEN`), UNIQUE INDEX `TOKEN` (`TOKEN`)) COLLATE='utf8_general_ci' ENGINE=MyISAM;");
			statement.executeUpdate();
			statement.close();

			Statement stmt = con.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT count(*) as num FROM TLS206_PERSON");
			rs.first();
			int num = rs.getInt("num");
			logger.info(num + " werte in tabelle");
			System.out.println(num + " werte in tabelle");
			rs.close();
			stmt.close();

			int step = 50000;

			for (int i = 0; i <= num; i = i + step) {
				Statement stmt2 = con.createStatement();
				System.out.print("lese ab " + i);
				ResultSet rs2 = stmt2
						.executeQuery("SELECT * FROM TLS206_PERSON LIMIT "
								+ step + " OFFSET " + i);
				System.out.print(" ... fertig. Schreibe ... ");
				if (!rs2.first())
					System.out.println("Konnte nich lesen");
				StringBuffer last_token = new StringBuffer("");
				do {
					String clean = rs2.getNString("PERSON_NAME");

					Scanner sc = new Scanner(clean);
					sc.useDelimiter("[^\\w]");
					while (sc.hasNext()) {
						String x = sc.next().toUpperCase();
						if (x.trim().length() > 1) {
							// PreparedStatement stmt3 = con
							// .prepareStatement("INSERT INTO TLS206_PERSON_DISTRIBUTION (token, num) VALUES ('"
							// + x
							// + "', 1) ON DUPLICATE KEY UPDATE num=num+1;");
							// stmt3.executeUpdate();
							// stmt3.close();

							if (!ht.containsKey(x)) {
								ht.put(x, 1);

							} else {
								int c = (Integer) ht.get(x);
								c++;
								ht.put(x, c);
								// System.out.println(x + "\t" + ht.get(x));
							}

						}
						x = null;

					}

					sc.close();
					sc = null;
					rs2.next();
				} while (rs2.next());

				rs2.close();

				Iterator<Map.Entry<String, Integer>> it = ht.entrySet()
						.iterator();

				while (it.hasNext()) {
					Map.Entry<String, Integer> entry = it.next();
					PreparedStatement stmt3 = con
							.prepareStatement("INSERT INTO TLS206_PERSON_DISTRIBUTION (token, num) VALUES ('"
									+ entry.getKey()
									+ "', "
									+ entry.getValue()
									+ ") ON DUPLICATE KEY UPDATE num=num+"
									+ entry.getValue() + ";");
					stmt3.executeUpdate();
					stmt3.close();
				}

				ht.clear();
				System.out.println("fertig.");
			}

			System.out.println("ENDE.");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
