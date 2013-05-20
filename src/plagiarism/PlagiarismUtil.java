package plagiarism;

public class PlagiarismUtil {
	/**
	 * Gets the plagiarism score (from 0 to 1) between two strings.
	 * 
	 * @param a
	 *            the first string
	 * @param b
	 *            the second string
	 * @return a plagiarism score, going from 0 (not plagiarized) to 1
	 *         (completely plagiarized).
	 */
	public static double getPlagiarism(String a, String b) {
		return 1 - getLevenshteinDistance(a, b)
				/ (double) Math.max(a.length(), b.length());
	}
	
	/*
	 * From http://rosettacode.org/wiki/Levenshtein_distance#Java
	 */
	private static int getLevenshteinDistance(String a, String b) {
		a = a.toLowerCase();
		b = b.toLowerCase();
		
		int[] costs = new int[b.length() + 1];
		for (int i = 0; i <= a.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= b.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (a.charAt(i - 1) != b.charAt(j - 1))
							newValue =
									Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[b.length()] = lastValue;
		}
		return costs[b.length()];
	}
}