

import groovy.lang.Closure;
import groovy.lang.Script;

public class Test1 {
	static int NUMBEROFCALLS = 1000000;

	public static void main(String[] args) {

		Closure closure = getClosure();
		
		for (int i = 0; i < 100000; i++) {
			testSetProperty((Closure)closure.clone());
			testDelegate((Closure)closure.clone());
		}
		

	}

	private static void testDelegate(Closure closure) {
		long iniTime = System.currentTimeMillis();
		closure.setDelegate(new Object() {
			public Integer entity = 1;

			@Override
			public String toString() {
				return Integer.toString(entity);
			}
		});
		for (int i = 0; i < NUMBEROFCALLS; i++) {
			closure.call();
		}
		long timelength = System.currentTimeMillis() - iniTime;
		assert Integer.parseInt(closure.getDelegate().toString()) == NUMBEROFCALLS + 1;
		System.out.println("TimeDelegate :" + timelength);
	}

	private static void testSetProperty(Closure closure) {
		long iniTime = System.currentTimeMillis();
		closure.setProperty("entity", 1);
		for (int i = 0; i < NUMBEROFCALLS; i++) {
			closure.call();
		}
		assert (Integer) closure.getProperty("entity") == NUMBEROFCALLS + 1;
		long timelength = System.currentTimeMillis() - iniTime;
		System.out.println("TimeSetProperty :" + timelength);
	}

	private static Closure getClosure() {
		Script script = new Script() {

			@Override
			public Object run() {
				return evaluate("return {entity++}");
			}
		};

		Closure closure = (Closure) script.run();
		return closure;
	}
}
