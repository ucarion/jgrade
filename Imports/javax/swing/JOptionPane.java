package javax.swing;

import javax.swing.Icon;
import java.awt.Component;

public class JOptionPane {
	private static int counter = 0;
	private static String[] inputs = System.getenv("JavaInputs").split(" ");
	
	public static void showMessageDialog(Component parentComponent, Object message) {
		System.out.println(message);
	}
	
	public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType) {
		System.out.print("[" + title + "] ");
		showMessageDialog(parentComponent, message);
	}
	
	public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType, Icon icon) {
		showMessageDialog(parentComponent, message, title, messageType);
	}
	
	public static String showInputDialog(Component parentComponent, Object message) {
		if (counter >= inputs.length) {
			System.out.println("[End of input]");
			return null;
		}
		String out = inputs[counter];
		++counter;
		System.out.println("[Inputting: " + out + "]");
		return out;
	}
	
	public static String showInputDialog(Component parentComponent, Object message, Object initialSelectionValue) {
		return showInputDialog(parentComponent, message);
	}
	
	public static String showInputDialog(Component parentComponent, Object message, String title, int messageType) {
		return showInputDialog(parentComponent, message);
	}
	
	public static Object showInputDialog(Component parentComponent, Object message, String title, int messageType, Icon icon, Object[] selectionValues, Object initialSelectionValue) {
		return showInputDialog(parentComponent, message);
	}
}