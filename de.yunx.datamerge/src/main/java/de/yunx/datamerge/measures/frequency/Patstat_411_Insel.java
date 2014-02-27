package de.yunx.datamerge.measures.frequency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.yunx.datamerge.mySQL.PatStatConnect;

public class Patstat_411_Insel implements FrequencyDistribution {

	private Connection conn = null;
	private int default_val;

	public Patstat_411_Insel(Connection mysql) {
		conn = mysql;
		// System.out.println("max "+getMaxIDF()*2/3);
		// System.out.println("max "+getMaxIDF());
		default_val = (getMaxIDF() * 2 / 3);
		// System.out.println("default val set to "+default_val);
	}

	public void close() throws SQLException {
		conn.close();
	}

	

	private int getFrequencyx(String token) {
		token = token.toLowerCase();
		try {
			
			PreparedStatement stat1 = conn
					.prepareStatement("SELECT idf FROM tls206_person_verteilung WHERE token = '"
							+ token + "';");

			ResultSet rs = stat1.executeQuery();

			if (!rs.first()) {
				return -1;
			}
			// TODO wenn werte nicht gefunden, dann 2/3 als ungef�hre mitte
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
		if (x == -1)
			x = default_val;
		x = x / getMaxIDF();
		return x;
	}

	private int max = -1;

	private int getMaxIDF() {
		if (max > -1)
			return max;
		
		PreparedStatement stat1;
		try {
			stat1 = conn
					.prepareStatement("SELECT max(idf) as max FROM tls206_person_verteilung");
			
			ResultSet rs = stat1.executeQuery();
			
			if (!rs.first())
				return -1;
			max = rs.getInt("max");
			// System.out.println("max "+max);
			return max;
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}

	}
	
	


}
