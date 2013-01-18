package driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import config.Config;

/**
 * Finds waiting turnins and hands them over to TurninExecutor.
 * 
 * @author Ulysse Carion
 * 
 */
public class TurninFinder {
	private static final String DATABASE = Config.getFullDatabaseName();
	private static final String USERNAME = Config.getUsername();
	private static final String PASSWORD = Config.getPassword();
	
	/**
	 * Finds and takes care of all waiting turnins.
	 * 
	 * @param args
	 * @throws Exception
	 *             if database connection doesn't work
	 */
	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(DATABASE, USERNAME, PASSWORD);
		PreparedStatement ps =
				con.prepareStatement("SELECT turninid FROM turnins WHERE status = \"waiting\"");
		ResultSet rs = ps.executeQuery();
		
		while (rs.next()) {
			System.out.println("Found a new turnin! It's id is " + rs.getString(1));
			Thread t = new TurninExecutor(rs.getInt(1));
			t.run();
			System.out.println("--------");
		}
	}
}