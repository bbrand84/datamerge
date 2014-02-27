package de.yunx.datamerge.measures.frequency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.yunx.datamerge.mySQL.PatStatConnect;

public class Patstat_411_Freq_IPLytics implements FrequencyDistribution {

	private Connection conn = null;
	private double default_val;
	private String table="TLS206_PERSON_DISTRIBUTION";

	public Patstat_411_Freq_IPLytics(Connection mysql) {
		conn = mysql;
		// System.out.println("max "+getMaxIDF()*2/3);
		// System.out.println("max "+getMaxIDF());
		default_val = 1;
		// System.out.println("default val set to "+default_val);
	}

	public void close() throws SQLException {
		conn.close();
	}

	

	private int getFrequencyx(String token) {
		token = token.toLowerCase();
		try {
			
			PreparedStatement stat1 = conn
					.prepareStatement("SELECT NUM FROM "+table+" WHERE TOKEN = '"
							+ token + "';");

			ResultSet rs = stat1.executeQuery();

			if (!rs.first()) {
				return -1;
			}
			// TODO wenn werte nicht gefunden, dann 2/3 als ungef�hre mitte
			// einer log-fkt annehnem :)
			return rs.getInt("NUM");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			System.out.println("token " + token + " nicht gef catch");
			return -1;
		}
	}

	public double getFrequency(String token) {
		double x = getFrequencyx(token);
		// x = x / (getMaxIDF()-getMinIDF());
		if (x == -1)
			x = default_val;
				
		x = 1-Math.log(x)/Math.log(this.getMax());
		return x;
	}

	private double max = -1;

	private double getMax() {
		if (max > -1)
			return max;
		
		PreparedStatement stat1;
		try {
			stat1 = conn
					.prepareStatement("SELECT max(NUM) as max FROM "+table);
			
			ResultSet rs = stat1.executeQuery();
			
			if (!rs.first())
				return -1;
			max = rs.getInt("max");
			return max;
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			return -1;
		}

	}
	
	


}
