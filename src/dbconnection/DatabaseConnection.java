package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;

import config.Config;

public class DatabaseConnection {
	private static final String DATABASE = Config.getFullDatabaseName();
	private static final String USERNAME = Config.getUsername();
	private static final String PASSWORD = Config.getPassword();
	private static Connection con;
	
	static {
		con = getDBConnection();
	}
	
	/**
	 * Gets the database connection for the Java side.
	 * 
	 * @return a JDBC Connection to the database.
	 */
	public static Connection getConnection() {
		return con;
	}
	
	private static Connection getDBConnection() {
		try {
			System.out.println(DATABASE + " " + USERNAME + " " + PASSWORD);
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(DATABASE, USERNAME, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
