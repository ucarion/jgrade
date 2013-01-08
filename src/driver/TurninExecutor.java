package driver;

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
	 * @param turnin_id the id of the turnin in the database
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
		if (t.compile()) {
			System.out.println("Compile success");
			t.testSource();
			t.run();
		}
	}
}
