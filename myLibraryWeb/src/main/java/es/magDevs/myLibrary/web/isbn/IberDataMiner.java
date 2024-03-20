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
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;

public class IberDataMiner implements IsbnDataMiner {
	private static final String URL_IBER = "https://www.iberlibro.com";
	private static final String URL_CONSULTA = URL_IBER+"/servlet/SearchResults?cm_sp=SearchF-_-isbn-_-Results&isbn=";

	private static final String NAME = "IBER";
	private static final String SESSION_COOKIE = "session-id";

	private static final String REGEX_AUTOR = "(("+REGEX_LETRAS+"), *)?("+REGEX_LETRAS+")( \\(\\d{4}-\\d{4}\\))?";
	private static final String REGEX_EDITORIAL = "\\[?("+REGEX_LETRAS+")(,+ "+REGEX_LETRAS+")?(,+ "+REGEX_LETRAS+")?(.?,* (\\d{2}/\\d{2}/)?(\\d{4}))?(, \\[?"+REGEX_LETRAS+"\\]?(, \\d{4})?)?";
	private static final String REGEX_PAGINAS = ".* (\\d+) p\\..*";

	private Pattern patAutor, patEditorial, patPaginas;
	private String sessionCookie;

	public IberDataMiner() {
		
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
		Thread.sleep(3000);
		Document doc = hazConsulta(URL_CONSULTA + isbn);
		
		List<Libro> libros = new ArrayList<>();

		Element listaResultados = doc.getElementById("srp-results");
		if (listaResultados == null) {
			Thread.sleep(2000);
			if (isbn.contains("-")) {
				isbn = "978-" + isbn;
			} else {
				isbn = "978" + isbn;
			}
			doc = hazConsulta(URL_CONSULTA + isbn);
			listaResultados = doc.getElementById("srp-results");
		}
		Thread.sleep(2000);
		if (listaResultados != null) {
			Elements elementosLista = listaResultados.getElementsByTag("li");
			if (!elementosLista.isEmpty()) {
				elementosLista = elementosLista.get(0).getElementsByClass("result-item");
				if (!elementosLista.isEmpty()) {
					// Nos quedamos con el primer libro que encontremos
					String url = elementosLista.get(0).getElementsByClass("result-detail").get(0).getElementsByTag("a").get(0).attr("href");
					Document docDetalles = hazConsulta(URL_IBER+url);
					extraeDatos(isbn, libros, docDetalles);
				}
			}
		} else {
			handleError(isbn, new Exception("Datos con formato desconocido"));
		}

		return libros;
	}
	
	private Document hazConsulta(String url) throws Exception {
		try {
			Document doc;
			if (sessionCookie == null) {
				Response resp = Jsoup.connect(url).userAgent(USER_AGENT).method(Method.GET).cookie("abe_vc", "1").cookie("CMOptout", "opt_out").execute();
				sessionCookie = resp.cookie(SESSION_COOKIE);
				doc = resp.parse();
			} else {
				doc = Jsoup.connect(url).cookie(SESSION_COOKIE, sessionCookie).cookie("ql-"+SESSION_COOKIE, sessionCookie).cookie("wl-"+SESSION_COOKIE, sessionCookie)
						.cookie("abe_vc", "1").cookie("CMOptout", "opt_out").userAgent(USER_AGENT).get();
			}
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			Thread.sleep(150000);
			Response resp = Jsoup.connect(url).userAgent(USER_AGENT).method(Method.GET).cookie("abe_vc", "1").cookie("CMOptout", "opt_out").execute();
			sessionCookie = resp.cookie(SESSION_COOKIE);
			return resp.parse();
		}
	}

	private void extraeDatos(String isbn, List<Libro> libros, Document doc) {

		Libro libro = new Libro();

		// Titulo
		libro.setTitulo(doc.getElementById("book-title").text());
		// Autor
		Element elementAutor = doc.getElementById("book-author");
		if (elementAutor != null) {
			String textoAutor = elementAutor.text();
			
			if (textoAutor.contains(". traducción:")) {
				String[] splitted = textoAutor.split(". traducción:");
				textoAutor = splitted[0];
				if (libro.getTraductores() == null) {
					libro.setTraductores(new HashSet<>());
				}
				libro.getTraductores().add(new Traductor(null, splitted[1]));
			}
			
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
					String textoFhPub = m.group(6);
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
		// Ya no hay informacion de las paginas...
//		String txtDescripcion;
//		Element elementProduct = doc.getElementById("product");
//		if (elementProduct != null) {
//			txtDescripcion = elementProduct.getElementsByClass("detail-section").get(0).children().stream().filter(e -> e.tagName().equals("div")).findFirst(). get().ownText();
//		} else {
//			txtDescripcion = doc.getElementById("Description-heading").text();
//		}
//		if (StringUtils.isNotBlank(txtDescripcion)) {
//			Matcher m = patPaginas.matcher(txtDescripcion);
//			if (m.matches()) {
//				try {
//					libro.setNumPaginas(Integer.valueOf(m.group(1)));
//				} catch (NumberFormatException e) {
//					handleError(isbn, new Exception("Formato del numero de paginas desconocido: "+txtDescripcion));
//				}
//			}
//		}
		
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
