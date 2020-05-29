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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Prestamo;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.beans.Usuario;

/**
 * Realizara diversas operaciones sobre los datos a introducir en la base de
 * datos, como comprobar su validez o rellenar campos necesarios con el valor
 * por defecto
 * 
 * @author javier.vaquero
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
		// Si el libro es null los datos no son validos
		if (libro == null) {
			return false;
		}
		
		// Si el libro tiene una coleccion pero no una editorial, los datos no
		// son validos
		if (libro.getColeccion() != null
				&& libro.getColeccion().getId() != null
				&& libro.getColeccion().getId() > 0
				&& (libro.getEditorial() == null
						|| libro.getEditorial().getId() == null || libro
						.getEditorial().getId() <= 0)) {
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
		
		// Si no tenemos titulo los datos no son validos
		if (StringUtils.isBlank(libro.getTitulo())) {
			return false;
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
		
		// Convertimos datos vacios a null
		if(StringUtils.isBlank(libro.getIsbn())) {
			libro.setIsbn(null);
		}
		if(StringUtils.isBlank(libro.getNotas())) {
			libro.setNotas(null);
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
		// Convertimos los textos a mayusculas
		publisher.setNombre(StringUtils.upperCase(publisher.getNombre()));
		publisher.setCiudad(StringUtils.upperCase(publisher.getCiudad()));

		// Cortamos los textos por el tamaño maximo
		if (publisher.getNombre() != null && publisher.getNombre().length() > Integer.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))) {
			publisher.setNombre(StringUtils.substring(publisher.getNombre(), 0, Integer.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))));
		}
		if (publisher.getCiudad() != null && publisher.getCiudad().length() > Integer.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))) {
			publisher.setCiudad(StringUtils.substring(publisher.getCiudad(), 0, Integer.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))));
		}

		// Convertimos datos vacios a null
		if(StringUtils.isBlank(publisher.getCiudad())) {
			publisher.setCiudad(null);
		}
		// Si no tenemos el nombre de la editorial
		if (StringUtils.isBlank(publisher.getNombre())) {
			return false;
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
		// Convertimos los textos a mayusculas
		collection.setNombre(StringUtils.upperCase(collection.getNombre()));

		// Para los datos relacionados, si no tenemos una relacion valida,
		// ponemos a null su valor para que no de fallos Hibernate
		if (collection.getEditorial() != null && (collection.getEditorial().getId() == null || collection.getEditorial().getId() <= 0)) {
			collection.setEditorial(null);
		}
		// Cortamos los textos por el tamaño maximo
		if (collection.getNombre() != null && collection.getNombre().length() > Integer.parseInt(messageSource
				.getMessage("TEXT_MAX_LENGHT", null, null))) {
			collection.setNombre(StringUtils.substring(collection.getNombre(),
					0, Integer.parseInt(messageSource.getMessage(
							"TEXT_MAX_LENGHT", null, null))));
		}
		// Si no tenemos el nombre de la coleccion
		if (StringUtils.isBlank(collection.getNombre())) {
			return false;
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
		// Convertimos los textos a mayusculas
		author.setNombre(StringUtils.upperCase(author.getNombre()));
		author.setApellidos(StringUtils.upperCase(author.getApellidos()));
		author.setPais(StringUtils.upperCase(author.getPais()));
		author.setCiudad(StringUtils.upperCase(author.getCiudad()));
		author.setNotas(StringUtils.upperCase(author.getNotas()));
		// Cortamos los textos por el tamaño maximo
		if (author.getNombre() != null && author.getNombre().length() > Integer
				.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))) {
			author.setNombre(StringUtils.substring(author.getNombre(), 0,
					Integer.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))));
		}
		if (author.getApellidos() != null && author.getApellidos().length() > Integer
				.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))) {
			author.setApellidos(StringUtils.substring(author.getApellidos(), 0,
					Integer.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))));
		}
		if (author.getPais() != null && author.getPais().length() > Integer
				.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))) {
			author.setPais(StringUtils.substring(author.getPais(), 0,
					Integer.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))));
		}
		if (author.getCiudad() != null && author.getCiudad().length() > Integer
				.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))) {
			author.setCiudad(StringUtils.substring(author.getCiudad(), 0,
					Integer.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))));
		}
		if (author.getNotas() != null && author.getNotas().length() > Integer
				.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))) {
			author.setNotas(StringUtils.substring(author.getNotas(), 0,
					Integer.parseInt(messageSource.getMessage("TEXT_MAX_LENGHT", null, null))));
		}

		// Convertimos datos vacios a null
		if (StringUtils.isBlank(author.getNombre())) {
			author.setNombre(null);
		}
		if (StringUtils.isBlank(author.getPais())) {
			author.setPais(null);
		}
		if (StringUtils.isBlank(author.getCiudad())) {
			author.setCiudad(null);
		}
		if (StringUtils.isBlank(author.getNotas())) {
			author.setNotas(null);
		}

		if (StringUtils.isBlank(author.getApellidos())) {
			return false;
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
	public static boolean processType(Tipo type, MessageSource messageSource) {
		if (StringUtils.isBlank(type.getDescripcion())) {
			return false;
		}
		// Convertimos los textos a mayusculas
		type.setDescripcion(StringUtils.upperCase(type.getDescripcion()));
		// Cortamos los textos por el tamaño maximo
		if (type.getDescripcion().length() > Integer.parseInt(messageSource
				.getMessage("TITLE_MAX_LENGHT", null, null))) {
			type.setDescripcion(StringUtils.substring(type.getDescripcion(), 0,
					Integer.parseInt(messageSource.getMessage(
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
		if (place.getDescripcion() != null && place.getDescripcion().length() > Integer.parseInt(messageSource
				.getMessage("TITLE_MAX_LENGHT", null, null))) {
			place.setDescripcion(StringUtils.substring(place.getDescripcion(),
					0, Integer.parseInt(messageSource.getMessage(
							"TITLE_MAX_LENGHT", null, null))));
		}
		if (place.getCodigo().length() > Integer.parseInt(messageSource
				.getMessage("COD_MAX_LENGHT", null, null))) {
			place.setCodigo(StringUtils.substring(place.getCodigo(), 0, Integer
					.parseInt(messageSource.getMessage("COD_MAX_LENGHT", null,
							null))));
		}

		// Convertimos datos vacios a null
		if (StringUtils.isBlank(place.getDescripcion())) {
			place.setDescripcion(null);
		}

		return true;
	}

	public static boolean processLend(Prestamo lend, MessageSource messageSource) {
		if (lend.getUsuario() == null || lend.getUsuario().getUsername() == null ||
				lend.getLibro() == null || lend.getLibro().getId() == null) {
			return false;
		}
		if (StringUtils.isBlank(lend.getFecha())) {
			lend.setFecha(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		}
		return true;
	}

	public static boolean processUser(Usuario newData, MessageSource messageSource) {
		if (StringUtils.isBlank(newData.getNombre()) && StringUtils.isBlank(newData.getUsername())) {
			return false;
		}
		if (StringUtils.isBlank(newData.getUsername())) {
			newData.setUsername(StringUtils.substring(newData.getNombre().toLowerCase().replaceAll(" ", ""), 0, 50));
		}
		if (StringUtils.isBlank(newData.getNombre())) {
			newData.setNombre(newData.getUsername());
		}
		if (StringUtils.isBlank(newData.getPassword())) {
			newData.setPassword("-1");
		}
		if (newData.getNombre() != null && newData.getNombre().length() > 60) {
			newData.setNombre(StringUtils.substring(newData.getNombre(), 0, 60));
		}
		if (newData.getUsername() != null && newData.getUsername().length() > 50) {
			newData.setUsername(StringUtils.substring(newData.getUsername(), 0, 50));
		}
		if (newData.getEmail() != null && newData.getEmail().length() > 150) {
			newData.setEmail(StringUtils.substring(newData.getEmail(), 0, 50));
		}
		return true;
	}
}
