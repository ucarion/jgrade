package plagiarism;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import dbconnection.DatabaseConnection;

public class PlagiarismChecker {
	private static final String PATH_TO_TURNINS =
			"C:\\xampp\\htdocs\\test\\Grader\\turnins\\";
	private static final double SUSPICION_INDEX = 0.7;
	
	public static void main(String[] args) {
		try {
			plagiaCheck(Integer.parseInt(args[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void plagiaCheck(int assignment) throws SQLException {
		String sql =
				"SELECT turninid, path, main_class, status "
						+ "FROM turnins WHERE turnins.assignmentid = " + assignment;
		PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		String[] strs = getProgramsAsStrings(rs);
		int count = 0;
		while (rs.next()) {
			checkForPlagiarism(strs, count, rs);
			count++;
		}
	}
	
	private static void checkForPlagiarism(String[] strs, int index, ResultSet rs)
			throws SQLException {
		ArrayList<Integer> copiers = new ArrayList<Integer>();
		double max = 0;
		
		for (int i = 0; i < strs.length; i++) {
			if (i == index)
				continue;
			
			double plagia = PlagiarismUtil.getPlagiarism(strs[index], strs[i]);
			
			if (plagia > SUSPICION_INDEX) {
				rs.relative(i - index);
				copiers.add(rs.getInt(1));
				rs.relative(index - i);
			}
			
			if (plagia > max)
				max = plagia;
		}
		
		updateSuspicion(rs.getInt(1), copiers);
		updatePlagiarism(rs.getInt(1), max);
	}
	
	private static void updateSuspicion(int id, ArrayList<Integer> others)
			throws SQLException {
		String s = "";
		for (int other : others)
			s += getAuthor(other) + "\n";
		
		String sql =
				"UPDATE `turnins` SET `plag_from` = '" + s + "' WHERE turninid = " + id;
		DatabaseConnection.getConnection().prepareStatement(sql).executeUpdate();
	}
	
	private static void updatePlagiarism(int id, double plagia) throws SQLException {
		String result = new DecimalFormat("##.###").format(plagia * 100) + "%";
		String sql =
				"UPDATE turnins SET plagiarism = '" + result + "' WHERE turninid = " + id;
		System.out.println(sql);
		DatabaseConnection.getConnection().prepareStatement(sql).executeUpdate();
	}
	
	private static String getAuthor(int turninId) throws SQLException {
		String sql =
				"SELECT `name` FROM `users` WHERE `userid`=("
						+ "SELECT `studentid` FROM `turnins` WHERE `turninid`="
						+ turninId + ")";
		ResultSet rs =
				DatabaseConnection.getConnection().prepareStatement(sql).executeQuery();
		rs.next();
		return rs.getString(1);
	}
	
	private static String[] getProgramsAsStrings(ResultSet rs) throws SQLException {
		String[] a = new String[getSize(rs)];
		int count = 0;
		
		while (rs.next()) {
			String path =
					PATH_TO_TURNINS + rs.getString(2) + File.separator + rs.getString(3)
							+ ".java";
			System.out.println("Looking for file at: " + path);
			a[count] = readFile(path);
			
			if (a[count] == null)
				System.out.println(path + " yielded null!");
			
			count++;
		}
		rs.beforeFirst();
		
		return a;
	}
	
	private static String readFile(String path) {
		try {
			FileInputStream stream = new FileInputStream(new File(path));
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			stream.close();
			return Charset.defaultCharset().decode(bb).toString();
		} catch (Exception e) {}
		return null;
	}
	
	private static int getSize(ResultSet rs) throws SQLException {
		int rowcount = 0;
		if (rs.last()) {
			rowcount = rs.getRow();
			rs.beforeFirst();
		}
		return rowcount;
	}
}