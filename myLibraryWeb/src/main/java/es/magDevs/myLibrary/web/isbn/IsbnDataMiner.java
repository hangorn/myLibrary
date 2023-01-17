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
package es.magDevs.myLibrary.web.isbn;

import java.util.List;

import es.magDevs.myLibrary.model.beans.Libro;

public interface IsbnDataMiner {
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";
	public static final String REGEX_LETRAS = "[a-zA-Zá-úÁ-ÚçÇà-ùÀ-Ùâ-ûÂ-Û '-]+";

	String getName();
	
	List<Libro> getData(String isbn) throws Exception;
}
