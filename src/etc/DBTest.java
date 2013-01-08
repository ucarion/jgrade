package etc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import tester.Tester;

public class DBTest {
	private static final String DATABASE = "jdbc:mysql://10.55.255.252:3306/grading";
	private static final String USERNAME = "ODBC";
	private static final String PASSWORD = "";
	
	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(DATABASE, USERNAME, PASSWORD);
		PreparedStatement ps =
				con.prepareStatement("SELECT rules FROM assignments WHERE assignmentid = 14");
		ResultSet rs = ps.executeQuery();
		rs.next();
		System.out.println("Found: " + rs.getString(1));
		String[] a = Tester.getInputs(rs.getString(1));
		for (String s : a)
			System.out.println("RULE: " + s);
	}
}
