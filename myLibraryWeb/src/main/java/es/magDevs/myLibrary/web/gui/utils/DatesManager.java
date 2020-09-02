/**
 * Copyright (c) 2014-2015, Javier Vaquero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * required by applicable law or agreed to in writing, software
 * under the License is distributed on an "AS IS" BASIS,
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * the License for the specific language governing permissions and
 * under the License.
 */
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
	
	public static Integer presentation2Int(String date) {
		LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(Constants.PRESENTATION_FORMAT));
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
