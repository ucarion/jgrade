package driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dbconnection.DatabaseConnection;

/**
 * Finds waiting turnins and hands them over to TurninExecutor.
 * 
 * @author Ulysse Carion
 * 
 */
public class TurninFinder {
	/**
	 * Finds and takes care of all waiting turnins.
	 * 
	 */
	public static void main(String[] args) throws Exception {
		PreparedStatement ps =
				DatabaseConnection.getConnection().prepareStatement(
						"SELECT turninid FROM turnins WHERE status = \"waiting\"");
		ResultSet rs = ps.executeQuery();
		
		while (rs.next()) {
			System.out.println("Found a new turnin! It's id is " + rs.getString(1));
			Thread t = new TurninExecutor(rs.getInt(1));
			t.run();
			System.out.println("--------");
		}
	}
}