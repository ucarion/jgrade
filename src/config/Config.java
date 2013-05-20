package config;

import java.io.File;

import org.ini4j.Wini;

public class Config {
	private static final String PATH_TO_INI = "config.ini";
	private static Wini config;
	
	static {
		try {
			config = new Wini(new File(PATH_TO_INI));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the name of the host system, i.e. "localhost", "127.0.0.1", or
	 * "google.com".
	 * 
	 * @return the name of the host system.
	 */
	public static String getHost() {
		return config.get("sql", "host");
	}
	
	/**
	 * Gets the password the Java programs are supposed to use.
	 * 
	 * @return the password for the user returned by getUsername().
	 */
	public static String getPassword() {
		return config.get("sql", "password");
	}
	
	/**
	 * Gets what port the Java programs are supposed to connect to, i.e. "3306".
	 * 
	 * @return the port database connections are going through.
	 */
	public static String getPort() {
		return config.get("sql", "port");
	}
	
	/**
	 * Gets the username the Java programs use for connections.
	 * 
	 * @return the username of the user Java makes it database connections
	 *         through.
	 */
	public static String getUsername() {
		return config.get("sql", "username");
	}
	
	/**
	 * Gets the name of the grading database on the local system, i.e.
	 * "grading".
	 * 
	 * @return the name the database to connect to.
	 */
	public static String getDatabase() {
		return config.get("sql", "database");
	}
	
	/**
	 * Util method which gets the complete name of the database, including host
	 * name, port, and database.
	 * 
	 * @return a string which can be used as the "database" in mySQL
	 *         connections.
	 */
	public static String getFullDatabaseName() {
		return "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase();
	}
	
	/**
	 * Get the amount of time to wait before concluding a turnin has timed out.
	 * Returns a value in seconds.
	 * 
	 * @return the number of seconds for a turnin should time out.
	 */
	public static int getTimeout() {
		return 10;
	}
}
