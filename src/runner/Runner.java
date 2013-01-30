package runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
	 */
	public static String run(String path, String input, String main) {
		Runtime r = Runtime.getRuntime();
		String output = "";
		System.out.println("Runner called.");
		try {
			String cmd = "java -cp " + BASEPATH + path + " " + main + " " + input;
			Process p = r.exec(cmd);
			System.out.println("RUNNER IS EXECUTING " + cmd);
			BufferedReader br =
					new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			// loop through the lines of the output adding them to output
			String nextLine = br.readLine();
			while (nextLine != null) {
				output += nextLine + "\n";
				nextLine = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			output = e.getMessage();
		}
		return output;
	}
}
