package dd;

import org.junit.Test;

public class dd {
	public static void main(String[] args) {
		System.out.println("hello world");
	}

	public int getInt(int i) {
		return i;
	}

	public static int getI(int j) {
		return j;
	}

	private int getIn(int i) {
		return i;
	}

	private static int in(int i) {
		return i;
	}

	@Test
	public void getJ() throws Exception {
		System.out.println("hello world");
	}
}
