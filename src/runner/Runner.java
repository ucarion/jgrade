package runner;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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
			
			
			
			String cmd = "simplerunas.exe " + Config.getTestUser() + " " + Config.getTestPassword() + " turnins java -Xbootclasspath/p:..\\java\\Imports -cp ..\\" + lib_dir + ";" + path + " " + main + " " + input;
			//String cmd = "java -Xbootclasspath/p:java\\Imports -cp " + lib_dir + ";" + BASEPATH + path + " " + main;
			System.out.println("RUNNER IS EXECUTING " + cmd);
			ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
			pb.redirectErrorStream(true);
			pb.environment().put("JavaInputs", input);
			
			System.out.println("Test1");
			Process p = pb.start();
			System.out.println("Test2");
			(new Timeout(Config.getTimeout(), p)).start();
			System.out.println("Test2.1");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			System.out.println("Test3");
			
			// loop through the lines of the output adding them to output
			String nextLine;
			while((nextLine = br.readLine()) != null){
				System.out.println(nextLine);
				output += nextLine + "\n";
			}
			
			br.close();
			
			System.out.println("Test4");
			
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
			System.out.println("Test 2.2: Timeout created " + Thread.currentThread().getId());
		}
		
		@Override
		public void run() {
			System.out.println("Test 2.3: Run entered. " + Thread.currentThread().getId());
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
					System.out.println("Turnin timed out!");
					
					try { // where doin this man
						/*Field f = p.getClass().getDeclaredField("handle"); //may have to cast p to ProcessImpl or something
						f.setAccessible(true);
						Runtime.getRuntime().exec("taskkil /f /t /pid " + f.get(p));*/
						new PrintWriter(new BufferedOutputStream(p.getOutputStream())).write("exit\n");
						Thread.sleep(1000);
					} catch (Exception e) {
						
					} finally {
						p.destroy(); // hopefully it works
					}
					
				}
			}
		}
		
	}
}
