import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class BaseRunner extends JApplet {

	private static final long serialVersionUID = -2594949974470065461L;

	JTextArea j;
	JTextField in;
	LinkedBlockingQueue<Character> sb;
	JPanel panel;

	@Override
	public void init() {

		this.rootPane.setSize(500, 500);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		add(panel);
		
		sb = new LinkedBlockingQueue<Character>();
		
		j = new JTextArea(38, 70);
		j.setLineWrap(true);
		j.setEditable(false);
		j.setBackground(Color.BLACK);
		j.setForeground(Color.GREEN);
		
		
		JScrollPane scr = new JScrollPane(j);
		scr.setBounds(0, 0, 500, 470);
		panel.add(scr);
		Console.redirectOutput(j);
		
		in = new JTextField(70);
		panel.add(in);
		in.setBounds(400, 0, 500, 30);
		in.setEditable(true);
		in.setEnabled(true);
		in.setBackground(Color.RED);
		System.setIn(new InputStream(){

			@Override
			public int read() throws IOException {
				int c = -1;
			    try {
			      c = sb.take();            
			    } catch(InterruptedException ie) {
			    	ie.printStackTrace();
			    } 
			    return c;
			}
			
		});
		in.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				if(arg0.getKeyChar() == '\n'){
					addOutputLine(">" + in.getText());
					for(char c : in.getText().toCharArray())
						sb.add(c);
					sb.add('\n');
					in.setText("");
				}
				
			}
			
		});
		
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
			
			AccessController.doPrivileged(new PrivilegedExceptionAction<Object>(){

				@Override
				public Object run() throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
					Class<?> c = cl.loadClass(getParameter("main"));
					Object[] args = new Object[1];
					args[0] = new String[0];
					for(Method m : c.getMethods()){
						if(m.getName().equals("main")){
							
							m.invoke(this, args);
							
							break;
						}
					}
					
					cl.close();
					return null;
				}
				
			});
			
			
			
		} catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public void addOutputLine(String s){
		j.setText(j.getText() + s + "\n");
	}
}
