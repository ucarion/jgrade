package driver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
							"SELECT turninid FROM turnins WHERE status = \"waiting\"");
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				System.out.println("Found a new turnin! It's id is " + rs.getString(1));
				ExecutorService executor = Executors.newSingleThreadExecutor();
				Future<String> future = executor.submit(new TurninExecutor(rs.getInt(1)));
				
				try {
					System.out.println("Started..");
					System.out.println(future.get(5, TimeUnit.SECONDS));
					System.out.println("Finished!");
				} catch (TimeoutException e) {
					System.out.println("QUITTING");
					updateDBTimeout(rs.getInt(1));
				}
				
				executor.shutdownNow();
				
				System.out.println("--------");
			}
		} catch (Exception e) {
			try {
				// Create file
				FileWriter fstream = new FileWriter("C:\\out.txt");
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
	
	private static void updateDBTimeout(int turninid) {
		String sql =
				"UPDATE `turnins` SET output='Error: Timeout', status='error' WHERE turninid="
						+ turninid;
		System.out.println(sql);
		PreparedStatement ps;
		try {
			ps = DatabaseConnection.getConnection().prepareStatement(sql);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}