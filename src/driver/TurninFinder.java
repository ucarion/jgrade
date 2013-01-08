package driver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Finds waiting turnins and hands them over to TurninExecutor.
 * 
 * @author Ulysse Carion
 *
 */
public class TurninFinder {
	private static final String DATABASE = "jdbc:mysql://grader.computologycentral.com:3306/grading";
	private static final String USERNAME = "Grader";
	private static final String PASSWORD = "password";
	
	/**
	 * Finds and takes care of all waiting turnins.
	 * @param args
	 * @throws Exception if database connection doesn't work
	 */
	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(DATABASE, USERNAME, PASSWORD);
		PreparedStatement ps =
				con.prepareStatement("SELECT turninid FROM turnins WHERE status = \"waiting\"");
		ResultSet rs = ps.executeQuery();
		
		Runtime r = Runtime.getRuntime();
		Process p = r.exec("ls");
		BufferedReader br =
				new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		System.out.println("<FUCK THIS>");
		// loop through the lines of the output adding them to output
		String nextLine = br.readLine();
		while (nextLine != null) {
			System.out.println(nextLine);
			nextLine = br.readLine();
		}
		System.out.println("</FUCK THIS>");
		
		while (rs.next()) {
			System.out.println("Found a new turnin! It's id is " + rs.getString(1));
			Thread t = new TurninExecutor(rs.getInt(1));
			t.run();
			System.out.println("--------");
		}
	}
}