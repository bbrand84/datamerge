package de.yunx.datamerge.data.CSVData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class CSVData {

	// TODO skip line, more data types, maybe type recognition in own class??

	public CSVData(char delimiter, char enclosed, char escape) {
		_DELIMITER = delimiter;
		_ENCLOSED = enclosed;
		_ESCAPE = escape;
	}

	private char _DELIMITER;
	private char _ENCLOSED;
	private char _ESCAPE;

	private String[][] data;

	public int size = 0;

	private CSVReader cr = null;

	public boolean readCSVFile(File csv_file)
			throws InconsistentCSVFormatException {

		if (!csv_file.exists() || !csv_file.canRead() || !csv_file.isFile())
			return false;

		try {
			BufferedReader in = null;

			in = new BufferedReader(new FileReader(csv_file));

			cr = new CSVReader(in, _DELIMITER, _ENCLOSED, _ESCAPE);
			String[] line = {};

			cr.readNext();
			int numrows = 0;
			int count = 0;

			List<String[]> list = cr.readAll();
			Iterator<String[]> it = list.iterator();

			while (it.hasNext()) {
				line = it.next();

				// System.out.println(count);
				if (count == 0) {
					numrows = line.length;
					size = list.size();
					data = new String[size][numrows];

					for (int i = 0; i < line.length; i++) {
						if (line[i].matches("^[0-9\\.\\,]+$")) {
							// System.out.print(line[i] + "> (num)   \t");
							data[0][i] = "<num>";
						} else if (line[i].trim().matches(
								"[a-zA-Z0-9_\\-\\.]+\\.[a-zA-Z]{2,4}")) {
							data[0][i] = "<url>";
						} else if (line[i]
								.trim()
								.matches(
										"[a-zA-Z0-9_\\-\\.]+@[a-zA-Z0-9_\\-\\.]+\\.[a-zA-Z]{2,4}")) {
							data[0][i] = "<mail>";
						}else {
							// System.out.print(line[i] + " >(text)   \t");
							data[0][i] = "<text>";
						}
					}
				} else {

					try {
						for (int i = 0; i < line.length; i++) {

								data[count][i] = line[i];

						}
					} catch (ArrayIndexOutOfBoundsException e) {
						// Exception noch irgendwie anpassen
						throw new InconsistentCSVFormatException();
					}
				}
				count++;
			}

			//System.out.println(Arrays.deepToString(data));

			// cr.close();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				cr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the content of the CSV file. The First array row array[0][]
	 * contains a datatype, like \<num\> or \<mail\>
	 * 
	 * @return content of csv file as String array
	 */
	public String[][] getContent() {
		return data;
	}

}
