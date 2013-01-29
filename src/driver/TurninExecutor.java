package driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbconnection.DatabaseConnection;
import turnin.Turnin;

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
	 */
	@Override
	public void run() {
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
			String sql = "SELECT assignments.compilable FROM assignments, turnins WHERE " +
					"assignment.assignmentid = turnins.assignmentid AND " +
					"turnins.turninid = (1)";
			PreparedStatement ps =
					DatabaseConnection.getConnection().prepareStatement(sql);
			ps.setInt(1, turnin_id);
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
