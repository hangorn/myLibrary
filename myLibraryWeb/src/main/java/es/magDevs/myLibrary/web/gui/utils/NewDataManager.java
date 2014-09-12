package es.magDevs.myLibrary.web.gui.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.beans.Ubicacion;

/**
 * Realizara diversas operaciones sobre los datos a introducir en la base de
 * datos, como comprobar su validez o rellenar campos necesarios con el valor
 * por defecto
 * 
 * @author javi
 * 
 */
public class NewDataManager {

	/**
	 * Comprueba si estan todos los datos obligatorios y rellena algunos campos
	 * en caso de que esten vacios
	 * 
	 * @param libro
	 * @return {@code true} si los datos son validos
	 */
	public static boolean processBook(Libro libro, MessageSource messageSource) {
		// Si el libro es null o no tenemos titulo los datos no son validos
		if (libro == null || StringUtils.isBlank(libro.getTitulo())) {
			return false;
		}

		// Para los datos relacionados, si no tenemos una relacion valida,
		// ponemos a null su valor para que no de fallos Hibernate
		if (libro.getEditorial() != null
				&& (libro.getEditorial().getId() == null || libro
						.getEditorial().getId() <= 0)) {
			libro.setEditorial(null);
		}
		if (libro.getTipo() != null
				&& (libro.getTipo().getId() == null || libro.getTipo().getId() <= 0)) {
			libro.setTipo(null);
		}
		if (libro.getUbicacion() != null
				&& (libro.getUbicacion().getId() == null || libro
						.getUbicacion().getId() <= 0)) {
			libro.setUbicacion(null);
		}
		if (libro.getColeccion() != null
				&& (libro.getColeccion().getId() == null || libro
						.getColeccion().getId() <= 0)) {
			libro.setColeccion(null);
		}

		// Convertimos todos los textos a mayusculas
		libro.setTitulo(StringUtils.upperCase(libro.getTitulo()));
		libro.setNotas(StringUtils.upperCase(libro.getNotas()));

		// Cortamos los textos por el tamaño maximo
		if (libro.getTitulo().length() > Integer.parseInt(messageSource
				.getMessage("TITLE_MAX_LENGHT", null, null))) {
			libro.setTitulo(StringUtils.substring(libro.getTitulo(), 0, Integer
					.parseInt(messageSource.getMessage("TITLE_MAX_LENGHT",
							null, null))));
		}
		if (libro.getNotas() != null
				&& libro.getNotas().length() > Integer.parseInt(messageSource
						.getMessage("NOTES_MAX_LENGHT", null, null))) {
			libro.setNotas(StringUtils.substring(libro.getNotas(), 0, Integer
					.parseInt(messageSource.getMessage("NOTES_MAX_LENGHT",
							null, null))));
		}
		if (libro.getIsbn() != null
				&& libro.getIsbn().length() > Integer.parseInt(messageSource
						.getMessage("ISBN_MAX_LENGHT", null, null))) {
			libro.setIsbn(StringUtils.substring(libro.getIsbn(), 0, Integer
					.parseInt(messageSource.getMessage("ISBN_MAX_LENGHT", null,
							null))));
		}
		return true;
	}

	/**
	 * Comprueba si estan todos los datos obligatorios y rellena algunos campos
	 * en caso de que esten vacios
	 * 
	 * @param publisher
	 * @return {@code true} si los datos son validos
	 */
	public static boolean processPublisher(Editorial publisher,
			MessageSource messageSource) {
		// Si no tenemos el nombre de la editorial
		if (StringUtils.isBlank(publisher.getNombre())) {
			return false;
		}
		// Convertimos los textos a mayusculas
		publisher.setNombre(StringUtils.upperCase(publisher.getNombre()));
		publisher.setCiudad(StringUtils.upperCase(publisher.getCiudad()));

		// Cortamos los textos por el tamaño maximo
		if (publisher.getNombre().length() > Integer.parseInt(messageSource
				.getMessage("TEXT_MAX_LENGHT", null, null))) {
			publisher.setNombre(StringUtils.substring(publisher.getNombre(), 0,
					Integer.parseInt(messageSource.getMessage(
							"TEXT_MAX_LENGHT", null, null))));
		}
		if (publisher.getCiudad() != null
				&& publisher.getCiudad().length() > Integer
						.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT",
								null, null))) {
			publisher.setCiudad(StringUtils.substring(publisher.getCiudad(), 0,
					Integer.parseInt(messageSource.getMessage(
							"TEXT_MAX_LENGHT", null, null))));
		}
		return true;
	}

	/**
	 * Comprueba si estan todos los datos obligatorios y rellena algunos campos
	 * en caso de que esten vacios
	 * 
	 * @param collection
	 * @return {@code true} si los datos son validos
	 */
	public static boolean processCollection(Coleccion collection,
			MessageSource messageSource) {
		// Si no tenemos el nombre de la coleccion
		if (StringUtils.isBlank(collection.getNombre())) {
			return false;
		}
		// Convertimos los textos a mayusculas
		collection.setNombre(StringUtils.upperCase(collection.getNombre()));

		// Para los datos relacionados, si no tenemos una relacion valida,
		// ponemos a null su valor para que no de fallos Hibernate
		if (collection.getEditorial() != null
				&& (collection.getEditorial().getId() == null || collection
						.getEditorial().getId() <= 0)) {
			collection.setEditorial(null);
		}
		// Cortamos los textos por el tamaño maximo
		if (collection.getNombre().length() > Integer.parseInt(messageSource
				.getMessage("TEXT_MAX_LENGHT", null, null))) {
			collection.setNombre(StringUtils.substring(collection.getNombre(),
					0, Integer.parseInt(messageSource.getMessage(
							"TEXT_MAX_LENGHT", null, null))));
		}
		return true;
	}

	/**
	 * Comprueba si estan todos los datos obligatorios y rellena algunos campos
	 * en caso de que esten vacios
	 * 
	 * @param author
	 * @return {@code true} si los datos son validos
	 */
	public static boolean processAuthor(Autor author,
			MessageSource messageSource) {
		if (StringUtils.isBlank(author.getApellidos())) {
			return false;
		}
		// Convertimos los textos a mayusculas
		author.setNombre(StringUtils.upperCase(author.getNombre()));
		author.setApellidos(StringUtils.upperCase(author.getApellidos()));
		author.setPais(StringUtils.upperCase(author.getPais()));
		author.setCiudad(StringUtils.upperCase(author.getCiudad()));
		// Cortamos los textos por el tamaño maximo
		if (author.getNombre() != null
				&& author.getNombre().length() > Integer.parseInt(messageSource
						.getMessage("TITLE_MAX_LENGHT", null, null))) {
			author.setNombre(StringUtils.substring(author.getNombre(), 0,
					Integer.parseInt(messageSource.getMessage(
							"TITLE_MAX_LENGHT", null, null))));
		}
		if (author.getApellidos().length() > Integer.parseInt(messageSource
				.getMessage("TITLE_MAX_LENGHT", null, null))) {
			author.setApellidos(StringUtils.substring(author.getApellidos(), 0,
					Integer.parseInt(messageSource.getMessage(
							"TITLE_MAX_LENGHT", null, null))));
		}
		if (author.getPais() != null
				&& author.getPais().length() > Integer.parseInt(messageSource
						.getMessage("TITLE_MAX_LENGHT", null, null))) {
			author.setPais(StringUtils.substring(author.getPais(), 0, Integer
					.parseInt(messageSource.getMessage("TITLE_MAX_LENGHT",
							null, null))));
		}
		if (author.getCiudad() != null
				&& author.getCiudad().length() > Integer.parseInt(messageSource
						.getMessage("TITLE_MAX_LENGHT", null, null))) {
			author.setCiudad(StringUtils.substring(author.getCiudad(), 0,
					Integer.parseInt(messageSource.getMessage(
							"TITLE_MAX_LENGHT", null, null))));
		}
		return true;
	}

	/**
	 * Comprueba si estan todos los datos obligatorios y rellena algunos campos
	 * en caso de que esten vacios
	 * 
	 * @param translator
	 * @return {@code true} si los datos son validos
	 */
	public static boolean processTranslator(Traductor translator,
			MessageSource messageSource) {
		if (StringUtils.isBlank(translator.getNombre())) {
			return false;
		}
		// Convertimos los textos a mayusculas
		translator.setNombre(StringUtils.upperCase(translator.getNombre()));
		// Cortamos los textos por el tamaño maximo
		if (translator.getNombre().length() > Integer.parseInt(messageSource
				.getMessage("TITLE_MAX_LENGHT", null, null))) {
			translator.setNombre(StringUtils.substring(translator.getNombre(),
					0, Integer.parseInt(messageSource.getMessage(
							"TITLE_MAX_LENGHT", null, null))));
		}

		return true;
	}

	/**
	 * Comprueba si estan todos los datos obligatorios y rellena algunos campos
	 * en caso de que esten vacios
	 * 
	 * @param type
	 * @return {@code true} si los datos son validos
	 */
	public static boolean processType(Tipo type,
			MessageSource messageSource) {
		if (StringUtils.isBlank(type.getDescripcion())) {
			return false;
		}
		// Convertimos los textos a mayusculas
		type.setDescripcion(StringUtils.upperCase(type.getDescripcion()));
		// Cortamos los textos por el tamaño maximo
		if (type.getDescripcion().length() > Integer.parseInt(messageSource
				.getMessage("TITLE_MAX_LENGHT", null, null))) {
			type.setDescripcion(StringUtils.substring(type.getDescripcion(),
					0, Integer.parseInt(messageSource.getMessage(
							"TITLE_MAX_LENGHT", null, null))));
		}

		return true;
	}

	/**
	 * Comprueba si estan todos los datos obligatorios y rellena algunos campos
	 * en caso de que esten vacios
	 * 
	 * @param place
	 * @return {@code true} si los datos son validos
	 */
	public static boolean processPlace(Ubicacion place,
			MessageSource messageSource) {
		if (StringUtils.isBlank(place.getCodigo())) {
			return false;
		}
		// Convertimos los textos a mayusculas
		place.setDescripcion(StringUtils.upperCase(place.getDescripcion()));
		place.setCodigo(StringUtils.upperCase(place.getCodigo()));
		// Cortamos los textos por el tamaño maximo
		if (place.getDescripcion().length() > Integer.parseInt(messageSource
				.getMessage("TITLE_MAX_LENGHT", null, null))) {
			place.setDescripcion(StringUtils.substring(place.getDescripcion(),
					0, Integer.parseInt(messageSource.getMessage(
							"TITLE_MAX_LENGHT", null, null))));
		}
		if (place.getCodigo().length() > Integer.parseInt(messageSource
				.getMessage("COD_MAX_LENGHT", null, null))) {
			place.setCodigo(StringUtils.substring(place.getCodigo(),
					0, Integer.parseInt(messageSource.getMessage(
							"COD_MAX_LENGHT", null, null))));
		}

		return true;
	}
}
