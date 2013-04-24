package driver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
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
		try {
			PreparedStatement ps =
					DatabaseConnection.getConnection().prepareStatement(
							"SELECT turninid FROM turnins WHERE status = \"waiting\""
									+ " AND assignmentid = " + args[0]);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				System.out.println("Found a new turnin! It's id is " + rs.getString(1));
				System.out.println("Started turnin id #" + rs.getInt(1));
				new TurninExecutor(rs.getInt(1)).start();
				
				System.out.println("--------");
			}
		} catch (Exception e) {
			try {
				// Create file
				FileWriter fstream = new FileWriter("out.txt");
				BufferedWriter out = new BufferedWriter(fstream);
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				// stack trace as a string
				out.write(sw.toString());
				// Close the output stream
				out.close();
			} catch (Exception ex) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
	}
}