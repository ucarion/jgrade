package config;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
	private static final String PATH_TO_INI = "C:\\xampp\\htdocs\\test\\Grader\\java\\config.ini";
	private static Properties config;
	
	static {
		config = new Properties();
		try {
			config.load(new FileInputStream(PATH_TO_INI));
		} catch (Exception e) {}
	}
	
	/**
	 * Gets the name of the host system, i.e. "localhost", "127.0.0.1", or
	 * "google.com".
	 * 
	 * @return the name of the host system.
	 */
	public static String getHost() {
		return config.getProperty("host");
	}
	
	/**
	 * Gets the password the Java programs are supposed to use.
	 * 
	 * @return the password for the user returned by getUsername().
	 */
	public static String getPassword() {
		return config.getProperty("password");
	}
	
	/**
	 * Gets what port the Java programs are supposed to connect to, i.e. "3306".
	 * 
	 * @return the port database connections are going through.
	 */
	public static String getPort() {
		return config.getProperty("port");
	}
	
	/**
	 * Gets the username the Java programs use for connections.
	 * 
	 * @return the username of the user Java makes it database connections
	 *         through.
	 */
	public static String getUsername() {
		return config.getProperty("username");
	}
	
	/**
	 * Gets the name of the grading database on the local system, i.e.
	 * "grading".
	 * 
	 * @return the name the database to connect to.
	 */
	public static String getDatabase() {
		return config.getProperty("database");
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
}
