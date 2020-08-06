package es.magDevs.myLibrary.web.gui.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import es.magDevs.myLibrary.model.Constants;

public class DatesManager {


	public static String int2String(Integer date) {
		return LocalDate.of(date/10000, date % 10000 / 100, date % 100).format(DateTimeFormatter.ofPattern(Constants.STRING_FORMAT));
	}

	public static Integer string2Int(String date) {
		LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(Constants.STRING_FORMAT));
		return localDate.getYear()*10000 + localDate.getMonthValue()*100 + localDate.getDayOfMonth();
	}
	
	public static String getStringToday() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern(Constants.STRING_FORMAT));
	}
	
	public static Integer getIntToday() {
		LocalDate localDate = LocalDate.now();
		return localDate.getYear()*10000 + localDate.getMonthValue()*100 + localDate.getDayOfMonth();
	}
	
	public static String string2Presentation(String date) {
		return LocalDate.parse(date, DateTimeFormatter.ofPattern(Constants.STRING_FORMAT)).format(DateTimeFormatter.ofPattern(Constants.PRESENTATION_FORMAT));
	}
	
	public static String int2Presentation(Integer date) {
		return LocalDate.of(date/10000, date % 10000 / 100, date % 100).format(DateTimeFormatter.ofPattern(Constants.PRESENTATION_FORMAT));
	}
}
