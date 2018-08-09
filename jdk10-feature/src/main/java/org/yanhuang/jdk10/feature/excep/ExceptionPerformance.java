package org.yanhuang.jdk10.feature.excep;

public class ExceptionPerformance {

	public static final String msg = "for test";

	public static void main(String[] args) {
		for (int i = 0; i < 10000000; i++) {
			// warm up
			new NException(msg,null);
		}
		long tsb = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			new NException(msg,null);
		}
		System.out.println("time cost " + (System.currentTimeMillis() - tsb) + " ms.");
	}

	public static class NException extends Exception {

		private static final long serialVersionUID = 1L;

		public NException(String message, Throwable cause) {
			super(message, cause);
		}

	}

}
