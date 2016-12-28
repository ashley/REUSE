package test;

public final class Test {
	
	public String aNewString = "test_string";

	public int aField;

	public static String sField;
	public volatile int vField;
	public transient String tField;
	public synchronized long synchField;
	
	private String[] arrayField;

	protected int foo(int number) {
		
		boolean check = number > 0;
		int a = 0;
		int b = 2;
		
		
		if (! check) {
			a = 23 + Integer.parseInt("42");
			b = Math.abs(number);
			return a + b;
		} else {
			b = Math.round(Math.random());
			String.valueOf(true);
			return b;
		}
		return 42;
	}

	public void emptyMethod() { }
	
	private class Bar {
		private void newMethod() {
			System.out.println();
			System.out.println();
			System.out.println();		
		}
	}
	public void newBar(long test) {
		System.out.println("aString");
	}

	public native void nativeMethod();
	
	public strictfp float strictfpMethod() {
		return 2.0f * 3.3f;
	}
}
