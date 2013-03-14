package compiler;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Using javax.tool's JavaCompiler class, this class finds all .java files in a
 * folder, compiles them, and detects compile errors.
 * 
 * @author Ulysse & Sarah
 */
public class Compiler {
	private static final String BASEPATH = "C:\\xampp\\htdocs\\test\\Grader\\turnins\\";
	
	static {
		System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.7.0_11");
	}
	
	/**
	 * Compiles a program.
	 * 
	 * @param path
	 *            the path to the file containing the program to execute.
	 * @param main
	 *            the name of the main class of the program to compile.
	 * @return the output of the compiler (which may be an empty string if there
	 *         are no compile errors)
	 */
	public static String compile(String path, String main) {
		String output = "";
		
		try {
			File[] subfiles = (new File(BASEPATH + path)).listFiles();
			// File[] subfiles = (new File(path)).listFiles();
			List<File> files = new ArrayList<File>();
			for (File f : subfiles) {
				if (f.getName().endsWith(".java"))
					files.add(f);
			}
			
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager =
					compiler.getStandardFileManager(null, null, null);
			
			Writer w = new StringWriter();
			
			Iterable<? extends JavaFileObject> compilationUnits1 =
					fileManager.getJavaFileObjectsFromFiles(files);
			if ( !compiler
					.getTask(w, fileManager, null, null, null, compilationUnits1)
					.call()) {
				output =
						"Your program contains compile errors. Please fix these and try again: \n" + w.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			output = "There was an error";
		}
		return output;
	}
}
