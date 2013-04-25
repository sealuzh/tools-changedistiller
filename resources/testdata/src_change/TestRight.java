package test;

/**
 * This is our second (right) test class.
 * @author Beat Fluri
 */
public final class Test {
	
	/**
	 * Yet another changed attribute
	 */
	public String aNewString = "test_string";

	/*
	 * Scarab Lord Kungen the Elder
	 */
	public int aField;

	public static String sField;
	public volatile int vField;
	public transient String tField;
	public synchronized long synchField;
	
	private String[] arrayField;
	
	/**
	 * Yet another method with a better comment
	 * @param number
	 * @return
	 */
	protected int foo(int number) {
		
		// check if number is greater than 0
		boolean check = number > 0;
		int a = 0;
		int b = 2;
		
		// check the number
		
		if (! check) {
			a = 23 + Integer.parseInt("42");
			b = Math.abs(number);
			return a + b;
		} else {
			/* huga bimbo */
			b = Math.round(Math.random() /* mimimi mi */);
			String.valueOf(true);
			return b;
		}
		return 42;
	}

	public void emptyMethod() { }
	/*
	 * Inner classes are cool
	 */	
	
	private class Bar {
		private void newMethod() {
			System.out.println();
			System.out.println();
			System.out.println();		
		}
	}
	// the huga bar method
	public void newBar(long test) {
		System.out.println("aString");
	}

	public native void nativeMethod();
	
	public strictfp float strictfpMethod() {
		return 2.0f * 3.3f;
	}
}
