package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class Utilities {

	
	public static String getTimeStamp(LocalDateTime d) {		
		return d.toString();//Long.toString(d.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
	}
	
	public static String getTimeStamp(LocalDate d,LocalTime t) {
		LocalDateTime temp = d.atTime(t);
		return getTimeStamp(temp);
	}
	public static String getTimeStamp(LocalDate d) {
		return  getTimeStamp(d);
	}
	public static String getTimeStamp(LocalTime t) {
		return  getTimeStamp(t);
	}
}
