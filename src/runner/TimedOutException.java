package runner;

public class TimedOutException extends Exception {
	private static final long serialVersionUID = -9063827913624282095L;
	
	String output;
	
	public TimedOutException() {
		super("This one timed out!");
	}
	
	public TimedOutException(String s) {
		super(s);
	}

	public TimedOutException(String s, String output) {
		super(s);
		this.output = output;
	}
	
	public String getPrevOutput(){
		return output;
	}
}
