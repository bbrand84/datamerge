package de.yunx.datamerge.mySQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class PatStatConnect {

	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	public PatStatConnect(){
		connect("130.149.207.137", "patstatApril2013", "user", "patstat");
	}
	
	public PatStatConnect(String url, String db_name, String user, String password){
		connect(url, db_name, user, password);
	}
	
	
	/**
	 * 
	 * @param fields
	 * @param table
	 * @param more sth to add to query, like WHERE clause etc
	 */
	public ResultSet query(String query){
		if(conn==null) return null; //TODO throw
		//boolean result = true;
		try {
			stmt = conn.createStatement();
			ResultSet rs =  stmt.executeQuery(query);			
			return rs;
			
			//conn.close();

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return rs;
		} 
		
//		finally {
//			// it is a good idea to release
//			// resources in a finally{} block
//			// in reverse-order of their creation
//			// if they are no-longer needed
//
//			if (rs != null) {
//				try {
//					rs.close();
//				} catch (SQLException sqlEx) {
//				} // ignore
//
//				rs = null;
//			}
//
//			if (stmt != null) {
//				try {
//					stmt.close();
//				} catch (SQLException sqlEx) {
//				} // ignore
//
//				stmt = null;
//			}
//
//		}
		
	}
	
	private void connect(String url, String db_name, String user, String password) {

		//boolean result = true;
		try {
			conn = DriverManager
					.getConnection("jdbc:mysql://130.149.207.137/patstatApril2013?"
							+ "user=user&password=patstat");

			// Do something with the Connection

//			stmt = conn.createStatement();
//			rs = stmt
//					.executeQuery("SELECT person_name FROM tls206_person WHERE person_name REGEXP '^(.*| )siemens(.*| )$'");
//
//			while (rs.next() != false) {
//				System.out.println(rs.getString(1));
//			}


			//System.out.println("verbunden zu patstat");
			//conn.close();

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			//result =  false;
		} 
	}
	
	public boolean close(){
		try {
			if(!conn.isClosed())
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
