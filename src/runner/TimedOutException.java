package runner;

public class TimedOutException extends Exception {

	private static final long serialVersionUID = -9063827913624282095L;
	
	public TimedOutException(){
		super("This one timed out!");
	}
	
	public TimedOutException(String s){
		super(s);
	}
}
