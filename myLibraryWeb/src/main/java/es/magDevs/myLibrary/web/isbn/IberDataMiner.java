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
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;

public class IberDataMiner implements IsbnDataMiner {
	private static final String URL_IBER = "https://www.iberlibro.com";
	private static final String URL_CONSULTA = URL_IBER+"/servlet/SearchResults?cm_sp=SearchF-_-isbn-_-Results&isbn=";

	private static final String NAME = "IBER";

	private static final String REGEX_AUTOR = "(("+REGEX_LETRAS+"), )?("+REGEX_LETRAS+")";
	private static final String REGEX_EDITORIAL = "("+REGEX_LETRAS+")(, "+REGEX_LETRAS+")?(, (\\d{4}))?";
	private static final String REGEX_PAGINAS = ".* (\\d+) p\\..*";

	private Pattern patAutor, patEditorial, patPaginas;

	public IberDataMiner() throws Exception {
		
		patAutor = Pattern.compile(REGEX_AUTOR);
		patEditorial = Pattern.compile(REGEX_EDITORIAL);
		patPaginas = Pattern.compile(REGEX_PAGINAS);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<Libro> getData(String isbn) throws Exception {
		
		Document doc = Jsoup.connect(URL_CONSULTA + isbn).userAgent(USER_AGENT).get();
		
		List<Libro> libros = new ArrayList<>();
		
		Element listaResultados = doc.getElementById("srp-results");
		if (listaResultados == null) {
			if (isbn.contains("-")) {
				isbn = "978-" + isbn;
			} else {
				isbn = "978" + isbn;
			}
			doc = Jsoup.connect(URL_CONSULTA + isbn).userAgent(USER_AGENT).get();
			listaResultados = doc.getElementById("srp-results");
		}
		if (listaResultados != null) {
			Elements elementosLista = listaResultados.getElementsByTag("li");
			if (!elementosLista.isEmpty()) {
				elementosLista = elementosLista.get(0).getElementsByClass("result-item");
				if (!elementosLista.isEmpty()) {
					// Nos quedamos con el primer libro que encontremos
					String url = elementosLista.get(0).getElementsByClass("result-detail").get(0).getElementsByTag("a").get(0).attr("href");
					Document docDetalles = Jsoup.connect(URL_IBER+url).userAgent(USER_AGENT).get();
					extraeDatos(isbn, libros, docDetalles);
				}
			}
		} else {
			handleError(isbn, new Exception("Datos con formato desconocido"));
		}

		return libros;
	}

	private void extraeDatos(String isbn, List<Libro> libros, Document doc) {

		Libro libro = new Libro();

		// Titulo
		libro.setTitulo(doc.getElementById("book-title").text());
		// Autor
		Element elementAutor = doc.getElementById("book-author");
		if (elementAutor != null) {
			String textoAutor = elementAutor.text();
			if (StringUtils.isNotBlank(textoAutor)) {
				Matcher m = patAutor.matcher(textoAutor);
				if (m.matches()) {
					Autor autor = new Autor();
					libro.setAutores(new HashSet<>(1));
					libro.getAutores().add(autor);
					autor.setApellidos(m.group(2));
					autor.setNombre(m.group(3));
				} else if (textoAutor.contains("|") || textoAutor.contains(";") || textoAutor.contains("/") || StringUtils.countMatches(textoAutor, ",") > 1) {
					int i = 0;
					String regex = "[\\|;/]+";
					if (StringUtils.countMatches(textoAutor, ",") > 1) {
						regex = "[\\|;,/]+";
					}
					for (String splitted : textoAutor.split(regex)) {
						if (StringUtils.isNotBlank(splitted)) {
							m = patAutor.matcher(splitted);
							if (m.matches()) {
								if (libro.getAutores() == null) {
									libro.setAutores(new HashSet<>());
								}
								Autor autor = new Autor();
								autor.setId(i++);
								autor.setApellidos(m.group(2));
								autor.setNombre(m.group(3));
								libro.getAutores().add(autor);
							} else {
								handleError(isbn, new Exception("Formato del dato del autor desconocido: "+splitted));
							}
						}
					}
				} else {
					handleError(isbn, new Exception("Formato del dato del autor desconocido: "+textoAutor));
				}
			}
		}
		// Editorial
		Element elementoEditorial = doc.getElementById("book-publisher");
		if (elementoEditorial != null) {
			String textoEditorial = elementoEditorial.text();
			if (StringUtils.isNotBlank(textoEditorial)) {
				Matcher m = patEditorial.matcher(textoEditorial);
				if (m.matches()) {
					Editorial editorial = new Editorial();
					libro.setEditorial(editorial);
					editorial.setNombre(m.group(1));
					// Fh publicacion
					String textoFhPub = m.group(4);
					if (StringUtils.isNotBlank(textoFhPub)) {
						try {
							libro.setAnnoPublicacion(Integer.valueOf(textoFhPub));
						} catch (NumberFormatException e) {
							handleError(isbn, new Exception("Formato del año de edicion desconocido: " + textoEditorial));
						}
					}
				} else {
					handleError(isbn, new Exception("Formato del dato de la editorial desconocido: "+textoEditorial));
				}
			} 
		}
		// Paginas
		String txtDescripcion = doc.getElementById("product").getElementsByClass("detail-section").get(0).children().stream().filter(e->e.tagName().equals("div")).findFirst().get().ownText();
		if (StringUtils.isNotBlank(txtDescripcion)) {
			Matcher m = patPaginas.matcher(txtDescripcion);
			if (m.matches()) {
				try {
					libro.setNumPaginas(Integer.valueOf(m.group(1)));
				} catch (NumberFormatException e) {
					handleError(isbn, new Exception("Formato del numero de paginas desconocido: "+txtDescripcion));
				}
			}
		}
		
		// ISBN
		Element elementoIsbn = doc.getElementById("isbn");
		if (elementoIsbn != null) {
			Elements elementosIsbn = elementoIsbn.getElementsByTag("a");
			if (!elementosIsbn.isEmpty()) {
				libro.setIsbn(elementosIsbn.get(1).text());
			}
		}
		
		libros.add(libro);
	}

}
