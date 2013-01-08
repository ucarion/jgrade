package turnin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import runner.Runner;
import tester.Tester;
import compiler.Compiler;

/**
 * A wrapper and util class for dealing with turnins. Handles all database
 * manipulation, compilation, and running processes. A proper usage of this
 * class would work along the lines of:
 * 
 * <pre>
 * Turnin t = new Turnin(ID_NUMBER);
 * if (t.compile()) {
 * 	t.testSource();
 * 	t.run();
 * }
 * </pre>
 * 
 * @author Ulysse
 * 
 */
public class Turnin {
	private static final String DATABASE =
			"jdbc:mysql://grader.computologycentral.com:3306/grading";
	private static final String USERNAME = "Grader";
	private static final String PASSWORD = "password";
	
	private static Connection con;
	
	static {
		con = getDBConnection();
	}
	
	private int id;
	private String path;
	
	/**
	 * Create a new Turnin. Note that this does not really create a new turnin--
	 * it simply creates a new Turnin class instance based on a pre-existent
	 * turnin the the database.
	 * 
	 * @param id
	 *            the id of the turnin in the database
	 */
	public Turnin(int id) {
		this.id = id;
		path = dbGet(id, "path");
	}
	
	/**
	 * Compile the program, updating the database as necessary.
	 * 
	 * @return true if and only if the compile is successful.
	 */
	public boolean compile() {
		String main = dbGet(id, "main_class");
		String compileResults = Compiler.compile(path, main);
		// System.out.println("Turnin#" + id + " compile result: " +
		// compileResults);
		if ( !compileResults.isEmpty()) {
			dbSet(id, "output", compileResults);
			dbSet(id, "status", "error");
			return false;
		}
		
		dbSet(id, "status", "compiled");
		return true;
	}
	
	/**
	 * Update the database according to whether the Turnin's source code is
	 * correct. Source rules are acquired from the database.
	 */
	public void testSource() {
		// System.out.println("Source test called!");
		String rule = getRule(id);
		String file = path + "/" + dbGet(id, "main_class") + ".java";
		dbSet(id, "tests", "<Source code passes test?>" + Tester.testSource(file, rule)
				+ "\n");
	}
	
	/**
	 * Run the program and update test results. Output tests are based on the
	 * information in the database. The turnin must have already been compiled
	 * before calling this method.
	 */
	public void run() {
		String rule = getRule(id);
		String[] inputs = Tester.getInputs(rule);
		String output = "";
		String testResults = dbGet(id, "tests");
		String main = dbGet(id, "main_class");
		boolean noRunErrors = true;
		for (String input : inputs) {
			String runResult = Runner.run(path, input, main);
			output += "<Input \"" + input + "\" yields output:>\n" + runResult + "\n";
			boolean testworked = Tester.testOutput(input, runResult, rule);
			noRunErrors = noRunErrors && testworked;
			String testResult = testworked ? "passed" : "failed";
			testResults += "<Test for input \"" + input + "\":> " + testResult + "\n";
		}
		
		dbSet(id, "output", output);
		dbSet(id, "tests", testResults);
		dbSet(id, "status", noRunErrors ? "ran" : "error");
	}
	
	private static Connection getDBConnection() {
		System.out.println("Created a new connection!");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(DATABASE, USERNAME, PASSWORD);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static void dbSet(int id, String column, String value) {
		System.out.println("Updating turnin#" + id + "'s " + column + " to \"" + value
				+ "\"");
		try {
			String sql = "UPDATE turnins SET " + column + " = ? WHERE turninid = " + id;
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, value);
			ps.executeUpdate();
		} catch (Exception e) {}
	}
	
	private static String dbGet(int id, String column) {
		try {
			String sql = "SELECT " + column + " FROM turnins WHERE turninid = " + id;
			System.out.println("Query is: " + sql);
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getString(1);
		} catch (Exception e) {
			e.printStackTrace();
			return "Danger, Will Robinson";
		}
	}
	
	private static String getRule(int id) {
		try {
			String sql =
					"SELECT assignments.rules " + "FROM turnins, assignments "
							+ "WHERE turninid = " + id + " "
							+ "AND turnins.assignmentid = assignments.assignmentid";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getString(1);
		} catch (Exception e) {
			e.printStackTrace();
			return "Intruder alert";
		}
	}
}
