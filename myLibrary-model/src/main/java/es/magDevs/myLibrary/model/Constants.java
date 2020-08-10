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
    public static final String PRESTAMOS_TABLE = "Prestamo";
    public static final String PENDIENTE_TABLE = "Pendiente";
    public static final String LEIDO_TABLE = "Leido";
    
    //Roles
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_ROLE_ADMIN = "ROLE_ADMIN";
    
    //Formatos de fechas
	public static final String INT_FORMAT = "yyyyMMdd";
	public static final String STRING_FORMAT = "yyyy-MM-dd";
	public static final String PRESENTATION_FORMAT = "dd/MM/yyyy";
	
	public static final boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

	/**
	 * Distintos tipos de acciones que se pueden realizar
	 * @author javier.vaquero
	 *
	 */
	public enum ACTION {
		LIST("list"),
		CREATE("create"),
		UPDATE("update"),
		MULTIUPDATE("multiupdate"),
		READ("read");
		
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
		TRANSLATORS("translators",6),
		LENDS("lends",7),
		USERS("users",8),
		PENDING("pending",9),
		READ("read",10);
		
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
	
	/**
	 * Controladores disponibles
	 * @author javier.vaquero
	 *
	 */
	public enum CONTROLLER {
		BOOKS("books"),
		AUTHORS("authors"),
		PUBLISHERS("publishers"),
		COLLECTIONS("collections"),
		TYPES("types"),
		PLACES("places"),
		TRANSLATORS("translators"),
		LENDS("lends"),
		USERS("users"),
		PENDING("pending"),
		READ("read");
		
		private static Map<String, CONTROLLER> instances;
		private static void saveInstance(String s,CONTROLLER c) {
			if(instances == null) {
				instances = new HashMap<String,CONTROLLER>();
			}
			instances.put(s, c);
		}
		private String value;
		public String get() {
			return value;
		}
		private CONTROLLER(String s) {
			value = s;
			saveInstance(s, this);
		}
		/**
		 * Obtiene el controlador asociado
		 * @param controller
		 * @return
		 */
		public static CONTROLLER getController(String controller) {
			return instances.get(controller);
		}
	}
	
	/**
	 * Acciones que se pueden realizar para los datos asociados a un elemento
	 */
	public enum RELATED_ACTION {
		ADD("add"),NEW("new"),DELETE("delete");
		
		private static Map<String, RELATED_ACTION> instances;
		private static void saveInstance(String s,RELATED_ACTION a) {
			if(instances == null) {
				instances = new HashMap<String,RELATED_ACTION>();
			}
			instances.put(s, a);
		}
		private String value;
		public String get() {
			return value;
		}
		private RELATED_ACTION(String s) {
			value = s;
			saveInstance(s, this);
		}
		/**
		 * Obtiene la accion indicada
		 * @param action
		 * @return
		 */
		public static RELATED_ACTION getAction(String action) {
			return instances.get(action);
		}
	}
}
