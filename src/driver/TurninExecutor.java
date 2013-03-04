package driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import turnin.Turnin;
import dbconnection.DatabaseConnection;

/**
 * A class that compiles/runs/tests waiting programs.
 * 
 * @author Ulysse Carion
 * 
 */
public class TurninExecutor extends Thread {
	private int turnin_id;
	
	/**
	 * Create a new TurninExecutor.
	 * 
	 * @param turnin_id
	 *            the id of the turnin in the database
	 */
	public TurninExecutor(int turnin_id) {
		this.turnin_id = turnin_id;
		System.out.println("New TurninExec made w/ id=" + turnin_id);
	}
	
	/**
	 * Compiles, runs, and executes this TurninExecutor's turnin.
	 * @return 
	 */
	public void start() {
		Turnin t = new Turnin(turnin_id);
		String s = getCompilableInfo();
		
		if (s.equals("nothing"))
			return;
		
		if (t.compile()) {
			if (s.equals("compile"))
				return;
			
			t.testSource();
			t.run();
		}
	}
	
	private String getCompilableInfo() {
		try {
			String sql =
					"SELECT `compilable` FROM `turnins` , `assignments` "
							+ "WHERE turnins.turninid = " + turnin_id + " "
							+ "AND turnins.assignmentid = assignments.assignmentid";
			PreparedStatement ps =
					DatabaseConnection.getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			rs.next();
			System.out.println("FOUND THAT COMPILABLE IS: " + rs.getString(1));
			return rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
}
