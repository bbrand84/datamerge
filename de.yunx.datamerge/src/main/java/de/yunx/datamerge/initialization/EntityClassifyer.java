package de.yunx.datamerge.initialization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


//ALTER TABLE `TLS206_PERSON_DISTRIBUTION_copy` ADD COLUMN `CLASS` INT(11) NULL AFTER `NUM`;

public class EntityClassifyer {
	
	static Level loglevel = Level.INFO;
	static Logger log = Logger.getLogger(EntityClassifyer.class.getName());
	static ConsoleHandler consolehandler = new ConsoleHandler();

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

	public EntityClassifyer(Connection con) {

		log.setUseParentHandlers(false);
		log.addHandler(consolehandler);
		log.setLevel(loglevel);


		PreparedStatement statement;
		try {
			log.info("Modyfying table 'TLS206_PERSON_DISTRIBUTION': Adding row 'class'");
			statement = con
					.prepareStatement("ALTER TABLE `TLS206_PERSON_DISTRIBUTION` ADD COLUMN `CLASS` INT(11) DEFAULT '0' AFTER `NUM`");

			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			log.throwing(EntityClassifyer.class.getName(), "EntityClassifyer", e);
			try {
				log.info("Modyfying table 'TLS206_PERSON_DISTRIBUTION': Resetting row 'class'.");
				statement = con
						.prepareStatement("UPDATE `TLS206_PERSON_DISTRIBUTION` SET `CLASS` = 0");

				statement.executeUpdate();
				statement.close();
			} catch (SQLException e2) {

			}
		}

		String classifications_dir = "data/classification";

		File dirs = new File(classifications_dir);

		if (dirs.exists()) {
			File[] files = dirs.listFiles();
			for (File f : files) {
				log.info("Reading " + f.getAbsolutePath());
				try {
					// read second string from filename as filenames must be in
					// format e.g. "german company somemoreinfo.txt"
					String classification = (String) f.getName().replaceAll(
							"^\\w* (\\w*).*$", "$1");
					log.info("Classifying by '" + classification + "'.");
					BufferedReader br = new BufferedReader(
							new InputStreamReader(new FileInputStream(f),
									"UTF8"));

					int classification_int = 0;
					switch (classification.toLowerCase()) {
					case "legal":
						classification_int = LEGAL;
						break;
					case "company":
						classification_int = COMPANY;
						break;
					case "name1":
						classification_int = NAME1;
						break;
					case "name2":
						classification_int = NAME2;
						break;
					case "preposition":
						classification_int = PREPOSITION;
						break;
					case "city":
						classification_int = CITY;
						break;
					case "street":
						classification_int = STREET;
						break;
					case "state":
						classification_int = STATE;
						break;
					case "country":
						classification_int = COUNTRY;
						break;
					case "specification":
						classification_int = SPECIFICATION;
						break;
					case "title":
						classification_int = TITLE;
						break;
					}

					if (classification_int == 0)
						log.warning("Classifcation term '" + classification
								+ "' in file '" + f.getAbsoluteFile()
								+ "' is not valid.");
					else {
						String r = "";
						while ((r = br.readLine()) != null) {
							// System.out.println(r);
							// ^ = BIT OR

							String[] r_a = r.split(" ");
							for (String rr : r_a) {
								String query = "UPDATE `TLS206_PERSON_DISTRIBUTION` SET `CLASS`=`CLASS` ^ "
										+ classification_int
										+ " WHERE `TOKEN` like \""
										+ rr.trim() + "\"";
								// if(f.getName().contains("unisex"))
								// System.out.println(query);
								statement = con.prepareStatement(query);
								statement.executeUpdate();
								statement.close();
							}
						}
					}
					br.close();
				} catch (FileNotFoundException e) {
					log.throwing(EntityClassifyer.class.getName(), "EntityClassifyer", e);
				} catch (IOException e) {
					log.throwing(EntityClassifyer.class.getName(), "EntityClassifyer", e);
				} catch (SQLException e) {
					log.throwing(EntityClassifyer.class.getName(), "EntityClassifyer", e);
				}
			}
		} else
			log.severe("Directory " + dirs.getAbsolutePath()
					+ " does not exists!!");

		// FileReader fr = new FileReader("test.txt");
		// BufferedReader br = new BufferedReader(fr);
		//
		// String zeile1 = br.readLine();
		// System.out.println(zeile1);

	}
}
