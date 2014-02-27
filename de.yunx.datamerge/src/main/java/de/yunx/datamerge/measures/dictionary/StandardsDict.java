package de.yunx.datamerge.measures.dictionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.avro.util.Utf8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class StandardsDict implements Dictionary {

	Logger logger = LoggerFactory
			.getLogger(StandardsDict.class);
	
	final static String FILE_NAME = "data/StandardsDict.dict";
	final static String OUTPUT_FILE_NAME = "C:\\Temp\\output.txt";
	final static Charset ENCODING = StandardCharsets.UTF_8;
	final static char DELIMITER = ';';
	final static char ENCLOSED = '\"';
	final static Path path = Paths.get(FILE_NAME);

	/*
	 * synomym_hm: all strings are identified by a same int as refering to the
	 * same entity classif_hm: all strings are classified as read from the first
	 * field in the classification csv file
	 */
	HashMap<String, Integer> synomym_hm = new HashMap();
	HashMap<String, Integer> classif_hm = new HashMap();

	// FORMAT: CSV: class;synonym1;synonym2;...

	public StandardsDict() {

		try {
			BufferedReader in = Files.newBufferedReader(path, ENCODING);
			CSVReader cr = new CSVReader(in, DELIMITER, ENCLOSED);
			String[] line = {};

			List<String[]> list = cr.readAll();
			Iterator<String[]> it = list.iterator();

			int classification = 0;
			int count = 0;
			while (it.hasNext()) {
				line = it.next();
				for (int i = 0; i < line.length; i++)
					if (i == 0)
						classification = Integer.parseInt(line[0]);
					else {
						synomym_hm.put(line[i], count);
						classif_hm.put(line[i], classification);
					}
				count++;
			}
		} catch (NumberFormatException e) {
			logger.error("The File "+FILE_NAME+" is not in a valid format.\nPlease make sure it is in CSV Format like this example class;synonym1;synonym2;...\nThe first field must be an Integer, the others Strings. UTF-8 Encoding. ");
//			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getClassification(Utf8 token) {
		return classif_hm.get(token.toString());
	}

	@Override
	public void addToDictionary(String token, int classification) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_NAME, true)));
			//BufferedWriter writer = Files.newBufferedWriter(path, ENCODING);
			String classi_str = new Integer(classification).toString();
			out.println(classi_str+";"+token);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
