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
package es.magDevs.myLibrary.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase con valores constantes 
 * @author javier.vaquero
 *
 */
public class Constants {
	//Tablas
	public static final String AUTHORS_TABLE = "Autor";
	public static final String COLLECTIONS_TABLE = "Coleccion";
	public static final String PUBLISHERS_TABLE = "Editorial";
	public static final String BOOKS_TABLE = "Libro";
	public static final String TYPES_TABLE = "Tipo";
	public static final String TRANSLATORS_TABLE = "Traductor";
	public static final String PLACES_TABLE = "Ubicacion";
    public static final String USERS_TABLE = "Usuario";
    
    //Roles
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_ROLE_ADMIN = "ROLE_ADMIN";
	
	/**
	 * Distintos tipos de acciones que se pueden realizar
	 * @author javier.vaquero
	 *
	 */
	public enum ACTION {
		LIST("List"),
		CREATE("Create"),
		UPDATE("Update"),
		READ("Read");
		
		String value;
		private ACTION(String s) {
			value = s;
		}
		public String get() {
			return value;
		}
	}
	
	/**
	 * Apartados o secciones de la aplicacion
	 * @author javier.vaquero
	 *
	 */
	public enum SECTION {
		BOOKS("books",0),
		AUTHORS("authors",1),
		PUBLISHERS("publishers",2),
		COLLECTIONS("collections",3),
		TYPES("types",4),
		PLACES("places",5),
		TRANSLATORS("translators",6);
		
		private String value;
		private Integer order;
		private static Map<String, Integer> orders;
		private static void setOrder(String value, Integer order) {
			if(orders == null) {
				orders = new HashMap<String,Integer>();
			}
			orders.put(value, order);
		}
		public static int getOrder(String value) {
			return orders.get(value);
		}
		private SECTION(String s, int i) {
			value = s;
			order = i;
			setOrder(value, order);
		}
		public String get() {
			return value;
		}
		public int getOrder() {
			return order;
		}
	}
}