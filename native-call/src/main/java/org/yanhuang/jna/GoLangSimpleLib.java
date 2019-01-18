package org.yanhuang.jna;

import com.sun.jna.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GoLangSimpleLib {

	public interface GoSimpleFunctions extends Library {

		// GoSlice class maps to:
		// C type struct { void *data; GoInt len; GoInt cap; }
		class GoSlice extends Structure {
			public static class ByValue extends GoSlice implements Structure.ByValue {
			}

			public Pointer data;
			public long len;
			public long cap;

			protected List<String> getFieldOrder() {
				return Arrays.asList("data", "len", "cap");
			}
		}

		// GoString class maps to:
		// C type struct { const char *p; GoInt n; }
		class GoString extends Structure {
			public static class ByValue extends GoString implements Structure.ByValue {
			}

			public String p;
			public long n;

			protected List<String> getFieldOrder() {
				return Arrays.asList("p", "n");
			}

		}

		// Foreign functions
		long Add(long a, long b);

		double Cosine(double val);

		void Sort(GoSlice.ByValue vals);

		long Log(GoString.ByValue str);
	}

	public static void main(String argv[]) {
		GoSimpleFunctions goFunctions = Native.load(
				"/home/zhyhang/temp/code-java/MathLib.so", GoSimpleFunctions.class);

		System.out.printf("goFunctions.Add(12, 99) = %s\n", goFunctions.Add(12, 99));
		System.out.printf("goFunctions.Cosine(1.0) = %s\n", goFunctions.Cosine(1.0));

		// Call Sort
		// First, prepare data array
		long[] nums = new long[]{53, 11, 5, 2, 88};
		Memory arr = new Memory(nums.length * Native.getNativeSize(Long.TYPE));
		arr.write(0, nums, 0, nums.length);
		// fill in the GoSlice class for type mapping
		GoSimpleFunctions.GoSlice.ByValue slice = new GoSimpleFunctions.GoSlice.ByValue();
		slice.data = arr;
		slice.len = nums.length;
		slice.cap = nums.length;
		goFunctions.Sort(slice);
		System.out.print("goFunctions.Sort(" + Arrays.toString(nums) + ") = [");
		long[] sorted = slice.data.getLongArray(0, nums.length);
		for (int i = 0; i < sorted.length; i++) {
			System.out.print(sorted[i] + " ");
		}
		System.out.println("]");

		// Call Log
		GoSimpleFunctions.GoString.ByValue str = new GoSimpleFunctions.GoString.ByValue();
		str.p = "Hello Java!你好 Java!";
		str.n =  str.p.getBytes(StandardCharsets.UTF_8).length;
		System.out.printf("msgid %d\n", goFunctions.Log(str));
		System.out.printf("msgid %d\n", goFunctions.Log(str));

	}

}
