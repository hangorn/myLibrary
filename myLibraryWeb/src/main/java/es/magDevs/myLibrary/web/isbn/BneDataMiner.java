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

public class BneDataMiner implements IsbnDataMiner {
	private static final String URL_BNE = "http://catalogo.bne.es";
	private static final String URL_ACCESO_INICIAL = URL_BNE+"/uhtbin/webcat";

	private static final String SESSION_COOKIE = "session_number";
	private static final String NAME = "BNE";

	private static final String REGEX_AUTOR = "(("+REGEX_LETRAS+"), )?("+REGEX_LETRAS+")( (\\d*)-(\\d*))?( .+)?";
	private static final String REGEX_PAGINAS = "(\\d+) p\\..*";
	private static final String REGEX_PAGINAS2 = "(\\d+) páginas.*";

	private Pattern patAutor, patPaginas, patPaginas2;
	private String sessionCookie, currentAction;

	public BneDataMiner() throws Exception {
		Response resp = Jsoup.connect(URL_ACCESO_INICIAL).method(Method.GET).execute();
		sessionCookie = resp.cookie(SESSION_COOKIE);
		Document doc = resp.parse();
		Elements searchform = doc.getElementsByClass("searchservices").get(0).getElementsByAttributeValue("name", "searchform");
		currentAction = searchform.attr("action");
		
		patAutor = Pattern.compile(REGEX_AUTOR);
		patPaginas = Pattern.compile(REGEX_PAGINAS);
		patPaginas2 = Pattern.compile(REGEX_PAGINAS2);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<Libro> getData(String isbn) throws Exception {
		
		Document doc = Jsoup.connect(URL_BNE+currentAction)
				.userAgent(USER_AGENT)
				.cookie(SESSION_COOKIE, sessionCookie)
				.data("query_type", "search")
				.data("searchdata1", isbn)
				.data("srchfield1", "NUMEROSBNE^TITLE^GENERAL^Title+Processing^ISBN,+ISSN,+etc.")
//				.data("srchfield1", "GENERAL^SUBJECT^GENERAL^^Todos+los+campos")
				.data("library", "TODAS")
				.data("sort_by", "TI")
				.post();
		
		List<Libro> libros = new ArrayList<>();
		
		Element listaResultados = doc.getElementById("hitlist");
		if (listaResultados != null) {
			currentAction = listaResultados.attr("action");
			// Si hay lista de resultados la recorremos
			for (Element elementoLista : listaResultados.getElementsByClass("hit_list_row")) {
				String nombreElemento = elementoLista.getElementsByClass("hit_list_details_button").get(0).attr("name");
				Document docDetalles = Jsoup.connect(URL_BNE+currentAction)
						.userAgent(USER_AGENT)
						.cookie(SESSION_COOKIE, sessionCookie)
						.data("first_hit", "1")
						.data("last_hit", "30")
						.data("form_type", "")
						.data(nombreElemento, "Detalles")
						.post();
				extraeDatos(libros, docDetalles);
			}
		} else if (doc.getElementById("detail_item_information") != null) {
			// Si no buscamos directamente los datos del libro
			extraeDatos(libros, doc);
		} else if (doc.getElementsByClass("searchsum_container").isEmpty()) {
			throw new Exception("Datos con formato desconocido");
		}

		return libros;
	}

	private void extraeDatos(List<Libro> libros, Document doc) throws Exception {
		Element elementoDetalles = doc.getElementById("detail_item_information").getElementsByTag("dl").get(0);

		Libro libro = new Libro();

		// Titulo
		libro.setTitulo(elementoDetalles.getElementsByClass("title").get(0).text().replaceAll(" \\[.+\\]", ""));
		// Autor
		String textoAutor = elementoDetalles.getElementsByClass("author").get(0).text();
		if (StringUtils.isNotBlank(textoAutor)) {
			Matcher m = patAutor.matcher(textoAutor);
			if (m.matches()) {
				Autor autor = new Autor();
				libro.setAutores(new HashSet<>(1));
				libro.getAutores().add(autor);
				autor.setApellidos(m.group(2));
				autor.setNombre(m.group(3));
				String fnac = m.group(5);
				if (StringUtils.isNotEmpty(fnac)) {
					try {
						autor.setAnnoNacimiento(Integer.valueOf(fnac));
					} catch (NumberFormatException e) {
						throw new Exception("Formato del año de nacimiento del autor desconocido: "+textoAutor);
					}
				}
				String fex = m.group(6);
				if (StringUtils.isNotEmpty(fex)) {
					try {
						autor.setAnnoFallecimiento(Integer.valueOf(fex));
					} catch (NumberFormatException e) {
						throw new Exception("Formato del año de fallec. del autor desconocido: "+textoAutor);
					}
				}
			} else {
				throw new Exception("Formato del dato del autor desconocido: "+textoAutor);
			}
		}
		// Editorial
		String textoEditorial = elementoDetalles.getElementsByClass("publisher").get(0).text();
		if (StringUtils.isNotBlank(textoEditorial)) {
			Editorial editorial = new Editorial();
			libro.setEditorial(editorial);
			editorial.setNombre(textoEditorial);
		}
		// Fh publicacion
		String fhPublicacion = elementoDetalles.getElementsByClass("publishing_date").get(0).text();
		if (StringUtils.isNotBlank(fhPublicacion)) {
			fhPublicacion = fhPublicacion.replaceAll("[\\[\\]]", "").replace("imp. ", "").trim();
			try {
				libro.setAnnoPublicacion(Integer.valueOf(fhPublicacion));
			} catch (NumberFormatException e) {
				throw new Exception("Formato del año de edicion desconocido: "+fhPublicacion);
			}
		}
		// Paginas
		int indexDesc = -1;
		for (int i = 0; i < elementoDetalles.childrenSize(); i++) {
			if (elementoDetalles.children().get(i).text().trim().equals("Descripción física")) {
				indexDesc = i;
				break;
			}
		}
		if (indexDesc >= 0) {
			String textoDesc = elementoDetalles.children().get(indexDesc+1).text();
			Matcher m = patPaginas.matcher(textoDesc);
			Matcher m2 = patPaginas2.matcher(textoDesc);
			boolean isM = m.matches(), isM2 = m2.matches();
			if (isM || isM2) {
				if (isM2) {
					m = m2;
				}
				String paginas = m.group(1);
				try {
					libro.setNumPaginas(Integer.valueOf(paginas));
				} catch (NumberFormatException e) {
					throw new Exception("Formato del numero de paginas desconocido: "+textoDesc);
				}
			} else if (!textoDesc.matches("\\d+ recurso en línea.*")) {
				throw new Exception("Formato de la descripcion con el numero de paginas desconocido: "+textoDesc);
			}
		}
		

		Element elementoRegistro = doc.getElementById("detail_marc_record").getElementsByTag("dl").get(0);
		// ISBN
		indexDesc = -1;
		for (int i = 0; i < elementoRegistro.childrenSize(); i++) {
			if (elementoRegistro.children().get(i).text().trim().equals("ISBN:")) {
				indexDesc = i;
				break;
			}
		}
		if (indexDesc >= 0) {
			String textoIsbn = elementoRegistro.children().get(indexDesc+1).text();
			if (StringUtils.isNotBlank(textoIsbn)) {
				libro.setIsbn(textoIsbn);
			}
		}
		
		libros.add(libro);
	}

}
