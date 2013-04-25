package test;

/**
 * This is our first (left) test class.
 * @author Beat Fluri
 */
public class Test {
	
	/**
	 * Yet another attribute
	 */
	public String aString = "test_string";
	
	/*
	 * Scarab Lord Kungen
	 */
	public String aField;

	public static String sField;
	public volatile int vField;
	public transient String tField;
	public synchronized long synchField;
	
	private String arrayField;
	
	public Integer anInteger = new Integer(1);
	
	/**
	 * Yet another method with a comment
	 * @param number
	 * @return
	 */
	public int foo(int number) {
		System.out.println("left");
		
		// check if number is greater than -1
		boolean check = number > 0;
		int a = 0;
		int b = 2;

		// check the huga number
		// and some new

		if (check) {
			/* This is the most beautiful comment in the world
			 * and soon it will be gone :'(
			 */
			a = 23 + Integer.parseInt("42");
			b = Math.round(Math.random() /* mimimi */);
			return a + b;
		} else {
			/* huga bimbo */
			b = Math.abs(number);
			String.valueOf(true);
			return b;
		}
	}
	/*
	 * Inner classes are cool
	 */	
	
	private class Bar {
		private void method() {
			System.out.println();
			System.out.println();
			System.out.println();
		}
	}
	// the huga bar method
	public void bar(int test) {
		System.out.println("aString");
	}

	public native void nativeMethod();
	
	public strictfp float strictfpMethod() {
		return 2.0f * 3.3f;
	}
}