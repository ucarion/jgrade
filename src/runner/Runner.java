package runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dbconnection.DatabaseConnection;

/**
 * The is the class that runs the file with command line and tells the user what
 * the output is
 * 
 * @author Sarah Herrman & Bob & Ulysse Carion
 * @version Filename: Runner.java
 * @version Date: 10/18/12
 * @version Program: Java thingie#2
 * @version Description: run files and shows output
 */
public class Runner {
	private static final String BASEPATH = "C:\\xampp\\htdocs\\test\\Grader\\turnins\\";
	boolean timedOut;
	/**
	 * Runs the passed program. For this program to work correctly, the program
	 * must already be compiled.
	 * 
	 * @param path
	 *            the path to the folder containing the program
	 * @param input
	 *            the input to pass to the program
	 * @param main
	 *            the name of the main class to execute.
	 * @return the output of the program
	 * @throws TimedOutException 
	 */
	public String run(String path, String input, String main) throws TimedOutException {
		
		timedOut = false;
		
		String[] s = new String[10];
		
		
		Runtime r = Runtime.getRuntime();
		String output = "";
		System.out.println("Runner called.");
		try {
			String cmd = "java -cp " + BASEPATH + path + " " + main;
			Process p = r.exec(cmd);
			System.out.println("RUNNER IS EXECUTING " + cmd);
			(new Timeout(10, p)).start();
			BufferedReader br =
					new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			// loop through the lines of the output adding them to output
			String nextLine = br.readLine();
			while (nextLine != null) {
				output += nextLine + "\n";
				nextLine = br.readLine();
			}
			
			if(timedOut)
				throw new TimedOutException();
			
			System.out.println("Runner finished");
			
		} catch (IOException e) {
			e.printStackTrace();
			output = e.getMessage();
		}
		return output;
	}
	
	private class Timeout extends Thread{
		
		int seconds;
		Process p;
		
		public Timeout(int secs, Process p){
			seconds = secs;
			this.p = p;
		}
		
		@Override
		public void run(){
			try {
				Thread.sleep(seconds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				try {
					p.exitValue();
				} catch(IllegalThreadStateException ex) {
					timedOut = true;
				} finally {
					p.destroy();
				}
			}
		}
		
	}
}
