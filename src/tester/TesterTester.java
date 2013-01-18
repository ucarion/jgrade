package tester;

public class TesterTester {
	public static void main(String[] args) throws Exception {
		String rule = "<S:>.*for.*<I:>3<O:>XXX<I:>5<O:>XXXXX<I:>7<O:>XXXXXXX";
		String[] a = Tester.getInputs(rule);
		for (String s : a)
			System.out.println(s);
		System.out.println(Tester.testOutput("3", "XXX", rule));
		System.out.println(Tester.testOutput("5", "XXX", rule));
		System.out.println(Tester.testOutput("5", "XXXXX", rule));
		System.out.println(Tester.testOutput("7", "XXXXXXX", rule));
		System.out.println(Tester.testOutput("3", "XXXXX", rule));
	}
}
