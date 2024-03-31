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
import java.util.function.Consumer;

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
	
	private Consumer<Exception> errorHandler;

	public IsbnDataProcesor(Consumer<Exception> errorHandler) {
		iberDataMiner = new IberDataMiner();
		this.errorHandler = errorHandler;
	}
	
	public Libro getData(String isbn) throws Exception {
		IsbnDataMiner ministerioDataMiner = new MinisterioDataMiner();
		List<Libro> libros;
		try {
			libros = ministerioDataMiner.getData(isbn);
		} catch (Exception e) {
			errorHandler.accept(e);
			libros = new ArrayList<>();
		}
		
		try {
			libros.addAll(iberDataMiner.getData(isbn));
		} catch (Exception e) {
			errorHandler.accept(e);
		}
		
		if (libros.isEmpty()) {
			try {
				IsbnDataMiner bneDataMiner = new BneDataMiner();
				libros.addAll(bneDataMiner.getData(isbn));
			} catch (Exception e) {
				errorHandler.accept(e);
			}
		}
		if (libros.isEmpty()) {
			return null;
		}
		// Primero rellenamos los datos de un libro con todos los datos que hemos obtenido
		Libro libro = new Libro();
		List<Editorial> editoriales = new ArrayList<>();
		int i = 1;
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
						autor.setId(i++);
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
							autor.setId(i++);
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
		return data.replaceAll("\\s+", " ").replaceAll("[ÁÀÂ]", "A").replaceAll("[ÉÈÊ]", "E").replaceAll("[ÍÌÎ]", "I").replaceAll("[ÓÒÔØÖ]", "O").replaceAll("[ÚÙÛ]", "U").trim();
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
				} else if (autor.getNombre() != null && autor.getApellidos() == null) {
					autorQry.setNombreExacto(null);
					autorQry.setApellidosExacto(autor.getNombre());
					autoresBBDD = dao.getWithPag(autorQry, 0, 3);
					if (autoresBBDD.size() == 1) {
						Autor autorBBDD = autoresBBDD.get(0);
						if (!idAutores.contains(autorBBDD.getId())) {
							autores.add(autorBBDD);
							idAutores.add(autorBBDD.getId());
						}
					}
				}
			}
			// Si no ha encontrado ningun autor, intentamos buscar quitando puntos y guiones
			if (autores.isEmpty()) {
				for (Autor autor : libro.getAutores()) {
					Autor autorQry = new Autor();
					if (autor.getNombre() != null) {
						autorQry.setNombreExacto(autor.getNombre().replaceAll("[.-]", " ").replaceAll(" +", " "));
					}
					autorQry.setApellidosExacto(autor.getApellidos().replaceAll("[.-]", " ").replaceAll(" +", " "));
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
			}
			// Si no ha encontrado ningun autor, intentamos buscar autores separando por espacios
			if (autores.isEmpty()) {
				for (Autor autor : libro.getAutores()) {
					if (StringUtils.isNotBlank(autor.getApellidos()) && StringUtils.isBlank(autor.getNombre())) {
						Autor autorQry = new Autor();
						bucleSeparador : for (Autor separados : separaNombreApellidos(autor.getApellidos())) {
							autorQry.setNombreExacto(separados.getNombre());
							autorQry.setApellidosExacto(separados.getApellidos());
							List<Autor> autoresBBDD = dao.getWithPag(autorQry, 0, 3);
							if (autoresBBDD.size() == 1) {
								Autor autorBBDD = autoresBBDD.get(0);
								if (!idAutores.contains(autorBBDD.getId())) {
									autores.add(autorBBDD);
									idAutores.add(autorBBDD.getId());
									break;
								}
							} else if (autoresBBDD.size() > 1) {
								for (Autor autorBBDD : autoresBBDD) {
									if (Objects.equals(autorBBDD.getAnnoNacimiento(), autor.getAnnoNacimiento())) {
										autores.add(autorBBDD);
										idAutores.add(autorBBDD.getId());
										break bucleSeparador;
									}
								}
							}
						}
					}
				}
			}
			// Si no ha encontrado ningun autor, intentamos buscar autores separando por espacios y quitando puntos
			if (autores.isEmpty()) {
				for (Autor autor : libro.getAutores()) {
					if (StringUtils.isNotBlank(autor.getApellidos()) && StringUtils.isBlank(autor.getNombre())) {
						Autor autorQry = new Autor();
						bucleSeparador : for (Autor separados : separaNombreApellidos(autor.getApellidos().replaceAll("\\.([^ ])", " $1").replaceAll("\\.", ""))) {
							autorQry.setNombreExacto(separados.getNombre());
							autorQry.setApellidosExacto(separados.getApellidos());
							List<Autor> autoresBBDD = dao.getWithPag(autorQry, 0, 3);
							if (autoresBBDD.size() == 1) {
								Autor autorBBDD = autoresBBDD.get(0);
								if (!idAutores.contains(autorBBDD.getId())) {
									autores.add(autorBBDD);
									idAutores.add(autorBBDD.getId());
									break;
								}
							} else if (autoresBBDD.size() > 1) {
								for (Autor autorBBDD : autoresBBDD) {
									if (Objects.equals(autorBBDD.getAnnoNacimiento(), autor.getAnnoNacimiento())) {
										autores.add(autorBBDD);
										idAutores.add(autorBBDD.getId());
										break bucleSeparador;
									}
								}
							}
						}
					}
				}
			}
			// Si no ha encontrado ningun autor, intentamos buscar autores quitando apellidos
			if (autores.isEmpty()) {
				for (Autor autor : libro.getAutores()) {
					if (StringUtils.isNotBlank(autor.getApellidos()) && StringUtils.isNotBlank(autor.getNombre())) {
						Autor autorQry = new Autor();
						bucleSeparador : for (String separados : quitarApellidos(autor.getApellidos().replaceAll("\\.", ""))) {
							autorQry.setNombreExacto(autor.getNombre());
							autorQry.setApellidosExacto(separados);
							List<Autor> autoresBBDD = dao.getWithPag(autorQry, 0, 3);
							if (autoresBBDD.size() == 1) {
								Autor autorBBDD = autoresBBDD.get(0);
								if (!idAutores.contains(autorBBDD.getId())) {
									autores.add(autorBBDD);
									idAutores.add(autorBBDD.getId());
									break;
								}
							} else if (autoresBBDD.size() > 1) {
								for (Autor autorBBDD : autoresBBDD) {
									if (Objects.equals(autorBBDD.getAnnoNacimiento(), autor.getAnnoNacimiento())) {
										autores.add(autorBBDD);
										idAutores.add(autorBBDD.getId());
										break bucleSeparador;
									}
								}
							}
						}
					}
				}
			}

			// Si no ha encontrado ningun autor, intentamos buscar autores separando el apellido en nombre y apellidos y luego quitando apellidos
			if (autores.isEmpty()) {
				for (Autor autor : libro.getAutores()) {
					if (StringUtils.isNotBlank(autor.getApellidos()) && StringUtils.isBlank(autor.getNombre())) {
						Autor autorQry = new Autor();
						bucleSeparador : for (Autor separados : separaNombreApellidos(autor.getApellidos().replaceAll("\\.", ""))) {
							if (!separados.getApellidos().contains(" ")) {
								continue;
							}
							autorQry.setNombreExacto(separados.getNombre());
							autorQry.setApellidosExacto(StringUtils.left(separados.getApellidos(), separados.getApellidos().lastIndexOf(" ")));
							List<Autor> autoresBBDD = dao.getWithPag(autorQry, 0, 3);
							if (autoresBBDD.size() == 1) {
								Autor autorBBDD = autoresBBDD.get(0);
								if (!idAutores.contains(autorBBDD.getId())) {
									autores.add(autorBBDD);
									idAutores.add(autorBBDD.getId());
									break;
								}
							} else if (autoresBBDD.size() > 1) {
								for (Autor autorBBDD : autoresBBDD) {
									if (Objects.equals(autorBBDD.getAnnoNacimiento(), autor.getAnnoNacimiento())) {
										autores.add(autorBBDD);
										idAutores.add(autorBBDD.getId());
										break bucleSeparador;
									}
								}
							}
						}
					}
				}
			}
			
			// Si no ha encontrado ningun autor, intentamos buscar autores por el inicio del apellido
			if (autores.isEmpty()) {
				for (Autor autor : libro.getAutores()) {
					Autor autorQry = new Autor();
					autorQry.setNombreExacto(autor.getNombre());
					autorQry.setApellidos(autor.getApellidos());
					List<Autor> autoresBBDD = dao.getWithPag(autorQry, 0, 3);
					if (autoresBBDD.size() == 1) {
						Autor autorBBDD = autoresBBDD.get(0);
						if (!idAutores.contains(autorBBDD.getId())) {
							autores.add(autorBBDD);
							idAutores.add(autorBBDD.getId());
						}
					} else if (autoresBBDD.size() > 1) {
						for (Autor autorBBDD : autoresBBDD) {
							if (autorBBDD.getApellidos().startsWith(autor.getApellidos())) {
								autores.add(autorBBDD);
								idAutores.add(autorBBDD.getId());
								break;
							}
						}
					}
				}
			}
			// Si no ha encontrado ningun autor, intentamos buscar autores quitando nombres
			if (autores.isEmpty()) {
				for (Autor autor : libro.getAutores()) {
					if (StringUtils.isNotBlank(autor.getApellidos()) && StringUtils.isNotBlank(autor.getNombre())) {
						Autor autorQry = new Autor();
						bucleSeparador : for (String separados : quitarApellidos(autor.getNombre().replaceAll("\\.", ""))) {
							autorQry.setNombreExacto(separados);
							autorQry.setApellidosExacto(autor.getApellidos());
							List<Autor> autoresBBDD = dao.getWithPag(autorQry, 0, 3);
							if (autoresBBDD.size() == 1) {
								Autor autorBBDD = autoresBBDD.get(0);
								if (!idAutores.contains(autorBBDD.getId())) {
									autores.add(autorBBDD);
									idAutores.add(autorBBDD.getId());
									break;
								}
							} else if (autoresBBDD.size() > 1) {
								for (Autor autorBBDD : autoresBBDD) {
									if (Objects.equals(autorBBDD.getAnnoNacimiento(), autor.getAnnoNacimiento())) {
										autores.add(autorBBDD);
										idAutores.add(autorBBDD.getId());
										break bucleSeparador;
									}
								}
							}
						}
					}
				}
			}
			// Si no ha encontrado ningun autor, intentamos mover articulos (de, del, van,...) del nombre a los apellidos
			if (autores.isEmpty()) {
				for (Autor autor : libro.getAutores()) {
					if (StringUtils.isNotBlank(autor.getApellidos()) && StringUtils.isNotBlank(autor.getNombre())) {
						bucleSeparador: for (String articulo : Arrays.asList("VAN", "DEL", "DE")) {
							if (autor.getNombre().endsWith(" "+articulo)) {
								Autor autorQry = new Autor();
								autorQry.setNombreExacto(autor.getNombre().substring(0, autor.getNombre().length()-articulo.length()).trim());
								autorQry.setApellidosExacto(articulo + " " + autor.getApellidos());
								List<Autor> autoresBBDD = dao.getWithPag(autorQry, 0, 3);
								if (autoresBBDD.size() == 1) {
									Autor autorBBDD = autoresBBDD.get(0);
									if (!idAutores.contains(autorBBDD.getId())) {
										autores.add(autorBBDD);
										idAutores.add(autorBBDD.getId());
										break;
									}
								} else if (autoresBBDD.size() > 1) {
									for (Autor autorBBDD : autoresBBDD) {
										if (Objects.equals(autorBBDD.getAnnoNacimiento(), autor.getAnnoNacimiento())) {
											autores.add(autorBBDD);
											idAutores.add(autorBBDD.getId());
											break bucleSeparador;
										}
									}
								}
							}
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
	
	private static final List<String> TXTS_REPLACE = Arrays.asList("", ".", ",", "EDITORIALES","EDITORIAL", "EDICIONES", "EDITORES", "EDITORES S.A.", "EDITORAS", "ED.", "EDICIONES DE",
			"GRUPO EDITORIAL", "GRUPO EDITORIAL,", "GRUPO EDITORIAL, S.A.U.,", "GRUPO EDITORIAL, S.A.U.", "ESPAÑA", "PAIS", "MULTIMEDIA", "AUDIOBOOKS.", "MAXI-",
			"BOOKS", "LIBROS", "COMICS", "CLASICOS", "S.L", "D.L", "S.L.", "S.L.U.", ", S.L.", "SL", "TOURING", ": TRUE CRIME", "GRAPHIC", " DEAGOSTINI", " DE AGOSTINI", " MONDADORI.COLECCION:TRUE CRIME",
			"\\d{4}. ED. ED.", "(EDICIONES)|(S\\.L\\.)", "(EDICIONES)|(,? ?S\\.?[AL]\\.?)", "(MAXI)|(\\.)", "(EDITORIAL)|(,? ?S\\.[AL]\\.)", "& INTERACTIVA S.L.U.",
			"COLECCIONABLES", "EDUCACION", "E.L.E.", "BOLSILLO", "LIBROS, S.L.", "PUBLICACIONES", "(EDICIONES)|(IBERICA)", ", MARKETING DIRECTO, S.L.", "MONDADORI, S.A. - JUNIOR",
			": GRANDES PASIONES DE LA LITERATURA", " DE EDICIONES Y PUBLICACIONES S.L.", "COMUNICACION, S.A.", "(DIARIO)|(, S.A.)", "EDICIONES DE LIBRERIA", "FUNDACION SANTA MARIA-EDICIONES",
			"EDICIONES DE HISTORIA, S.A.", "EDICIONES VOC, S.L.", "DE EDICIONES", "DE EDICIONES. REVISTA GALICIA HISTORICA, S.L.", "EDITORIALEDICIONES DEL PRADO D.L.", "EDICIONES DEL PRADO D.L.",
			"(GRANDEL OBRAS)|\\.", ". HISTORIA VIVA", "FABRI", "EDITORA, S.L.", "(EDICIONES GENERALES)|(, S\\.A\\.)", "EDITORES, S.A.", "-DARGAUD, S.A. EDITORES", "- ",
			"INTERNACIONAL, S.A.", "DE ESPAÑA EDITORES, S.A.", "(EDICIONES DEL)|(, S\\.A\\.)", ". SUBDIRECCION GENERAL DE PUBLICACIONES Y PATRIMONIO CULTURAL.", "(\\.)|(, S\\.L\\.)",
			"(FUNDACION DEL MUSEO)|(BILBAO)", "GUIAS", "EDITORA, S.A.");
	private IberDataMiner iberDataMiner;
	@SuppressWarnings("unchecked")
	private Editorial buscaEditorial(String nombreEd, Editorial editorialQry, EditorialDao dao) throws Exception {
		for (String txt : TXTS_REPLACE) {
			if (txt.isEmpty() || nombreEd.contains(txt) || txt.contains("|")) {
				editorialQry.setNombreExacto(nombreEd.replaceAll(txt, "").trim());
				List<Editorial> editorialesBBDD = dao.getWithPag(editorialQry, 0, 3);
				if (editorialesBBDD.size() == 1) {
					return editorialesBBDD.get(0);
				} else if (nombreEd.contains(".")) {
					editorialQry.setNombreExacto(nombreEd.replaceAll(txt, "").replace(".", "").trim());
					editorialesBBDD = dao.getWithPag(editorialQry, 0, 3);
					if (editorialesBBDD.size() == 1) {
						return editorialesBBDD.get(0);
					}
				}
				if (nombreEd.replaceAll(txt, "").trim().toUpperCase().equals("PENGUIN RANDOM")) {
					editorialQry.setNombreExacto("PENGUIN RANDOM HOUSE");
					editorialesBBDD = dao.getWithPag(editorialQry, 0, 3);
					if (editorialesBBDD.size() == 1) {
						return editorialesBBDD.get(0);
					}
				} else if (nombreEd.replaceAll(txt, "").trim().toUpperCase().equals("ECC")) {
					editorialQry.setNombreExacto("ECC EDICIONES");
					editorialesBBDD = dao.getWithPag(editorialQry, 0, 3);
					if (editorialesBBDD.size() == 1) {
						return editorialesBBDD.get(0);
					}
				} else if (nombreEd.replaceAll(txt, "").trim().toUpperCase().equals("LIBROS DE ASTEROIDE")) {
					editorialQry.setNombreExacto("LIBROS DEL ASTEROIDE");
					editorialesBBDD = dao.getWithPag(editorialQry, 0, 3);
					if (editorialesBBDD.size() == 1) {
						return editorialesBBDD.get(0);
					}
				} else if (nombreEd.replaceAll(txt, "").trim().toUpperCase().equals("ESPASA")) {
					editorialQry.setNombreExacto("ESPASA CALPE");
					editorialesBBDD = dao.getWithPag(editorialQry, 0, 3);
					if (editorialesBBDD.size() == 1) {
						return editorialesBBDD.get(0);
					}
				} else if (nombreEd.replaceAll(txt, "").trim().toUpperCase().equals("MAGISTERIO")) {
					editorialQry.setNombreExacto("MAGISTERIO ESPAÑOL");
					editorialesBBDD = dao.getWithPag(editorialQry, 0, 3);
					if (editorialesBBDD.size() == 1) {
						return editorialesBBDD.get(0);
					}
				} else if (nombreEd.replaceAll(txt, "").trim().toUpperCase().equals("ME EDITORES")) {
					editorialQry.setNombreExacto("M E EDITORES");
					editorialesBBDD = dao.getWithPag(editorialQry, 0, 3);
					if (editorialesBBDD.size() == 1) {
						return editorialesBBDD.get(0);
					}
				} else if (nombreEd.contains(" & ")) {
					editorialQry.setNombreExacto(nombreEd.replaceAll(txt, "").replaceAll(" & ", " Y ").trim().toUpperCase());
					editorialesBBDD = dao.getWithPag(editorialQry, 0, 3);
					if (editorialesBBDD.size() == 1) {
						return editorialesBBDD.get(0);
					}
				}
			}
		}
		
		return null;
	}
	
	private List<Autor> separaNombreApellidos(String ape) {
		String[] splitted = ape.split(" ");
		List<Autor> separados = new ArrayList<>();
		for (int i = 1; i < splitted.length; i++) {
			String parte1 = "";
			for (int j = 0; j < i; j++) {
				parte1 += " "+splitted[j];
			}
			String parte2 = "";
			for (int j = i; j < splitted.length; j++) {
				parte2 += " "+splitted[j];
			}
			separados.add(new Autor(null, parte1.trim(), parte2.trim(), null, null, null, null, null));
		}
		return separados;
	}
	
	private List<String> quitarApellidos(String ape) {
		String[] splitted = ape.split(" ");
		List<String> separados = new ArrayList<>();
		String aps = "";
		for (int i = 0; i < splitted.length; i++) {
			aps += " " +splitted[i];
			separados.add(aps.trim());
		}
		return separados;
	}

}
