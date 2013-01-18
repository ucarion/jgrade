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
import java.text.DecimalFormat;

import plagiarism.PlagiarismUtil.PlagiarismStat;
import config.Config;

public class PlagiarismChecker {
	private static final String DATABASE = Config.getFullDatabaseName();
	private static final String USERNAME = Config.getUsername();
	private static final String PASSWORD = Config.getPassword();
	
	private static Connection con;
	
	static {
		con = getDBConnection();
	}
	
	public static void main(String[] args) {
		evalPlagiarism(Integer.parseInt(args[0]));
	}
	
	private static void evalPlagiarism(int assignmentid) {
		PreparedStatement ps;
		try {
			ps =
					con.prepareStatement("SELECT turninid, path, main_class, status FROM turnins WHERE turnins.assignmentid = "
							+ assignmentid);
			ResultSet rs = ps.executeQuery();
			
			String[] strs = new String[getSize(rs)];
			int count = 0;
			while (rs.next()) {
				if ( !rs.getString(4).equals("ran"))
					continue;
				
				strs[count] =
						readFile("turnins/" + rs.getString(2) + "/" + rs.getString(3)
								+ ".java");
				count++;
			}
			rs.beforeFirst();
			
			count = 0;
			while (rs.next()) {
				if ( !rs.getString(4).equals("ran"))
					continue;
				evalPlagiarism(rs.getInt(1), strs, count);
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void evalPlagiarism(int id, String[] strs, int index) {
		PlagiarismStat ps = PlagiarismUtil.findPlagiarism(index, strs);
		updatePlagiarism(id, asPercent(ps.getMax()) + "% plagiarised");
		System.out.println("TURNIN #" + id + ":\nMATCHED W/ " + ps.getString() + "\n"
				+ asPercent(ps.getMax()) + "% PLAGIARISED.");
	}
	
	private static void updatePlagiarism(int id, String s) {
		try {
			PreparedStatement ps =
					con.prepareStatement("UPDATE turnins SET plagiarism = '" + s
							+ "' WHERE turninid = " + id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static String asPercent(double d) {
		DecimalFormat df = new DecimalFormat("##.###");
		return df.format(d * 100);
	}
	
	private static Connection getDBConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(DATABASE, USERNAME, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String readFile(String path) {
		try {
			FileInputStream stream = new FileInputStream(new File(path));
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			stream.close();
			return Charset.defaultCharset().decode(bb).toString();
		} catch (Exception e) {}
		return null;
	}
	
	private static int getSize(ResultSet rs) {
		int rowcount = 0;
		try {
			if (rs.last()) {
				rowcount = rs.getRow();
				rs.beforeFirst(); // not rs.first() because the rs.next() below
									// will move on, missing the first element
			}
			return rowcount;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
}
