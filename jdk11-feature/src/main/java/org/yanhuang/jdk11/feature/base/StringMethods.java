package org.yanhuang.jdk11.feature.base;

public class StringMethods {

	public static void main(String[] args) {
		// isBlank
		String blanked = "\t \n";
		System.out.format("is empty? %b, is blank? %b\n", blanked.isEmpty(), blanked.isBlank());

		// Trip
		String tripped = "\t\n this is can be tripped \t\t \n";
		System.out.format("string is: %s", tripped);
		System.out.format("left tripped is: %s", tripped.stripLeading());
		System.out.format("right tripped is: %s\n", tripped.stripTrailing());
		System.out.format("all tripped is: %s\n", tripped.strip());
	}
}
