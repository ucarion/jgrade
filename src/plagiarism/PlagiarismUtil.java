package plagiarism;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlagiarismUtil {	
	/**
	 * Finds and outputs the plagiarism of each string passed; these strings
	 * would represent classes in the programs. Specifically, this program
	 * finds for each string passed the string it is most related to in the
	 * passed array. With this result, the program then outputs each string, the
	 * string it associated with, and what "percentage" (a number between 0 and
	 * 1) is plagiarized.
	 * 
	 * @param strings
	 *            the strings to evaluate for plagiarism.
	 */
	public static PlagiarismStat[] evalPlagiarism(String[] strings) {
		PlagiarismStat[] arr = new PlagiarismStat[strings.length];
		for (int i = 0; i < strings.length; i++) {
			arr[i] = findPlagiarism(i, strings);
		}
		return arr;
	}
	
	public static PlagiarismStat findPlagiarism(int index, String[] strings) {
		PlagiarismStat ps = new PlagiarismStat(null, -1);
		
		for (int i = 0; i < strings.length; i++) {
			if (i == index)
				continue;
			
			double plagiarism = getPlagiarismIndex(strings[i], strings[index]);
			
			if (plagiarism > ps.getMax())
				ps.update(strings[i], plagiarism);
		}
		
		return ps;
	}
	
	/**
	 * Returns a number between 0 and 1 indicating the percentage of the program
	 * estimated to be plagiarized. This is calculated as 1 - levenshtein(s1,
	 * s2) / max(s1.length, s2.length).<br>
	 * <br>
	 * 
	 * <i>Note:</i> these numbers may be very high or low when working with
	 * short strings.
	 * 
	 * @param s1
	 *            The initial string.
	 * @param s2
	 *            The string it becomes.
	 * @return A number between 0 and 1 representing the percentage of these two
	 *         programs being copied from one another.
	 */
	public static double getPlagiarismIndex(String s1, String s2) {
		return 1 - getLevenshteinDistance(s1, s2)
				/ (double) Math.max(s1.length(), s2.length());
	}
	
	/*
	 * From http://rosettacode.org/wiki/Levenshtein_distance#Java
	 */
	private static int getLevenshteinDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		
		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue =
									Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}
	
	static class PlagiarismStat {
		private String str;
		private double max;
		
		public PlagiarismStat(String str, double max) {
			this.str = str;
			this.max = max;
		}
		
		public String getString() {
			return str;
		}
		
		public double getMax() {
			return max;
		}
		
		public void update(String str, double max) {
			this.str = str;
			this.max = max;
		}
	}
}
