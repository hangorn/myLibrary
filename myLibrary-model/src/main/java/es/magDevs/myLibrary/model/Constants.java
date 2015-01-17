package es.magDevs.myLibrary.model;

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
		
		String value;
		Integer order;
		private SECTION(String s, int i) {
			value = s;
			order = i;
		}
		public String get() {
			return value;
		}
		public int getOrder() {
			return order;
		}
	}
}
