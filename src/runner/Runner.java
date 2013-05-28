package runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import config.Config;

/**
 * The is the class that runs the file with command line and tells the user what
 * the output is
 * 
 * @author Sarah Herrman & Ulysse Carion & Francesco Macagno
 * @version Filename: Runner.java
 * @version Date: 10/18/12
 * @version Program: Java thingie#2
 * @version Description: run files and shows output
 */
public class Runner {
	private static final String BASEPATH = "turnins" + File.separator;
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
	public String run(String path, String input, String main, String lib_dir) throws TimedOutException {
		timedOut = false;
		
		String output = "";
		System.out.println("Runner called.");
		try {
			
			String cmd = "simplerunas.exe test password java -Xbootclasspath/p:java\\Imports -cp " + lib_dir + ";" + BASEPATH + path + " " + main + " " + input;
			//String cmd = "java -Xbootclasspath/p:java\\Imports -cp " + lib_dir + ";" + BASEPATH + path + " " + main;
			System.out.println("RUNNER IS EXECUTING " + cmd);
			ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
			pb.redirectErrorStream(true);
			pb.environment().put("JavaInputs", input);
			
			
			Process p = pb.start();
			
			(new Timeout(Config.getTimeout(), p)).start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			// loop through the lines of the output adding them to output
			String nextLine;
			while((nextLine = br.readLine()) != null)
				output += nextLine + "\n";
			
			br.close();
			
			//If it times out
			if (timedOut)
				throw new TimedOutException(null, output);
			
			System.out.println("Runner finished");
			
		} catch (IOException e) {
			e.printStackTrace();
			output = e.getMessage();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}
	
	private class Timeout extends Thread {
		int seconds;
		Process p;
		
		public Timeout(int secs, Process p) {
			seconds = secs;
			this.p = p;
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(seconds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally {
				try {
					p.exitValue();
				} catch (IllegalThreadStateException ex) {
					timedOut = true;
				}
				finally {
					p.destroy();
				}
			}
		}
		
	}
}
