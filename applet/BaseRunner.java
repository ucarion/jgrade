import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

public class BaseRunner extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2594949974470065461L;

	JTextArea j;

	@Override
	public void init() {

		this.rootPane.setSize(900, 900);

		j = new JTextArea();
		
		j.setSize(900, 900);
		add(j);
		
		this.getParameter("");

		try {
			
			URL url = new URL("http://ocean/test/Grader/turnins/5113d173eb709/EvenSum.class");
			url.openConnection();
			InputStream reader = url.openStream();

			FileOutputStream writer = new FileOutputStream("EvenSum.class");
			byte[] buffer = new byte[153600];
			int bytesRead = 0;

			while ((bytesRead = reader.read(buffer)) > 0) {  
				writer.write(buffer, 0, bytesRead);
				buffer = new byte[153600];
			}

			writer.close();
			reader.close();
			
			
			new File("EvenSum.class").deleteOnExit();
		} catch (IOException c) {
			c.printStackTrace();
		}
	}

	@Override
	public void start() {

		try {
			Process p = Runtime
					.getRuntime()
					.exec("java EvenSum");
			
			//ClassLoader.getSystemClassLoader().loadClass("EvenSum");

			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			// loop through the lines of the output adding them to output
			String nextLine = br.readLine();
			while (nextLine != null) {
				j.setText(j.getText() + " " + nextLine + "\n");
				nextLine = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private class StreamReader extends Thread{
		
		private JTextComponent j;
		private InputStream r;

		public StreamReader(InputStream r,JTextComponent j){
			this.j = j;
			this.r = r;
		}
		
		public void run(){
			BufferedReader br = new BufferedReader(new InputStreamReader(r));

			// loop through the lines of the output adding them to output
			String nextLine;
			try {
				nextLine = br.readLine();
				while (nextLine != null) {
					j.setText(j.getText() + nextLine + "\n");
					nextLine = br.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
