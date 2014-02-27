package de.yunx.datamerge.measures.frequency;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.yunx.datamerge.mySQL.PatStatConnect;

public class AircraftCompanies_IDF implements FrequencyDistribution {

	private PatStatConnect psc = null;
	private int default_val;

	public AircraftCompanies_IDF() {
		psc = new PatStatConnect("130.149.207.137", "patstatApril2013", "user",
				"patstat");
		//System.out.println("max "+getMaxIDF()*2/3);
		//System.out.println("max "+getMaxIDF());
		default_val = (getMaxIDF()*2/3);
		//System.out.println("default val set to "+default_val);
	}

	public boolean close() {
		return psc.close();
	}

	private int getFrequencyx(String token) {
		token = token.toLowerCase();

		ResultSet rs = psc
				.query("SELECT idf FROM marisa_verteilung WHERE token = '"
						+ token + "';");

		try {
			if (!rs.first()) {
				return -1;
			}
			// TODO wenn werte nicht gefunden, dann 2/3 als ungefähre mitte
			// einer log-fkt annehnem :)
			return rs.getInt("idf");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("token " + token + " nicht gef catch");
			return -1;
		}
	}

	public double getFrequency(String token) {
		float x = getFrequencyx(token);
		// x = x / (getMaxIDF()-getMinIDF());
		if(x==-1)
			x = default_val;
		x = x / getMaxIDF();
		return x;
	}

	private int max = -1;

	private int getMaxIDF() {
		if (max > -1)
			return max;
		PatStatConnect psc = new PatStatConnect("130.149.207.137",
				"patstatApril2013", "user", "patstat");
		ResultSet rs = psc
				.query("SELECT max(idf) as max FROM marisa_verteilung;");
		try {
			if (!rs.first())
				return -1;
			max = rs.getInt("max");
			// System.out.println("max "+max);
			return max;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	/*
	 * private int min = -1; private int getMinIDF(){ if(min>-1)return min;
	 * PatStatConnect psc = new PatStatConnect("130.149.207.137",
	 * "patstatApril2011", "user", "patstat"); ResultSet rs =
	 * psc.query("SELECT min(idf) as min FROM tls206_person_verteilung;"); try {
	 * if(!rs.first()) return 0; min = rs.getInt("min");
	 * //System.out.println("min "+min); return min; } catch (SQLException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); return 0; } }
	 */

}
