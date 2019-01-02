package org.yanhuang.jdk11.algorithm;

public class StringReverse {

	public static String reverse(String s) {
		if (null == s) {
			return s;
		}
		final int[] codePoints = s.codePoints().toArray();
		for (int i = 0, j = codePoints.length - 1; i < codePoints.length / 2; i++, j--) {
			int top = codePoints[j];
			codePoints[j] = codePoints[i];
			codePoints[i] = top;
		}
		return new String(codePoints, 0, codePoints.length);
	}

	public static void main(String[] args) {
		String s = "世界 really is very strong！\uD83D\uDCAA☀";
		System.out.println(reverse(s));
		System.out.println(new StringBuilder(s).reverse());
	}
}
