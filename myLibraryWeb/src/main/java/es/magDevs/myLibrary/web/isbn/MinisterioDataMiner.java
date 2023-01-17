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

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;

public class MinisterioDataMiner implements IsbnDataMiner {
	private static final String URL_MINISTERIO = "https://www.culturaydeporte.gob.es";
	private static final String URL_ACCESO_INICIAL = URL_MINISTERIO+"/webISBN/tituloSimpleFilter.do?cache=init&prev_layout=busquedaisbn&layout=busquedaisbn&language=es";

	private static final String REGEX_AUTOR = "(("+REGEX_LETRAS+"), )?("+REGEX_LETRAS+") (\\(+(\\d*( a.C.)?)-(\\d*( a.C.)?)\\)+)?(;.+)?";
	private static final String REGEX_PRECIO = "([0-9.]+) Euros";
	private static final String REGEX_PAGINAS = "(\\d+) p\\..*";
	private static final String REGEX_FH_EDIC = "\\d{2}/(\\d{4})";
	
	private static final String SESSION_COOKIE = "JSESSIONID";
	private static final String NAME = "MINISTERIO";
	
	private SSLSocketFactory sslSocketFactory;
	private String sessionCookie;
	private Pattern patAutor, patFhEd, patPaginas, patPrecio;
	
	public MinisterioDataMiner() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
		SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());
		sslSocketFactory = sslContext.getSocketFactory();
		
		Response resp = Jsoup.connect(URL_ACCESO_INICIAL).sslSocketFactory(sslSocketFactory).execute();
		sessionCookie = resp.cookie(SESSION_COOKIE);
		
		patAutor = Pattern.compile(REGEX_AUTOR);
		patFhEd = Pattern.compile(REGEX_FH_EDIC);
		patPaginas = Pattern.compile(REGEX_PAGINAS);
		patPrecio = Pattern.compile(REGEX_PRECIO);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<Libro> getData(String isbn) throws Exception {
		Document doc = Jsoup.connect("https://www.culturaydeporte.gob.es/webISBN/tituloSimpleDispatch.do")
							.sslSocketFactory(sslSocketFactory)
							.userAgent(USER_AGENT)
							.cookie(SESSION_COOKIE, sessionCookie)
							.timeout(3000)
							.data("params.forzaQuery", "N")
							.data("params.cdispo", "A")
							.data("params.cisbnExt", isbn)
							.data("params.orderByFormId", "1")
							.data("action", "Buscar")
							.data("language", "es")
							.data("prev_layout", "busquedaisbn")
							.data("layout", "busquedaisbn")
							.post();
			
		Element listaLibros = doc.getElementById("tituloListaForm");
		
		List<Libro> libros = new ArrayList<>();
		
		if (listaLibros != null) {
			for (Element filaLista : listaLibros.getElementsByAttributeValueStarting("class", "isbnResultado")) {
				String urlDetalles = filaLista.getElementsByClass("camposCheck").get(0).getElementsByTag("a").get(0).attr("href");
				Document docDetalles = Jsoup.connect("https://www.culturaydeporte.gob.es"+urlDetalles)
											.sslSocketFactory(sslSocketFactory)
											.userAgent(USER_AGENT)
											.cookie(SESSION_COOKIE, sessionCookie).get();
				
				Libro libro = new Libro();
				libros.add(libro);
				
				Element ficha = docDetalles.getElementsByClass("fichaISBN").get(0);
				libro.setIsbn(ficha.getElementsByClass("cabecera").get(0).getElementsByTag("strong").get(0).text());
				
				Element tablaDatos = docDetalles.getElementsByTag("table").get(0);
				
				for (Element filaDato : tablaDatos.getElementsByTag("tr")) {
					String cabeceraDato = filaDato.getElementsByTag("th").get(0).text();
					if (cabeceraDato.equals("Título:")) {
						libro.setTitulo(filaDato.getElementsByTag("td").text());
					} else if (cabeceraDato.equals("Autor/es:")) {
						libro.setAutores(new HashSet<>(1));
						for (Element valorDato : filaDato.getElementsByTag("td").get(0).getElementsByTag("span")) {
							String textoAutor = valorDato.ownText().trim();
							Matcher m = patAutor.matcher(textoAutor);
							if (m.matches()) {
								String masDatosAutor = m.group(7);
								if ("; tr.".equals(masDatosAutor)) {
									// Tambien salen los traductores en los datos del autor con el sufijo "; tr."
									String apesTrad = StringUtils.defaultString(m.group(2)), nomTrad = StringUtils.defaultString(m.group(3));
									if (StringUtils.isNotEmpty(apesTrad) || StringUtils.isNotEmpty(nomTrad)) {
										Traductor traductor = new Traductor();
										if (StringUtils.isNotEmpty(apesTrad) && StringUtils.isNotEmpty(nomTrad)) {
											nomTrad += " ";
										}
										nomTrad += apesTrad;
										traductor.setNombre(nomTrad);
										if (libro.getTraductores() == null) {
											libro.setTraductores(new HashSet<>(1));
										}
										libro.getTraductores().add(traductor);
									}
								} else {
									Autor autor = new Autor();
									libro.getAutores().add(autor);
									autor.setApellidos(m.group(2));
									autor.setNombre(m.group(3));
									String fnac = m.group(5);
									if (StringUtils.isNotEmpty(fnac)) {
										if (fnac.endsWith(" a.C.")) {
											fnac = "-"+fnac.replace(" a.C.", "");
										}
										try {
											autor.setAnnoNacimiento(Integer.valueOf(fnac));
										} catch (NumberFormatException e) {
											throw new Exception("Formato del año de nacimiento del autor desconocido: "+textoAutor);
										}
									}
									String fex = m.group(7);
									if (StringUtils.isNotEmpty(fex)) {
										if (fex.endsWith(" a.C.")) {
											fex = "-"+fex.replace(" a.C.", "");
										}
										try {
											autor.setAnnoFallecimiento(Integer.valueOf(fex));
										} catch (NumberFormatException e) {
											throw new Exception("Formato del año de fallec. del autor desconocido: "+textoAutor);
										}
									}
								}
							} else {
								throw new Exception("Formato del dato del autor desconocido: "+textoAutor);
							}
						}
					} else if (cabeceraDato.equals("Fecha Edición:")) {
						String textoFhEd = filaDato.getElementsByTag("td").get(0).text();
						Matcher m = patFhEd.matcher(textoFhEd);
						if (m.matches()) {
							String fhEd = m.group(1);
							try {
								libro.setAnnoPublicacion(Integer.valueOf(fhEd));
							} catch (NumberFormatException e) {
								throw new Exception("Formato del año de edicion desconocido: "+textoFhEd);
							}
						} else {
							throw new Exception("Formato de la fecha de edicion desconocido: "+textoFhEd);
						}
					} else if (cabeceraDato.equals("Descripción:")) {
						String textoDesc = filaDato.getElementsByTag("td").get(0).text();
						Matcher m = patPaginas.matcher(textoDesc);
						if (m.matches()) {
							String paginas = m.group(1);
							try {
								libro.setNumPaginas(Integer.valueOf(paginas));
							} catch (NumberFormatException e) {
								throw new Exception("Formato del numero de paginas desconocido: "+textoDesc);
							}
						} else {
							throw new Exception("Formato de la descripcion con el numero de paginas desconocido: "+textoDesc);
						}
					} else if (cabeceraDato.equals("Precio:")) {
						String textoPrecio = filaDato.getElementsByTag("td").get(0).text();
						Matcher m = patPrecio.matcher(textoPrecio);
						if (m.matches()) {
							String precio = m.group(1);
							try {
								libro.setPrecio(Float.valueOf(precio));
							} catch (NumberFormatException e) {
								throw new Exception("Formato del valor del precio desconocido: "+textoPrecio);
							}
						} else {
							throw new Exception("Formato del precio desconocido: "+textoPrecio);
						}
					} else if (cabeceraDato.equals("Publicación:")) {
						String textoEditorial = filaDato.getElementsByTag("td").get(0).text();
						if (StringUtils.isNotEmpty(textoEditorial)) {
							Editorial editorial = new Editorial();
							editorial.setNombre(textoEditorial);
							libro.setEditorial(editorial);
						}
					}
				}
				
			}
		}
		return libros;
	}

}
