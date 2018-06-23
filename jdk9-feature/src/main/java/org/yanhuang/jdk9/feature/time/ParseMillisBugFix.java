package org.yanhuang.jdk9.feature.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <li>Jdk1.8 has a bug that parse local date time from milliseconds.</li>
 * <li>see <a href="https://bugs.java.com/view_bug.do?bug_id=JDK-8031085">https://bugs.java.com/view_bug.do?bug_id=JDK-8031085</a></li>
 */
public class ParseMillisBugFix {

	public static void main(String[] args) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		LocalDateTime time = LocalDateTime.now();
		String localTime = df.format(time);
		System.out.println("LocalDateTime转成String类型的时间：" + localTime);
		LocalDateTime ldt = LocalDateTime.parse("20170928170705666", df);
		System.out.println("String类型的时间转成LocalDateTime：" + ldt);
	}


}
