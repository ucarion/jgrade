package tester;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * A class for running the tests required on a turnin. The syntax for the rules
 * must take the form:
 * 
 * <pre>
 *      <S:>{source code regex}\n
 *      <I:>{input 1}<O:>{output rule 1}
 *      <I:>{input 2}<O:>{output rule 2}
 *      <I:>{input 3}<O:>{output rule 3}
 *      etc.
 * </pre>
 * 
 * <i>NOTE: There should only be a newline after the source clause -- all other
 * content is one a single line.</i><br>
 * <br>
 * 
 * The methods to use are <code>testSource(String path, String rule)</code> and
 * <code>testOutput(String output, String rule)</code>, where <code>rule</code>
 * is in the aforementioned format.
 * 
 * @author Ulysse Carion
 * 
 */
public class Tester {
	private static final String BASEPATH = "turnins" + File.separator;
	
	/**
	 * Tests to see if a program's source code passes the source rule.
	 * 
	 * @param path
	 *            the path to the program's source code
	 * @param rule
	 *            the complete rule to test against
	 * @return true if it passes the test, false otherwise
	 */
	public static boolean testSource(String path, String rule) {
		String source = readFile(path);
		return getSourceRule(rule).matcher(source).find();
	}
	
	/**
	 * Tests to see if a program's output passes the output rule.
	 * 
	 * @param input
	 *            the string used as input
	 * @param output
	 *            what the program outputted
	 * @param rule
	 *            the complete rule to test against
	 * @return true if it passes the test, false otherwise
	 */
	public static boolean testOutput(String input, String output, String rule) {
		return getOutputRule(input, rule).matcher(output).find();
	}
	
	/**
	 * Extracts the inputs based on a turnin's rules.
	 * 
	 * @param rule
	 *            the rule a turnin is associated with
	 * @return the inputs to test against
	 */
	public static String[] getInputs(String rule) {
		String[] a = rule.split("<\\w:>");
		String[] inputs = new String[ (a.length - 2) / 2];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = a[2 * i + 2];
		}
		return inputs;
	}
	
	/*
	 * Splitting along the newlines gives you "<S:>{source rule}", so to get the
	 * actual rule, we use substring(4) to get everything after the fourth char.
	 */
	private static Pattern getSourceRule(String rule) {
		return Pattern.compile("(?i)" + rule.split("\\n")[0].substring(4));
	}
	
	/*
	 * Splitting along the <?:> clauses will get you an empty string, the source
	 * rule, then pairs of inputs and rules. What we want to do, then, is split
	 * along <?:> clauses and start looping through the inputs (which start at
	 * index = 2), going by twos until we run across the desired input. At this
	 * point, the subsequent entry in the array is the rule for that input.
	 */
	private static Pattern getOutputRule(String output, String rule) {
		String[] a = rule.split("<\\w:>");
		for (int i = 2; i < a.length; i += 2) {
			if (a[i].equals(output))
				return Pattern.compile("(?i)" + a[i + 1], Pattern.DOTALL);
		}
		return null;
	}
	
	private static String readFile(String path) {
		System.out.println("ATTEMPTING TO READ FROM " + BASEPATH + path);
		try {
			FileInputStream stream = new FileInputStream(new File(BASEPATH + path));
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			stream.close();
			return Charset.defaultCharset().decode(bb).toString();
		} catch (Exception e) {}
		return null;
	}
}
