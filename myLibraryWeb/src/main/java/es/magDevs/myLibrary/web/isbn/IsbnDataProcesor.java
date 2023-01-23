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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.dao.AutorDao;
import es.magDevs.myLibrary.model.dao.EditorialDao;
import es.magDevs.myLibrary.model.dao.TraductorDao;

public class IsbnDataProcesor {

	public IsbnDataProcesor() {
	}

	public Libro getData(String isbn) throws Exception {
		IsbnDataMiner ministerioDataMiner = new MinisterioDataMiner();
		IsbnDataMiner bneDataMiner = new BneDataMiner();
		
		List<Libro> libros = ministerioDataMiner.getData(isbn);
		libros.addAll(bneDataMiner.getData(isbn));
		libros.addAll(new IberDataMiner().getData(isbn));
		
		if (libros.isEmpty()) {
			return null;
		}
		// Primero rellenamos los datos de un libro con todos los datos que hemos obtenido
		Libro libro = new Libro();
		List<Editorial> editoriales = new ArrayList<>();
		for (Libro l : libros) {
			// Titulo
			if (StringUtils.isEmpty(libro.getTitulo()) && StringUtils.isNotEmpty(l.getTitulo())) {
				libro.setTitulo(sanitizeData(l.getTitulo()));
			}
			// Autor
			if (l.getAutores() != null) {
				for (Autor autor : l.getAutores()) {
					autor.setNombre(sanitizeData(autor.getNombre()));
					autor.setApellidos(sanitizeData(autor.getApellidos()));
					if (StringUtils.isBlank(autor.getApellidos()) && StringUtils.isNotBlank(autor.getNombre())) {
						autor.setApellidos(autor.getNombre());
						autor.setNombre(null);
					}
					if (libro.getAutores() == null) {
						// Si el libro no tiene autores lo añadimos directamente
						libro.setAutores(new HashSet<>());
						libro.getAutores().add(autor);
					} else {
						// Si el libro tiene autores, buscamos a ver si ya esta añadido
						boolean existeAutor = false;
						for (Autor a : libro.getAutores()) {
							if (Objects.equals(a.getNombre(), autor.getNombre()) &&
									Objects.equals(a.getApellidos(), autor.getApellidos()) &&
									Objects.equals(a.getAnnoNacimiento(), autor.getAnnoNacimiento())) {
								existeAutor = true;
								break;
							}
						}
						if (!existeAutor) {
							libro.getAutores().add(autor);
						}
					}
				}
			}
			
			// Traductor
			if (l.getTraductores() != null) {
				for (Traductor traductor : l.getTraductores()) {
					traductor.setNombre(sanitizeData(traductor.getNombre()));
					if (libro.getTraductores() == null) {
						// Si el libro no tiene traductores lo añadimos directamente
						libro.setTraductores(new HashSet<>());
						libro.getTraductores().add(traductor);
					} else {
						// Si el libro tiene traductores, buscamos a ver si ya esta añadido
						boolean existeTraductor = false;
						for (Traductor t : libro.getTraductores()) {
							if (Objects.equals(t.getNombre(), traductor.getNombre())) {
								existeTraductor = true;
								break;
							}
						}
						if (!existeTraductor) {
							libro.getTraductores().add(traductor);
						}
					}
				}
			}
			// Fh publicacion
			if (libro.getAnnoPublicacion() == null && l.getAnnoPublicacion() != null) {
				libro.setAnnoPublicacion(l.getAnnoPublicacion());
			}
			// Num pag
			if (libro.getNumPaginas() == null && l.getNumPaginas() != null) {
				libro.setNumPaginas(l.getNumPaginas());
			}
			// Precio
			if (libro.getPrecio() == null && l.getPrecio() != null) {
				libro.setPrecio(l.getPrecio());
			}
			// Editorial
			if (l.getEditorial() != null) {
				editoriales.add(l.getEditorial());
				l.getEditorial().setNombre(sanitizeData(l.getEditorial().getNombre()));
			}
			// ISBN
			if (StringUtils.isEmpty(libro.getIsbn()) && StringUtils.isNotEmpty(l.getIsbn())) {
				String isbnLibro = sanitizeData(l.getIsbn());
				if (isbnLibro.startsWith("978-")) {
					isbnLibro = isbnLibro.replaceFirst("978-", "");
				}
				libro.setIsbn(isbnLibro);
			}
		}
		
		linkData(libro, editoriales);
		libro.setCb(isbn);
		
		return libro;
	}

	private static String sanitizeData(String data) {
		if (data == null) {
			return null;
		}
		data = data.toUpperCase();
		return data.replaceAll("\\s+", " ").replaceAll("[ÁÀÂ]", "A").replaceAll("[ÉÈÊ]", "E").replaceAll("[ÍÌÎ]", "I").replaceAll("[ÓÒÔ]", "O").replaceAll("[ÚÙÛ]", "U").trim();
	}
	
	@SuppressWarnings("unchecked")
	private void linkData(Libro libro, List<Editorial> editoriales) throws Exception {
		// Autor
		if (libro.getAutores() != null) {
			Set<Autor> autores = new HashSet<>();
			Set<Integer> idAutores = new HashSet<>();
			AutorDao dao = DaoFactory.getAutorDao();
			for (Autor autor : libro.getAutores()) {
				Autor autorQry = new Autor();
				autorQry.setNombreExacto(autor.getNombre());
				autorQry.setApellidosExacto(autor.getApellidos());
				List<Autor> autoresBBDD = dao.getWithPag(autorQry, 0, 3);
				if (autoresBBDD.size() == 1) {
					Autor autorBBDD = autoresBBDD.get(0);
					if (!idAutores.contains(autorBBDD.getId())) {
						autores.add(autorBBDD);
						idAutores.add(autorBBDD.getId());
					}
				} else if (autoresBBDD.size() > 1) {
					for (Autor autorBBDD : autoresBBDD) {
						if (Objects.equals(autorBBDD.getAnnoNacimiento(), autor.getAnnoNacimiento())) {
							autores.add(autorBBDD);
							idAutores.add(autorBBDD.getId());
							break;
						}
					}
				}
			}
			libro.setAutores(autores);
		}
		// Traductor
		if (libro.getTraductores() != null) {
			Set<Traductor> traductores = new HashSet<>();
			Set<Integer> idTraductores = new HashSet<>();
			TraductorDao dao = DaoFactory.getTraductorDao();
			for (Traductor traductor : libro.getTraductores()) {
				Traductor traductorQry = new Traductor();
				traductorQry.setNombreExacto(traductor.getNombre());
				List<Traductor> traductoresBBDD = dao.getWithPag(traductorQry, 0, 3);
				if (!traductoresBBDD.isEmpty()) {
					Traductor traductorBBDD = traductoresBBDD.get(0);
					if (!idTraductores.contains(traductorBBDD.getId())) {
						traductores.add(traductorBBDD);
						idTraductores.add(traductorBBDD.getId());
					}
				}
			}
			libro.setTraductores(traductores);
		}
		// Editorial
		EditorialDao dao = DaoFactory.getEditorialDao();
		for (Editorial e : editoriales) {
			if (libro.getEditorial() == null) {
				Editorial editorialQry = new Editorial();
				Editorial ed = buscaEditorial(e.getNombre(), editorialQry, dao);
				if (ed != null) {
					libro.setEditorial(ed);
					break;
				}
			}
		}
	}
	
	private static final List<String> TXTS_REPLACE = Arrays.asList("", "EDITORIALES","EDITORIAL", "EDICIONES", "EDITORES");
	@SuppressWarnings("unchecked")
	private Editorial buscaEditorial(String nombreEd, Editorial editorialQry, EditorialDao dao) throws Exception {
		for (String txt : TXTS_REPLACE) {
			if (txt.isEmpty() || nombreEd.contains(txt)) {
				editorialQry.setNombreExacto(txt.replaceAll(txt, "").trim());
				List<Editorial> editorialesBBDD = dao.getWithPag(editorialQry, 0, 3);
				if (editorialesBBDD.size() == 1) {
					return editorialesBBDD.get(0);
				}
			}
		}
		return null;
	}

}
