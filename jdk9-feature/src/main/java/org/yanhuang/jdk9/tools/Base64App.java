package org.yanhuang.jdk9.tools;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class Base64App {

	public static void main(String[] args) {
		String msg = "测试";
		byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
		System.out.println(Base64.getUrlEncoder().
				encodeToString(bytes));
		System.out.println(Arrays.toString(bytes));
		byte[] decode = Base64.getUrlDecoder().decode("5rWL6K-V");
		System.out.println(new String(decode, StandardCharsets.UTF_8));
	}
}
