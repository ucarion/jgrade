import java.awt.Color;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.JApplet;
import javax.swing.JTextArea;

public class BaseRunner extends JApplet {

	private static final long serialVersionUID = -2594949974470065461L;

	JTextArea j;

	@Override
	public void init() {

		this.rootPane.setSize(500, 500);
		this.setForeground(Color.BLUE);
		
		j = new JTextArea(30, 70);
		j.setLineWrap(true);
		
		j.setSize(500, 600);
		add(j);
		Console.redirectOutput(j);
		
		System.out.println("Running program " + getParameter("main"));
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	@Override
	public void start() {

		try {
			final URL[] urls = {new URL(getParameter("path") + "/")};
			
			final URLClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
				public URLClassLoader run() {
					return new URLClassLoader(urls);
				}
			});
			
			Class<?> c = cl.loadClass(getParameter("main"));
			Object[] args = new Object[1];
			args[0] = new String[0];
			for(Method m : c.getMethods()){
				if(m.getName().equals("main"))
					m.invoke(this, args);
			}
			
			cl.close();
			
		} catch(Exception e){
			e.printStackTrace();
		}

	}
}
