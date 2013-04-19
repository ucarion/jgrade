import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.JApplet;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class BaseRunner extends JApplet {

	private static final long serialVersionUID = -2594949974470065461L;

	JTextField j;

	@Override
	public void init() {

		this.rootPane.setSize(900, 900);
		this.setForeground(Color.BLUE);
		
		j = new JTextField();
		
		j.setSize(900, 900);
		add(j);
		
		this.getParameter("");

		log("Downloading...\n");
		
		
		
		
		
		/*
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() 
			{
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
					
					FileInputStream f = new FileInputStream("EvenSum.class");
					String t = "";
					while(f.available() > 0)
						t = t + (char)f.read();
					j.setText(j.getText() + t);
					//new File("EvenSum.class").deleteOnExit();
				} catch (Exception c) {
					j.setText(j.getText() + c.getMessage());
				}
				
				return null;
			}

			});*/
		
		
		
	}

	@Override
	public void start() {

		/*
		try {
			j.setText(j.getText() + "\nRunning....");
			/*Process p = AccessController.doPrivileged(new PrivilegedAction<Process>() {
				public Process run() 
				{
					 try {
						return Runtime.getRuntime().exec("java EvenSum");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						j.setText(e.getMessage());
					}
					return null;
				}
			});*/
			
			//new StreamReader(p.getInputStream(), j);
			
			
			/*j.setText(j.getText() + " Run: " + AccessController.doPrivileged(new PrivilegedAction<Object>() {
				public Object run() 
				{
					try {
						return ClassLoader.getSystemClassLoader().loadClass("EvenSum").getMethod("main",String[].class).invoke(this, (Object[])null);
					} catch (Exception e){
						j.setText(j.getText() + e.getMessage());
					}
					return null;
					
				}
			}));
			
			/*BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			// loop through the lines of the output adding them to output
			String nextLine = br.readLine();
			while (nextLine != null) {
				j.setText(j.getText() + " " + nextLine + "\n");
				nextLine = br.readLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log(e.getMessage());
		}*/
		
		
		try {
			final URL[] urls = {new URL("http://ocean/test/Grader/turnins/5113d173eb709/")};
			
			final URLClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
				public URLClassLoader run() {
					return new URLClassLoader(urls);
				}
			});
			Class<?> c = cl.loadClass("EvenSum");
			for(Method m : c.getMethods()){
				m.invoke(this, (Object[])null);
			}
			
			cl.close();
			
		} catch(MalformedURLException e){
			log(e.getMessage());
		} catch (ClassNotFoundException e) {
			log(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void log(String s){
		j.setText(j.getText() + " " + s);
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
