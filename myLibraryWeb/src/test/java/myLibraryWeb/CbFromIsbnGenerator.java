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
package myLibraryWeb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.ISBNValidator;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.dao.LibroDao;
import es.magDevs.myLibrary.web.isbn.IsbnDataProcesor;

public class CbFromIsbnGenerator {

	@Test
	public void test() throws Exception {
		// Africanus - Posteguillo
//		String isbn = "978-84-9872-967-2";
		// Los jardines de luz - Maalouf, Amin
//		String isbn = "84-206-5690-9";
		
		List<Libro> libros = new ArrayList<Libro>();
//		libros.addAll(ministerioDataMiner.getData("84-206-5690-9"));
//		libros.addAll(ministerioDataMiner.getData("978-84-9872-967-2"));
//		libros.addAll(ministerioDataMiner.getData("84-239-1827-0"));
//		libros.addAll(ministerioDataMiner.getData("9788408240532"));
		
//		libros.add(new IsbnDataProcesor(e->e.printStackTrace()).getData("978-84-18701-18-4"));
//		libros.add(new IsbnDataProcesor().getData("9788418345098"));
//		libros.add(new IsbnDataProcesor().getData("9788411000420"));
//		libros.add(new IsbnDataProcesor().getData("9788419518019"));
//		libros.add(new IsbnDataProcesor().getData("9788495587381"));
//		libros.add(new IsbnDataProcesor().getData("978-84-08-25049-4"));
//		libros.add(new IsbnDataProcesor().getData("978-84-02-44433-2"));
//		libros.add(new IsbnDataProcesor().getData("84-08-23449-4"));
		
		for (Libro libro : libros) {
			if (libro != null) {
				mostrarDatosLibro(libro);
			}
		}
		assertTrue(isValidIsbn("84-7800-538-2"));
		assertEquals("9788401312694", isbn10_2isbn13("8401312698"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testComprobador() throws Exception {
		LibroDao dao = DaoFactory.getLibroDao();
		Libro query = new Libro();
		query.setSortedColumn("id");
		query.setSortedColumn("anno_publicacion");
		query.setSortedColumn("ubicacion");
		query.setSortedDirection(false);
		query.setCb("ERR_DATOS_2003");
		query.setCb("ERR_ISBN_10");
		query.setCb("ERR_DATOS_10");
		query.setCb("ERR_DATOS");
		query.setCb("NO_ENCONTRADO");
		query.setCb("PROCESAR");
		List<Libro> libros = dao.getWithPag(query ,0, 600);
		IsbnDataProcesor miner = new IsbnDataProcesor(e->e.printStackTrace());
		
		if (libros.isEmpty()) {
			System.err.println("---------------------------");
			System.err.println(" ¡¡¡¡ NO HAY LIBROS A PROCESAR !!!!");
			System.err.println("---------------------------");
		}
		
		int i = 0;
		for (Libro libro : libros) {
			System.err.println("---------------------------");
			System.err.println("Procesando "+ ++i+" de "+ libros.size());
			System.err.println("---------------------------");
			libro = (Libro) dao.get(libro.getId());
			if (StringUtils.isEmpty(libro.getIsbn())) {
				System.err.println("Libro "+libro.getTitulo()+" (ID="+libro.getId()+") sin ISBN.");
				updateCB(dao, libro, "");
				continue;
			}
			System.out.println("Procesando: "+libro.getTitulo()+ " ID="+libro.getId()+" Ubicacion: "+ (libro.getUbicacion() == null ? "" : libro.getUbicacion().getCodigo()));
			
			String cb = null;
			boolean isbnOk;
			boolean isIsbn10 = false;

			if (isValidIsbn("978"+libro.getIsbn().replace("-", ""))) {
				isbnOk = true;
				cb = "978"+libro.getIsbn();
			} else {
				if (isValidIsbn(libro.getIsbn())) {
					isbnOk = true;
					if (libro.getIsbn().replace("-", "").length() == 10) {
						System.err.println("ISBN-10 valido: "+ libro.getIsbn());
						cb = isbn10_2isbn13(libro.getIsbn().replace("-", ""));
						isIsbn10 = true;
					} else {
						cb = libro.getIsbn();
					}
				} else {
					System.err.println("ISBN NO valido: "+ libro.getIsbn());
					isbnOk = false;
					cb = "ERR_ISBN";
				}
			}
			
			if (isbnOk) {
				Libro datosLibro = miner.getData(libro.getIsbn().replace("-", ""));
				System.err.println("ISBN valido: "+cb);
				System.err.println("update libros set cb = '"+ cb.replace("-", "")+"' where id ="+libro.getId());
				if (datosLibro == null) {
					System.err.println("No se han encontrado datos del libro: "+libro.getTitulo()+" con ISBN="+libro.getIsbn());
					updateCB(dao, libro, "SIN_DATOS");
				} else if (comprobarDatos(libro, datosLibro)) {
					updateCB(dao, libro, cb);
				} else {
					updateCB(dao, libro, isIsbn10 ? "ERR_DATOS_10": "ERR_DATOS");
				}
				Thread.sleep(30000);
			} else {
				updateCB(dao, libro, cb);
			}
		}
	}

	protected void updateCB(LibroDao dao, Libro libro, String cb) throws Exception {
		try {
			dao.beginTransaction();
			dao.updateCB(libro.getId(), cb.replace("-", ""));
			dao.commitTransaction();
			System.err.println("Se ha actualizado el CB="+cb.replace("-", "")+" del libro ID="+libro.getId());
		} catch (Exception e) {
			dao.rollbackTransaction();
			throw e;
		}
	}

	protected void mostrarDatosLibro(Libro libro) {
		System.out.println("-----------------");
		System.out.println("Titulo: "+libro.getTitulo());
		if (!CollectionUtils.isEmpty(libro.getAutores())) {
			System.out.println("Autor(es): ");
			for (Autor autor : libro.getAutores()) {
				System.out.println("		"+autor.toString() + " ("+(autor.getAnnoNacimiento()==null?"":autor.getAnnoNacimiento())+"-"+(autor.getAnnoFallecimiento()==null?"":autor.getAnnoFallecimiento())+")");
			}
		}
		if (!CollectionUtils.isEmpty(libro.getTraductores())) {
			System.out.println("Traductor(es): ");
			for (Traductor traductor : libro.getTraductores()) {
				System.out.println("		"+traductor.getNombre());
			}
		}
		System.out.println("Año Pub.: "+libro.getAnnoPublicacion());
		System.out.println("Paginas: "+libro.getNumPaginas());
		System.out.println("Precio: "+libro.getPrecio());
		if (libro.getEditorial() != null) {
			System.out.println("Editorial: "+libro.getEditorial().getNombre());
		}
		System.out.println("ISBN: "+libro.getIsbn());
		System.out.println("");
		System.out.println("-----------------");
		System.out.println("");
	}

	private boolean comprobarDatos(Libro libro, Libro datosLibro) {
		boolean correcto = true;
		// Titulo
		if (!libro.getTitulo().equals(datosLibro.getTitulo())) {
			if (datosLibro.getTitulo() == null || !(datosLibro.getTitulo().startsWith(libro.getTitulo()) || libro.getTitulo().startsWith(datosLibro.getTitulo()) ||
					libro.getTitulo().replaceAll("[0,.:;()/\"¡!?¿-]", " ").replaceAll(" +", " ").trim().equals(datosLibro.getTitulo().replaceAll("[0,.:;()/\"¡!?¿-]", " ").replaceAll(" +", " ").trim()))) {
				System.err.println("Libro (ID="+libro.getId()+") \t no coincide titulo:\nBBDD="+libro.getTitulo()+"\nISBN="+datosLibro.getTitulo());
				correcto = false;
			}
		}
		// Autores
		if (libro.getAutores() != null && libro.getAutores().size() == 1 && libro.getAutores().iterator().next().getApellidos().equals("ANONIMO") && libro.getAutores().iterator().next().getNombre() == null) {
			// Si el autor del libro es ANONIMO, lo damos por bueno
		} else if (datosLibro.getAutores() == null || libro.getAutores() == null) {
			if (datosLibro.getAutores() != null || libro.getAutores() != null) {
				System.err.println("Libro (ID="+libro.getId()+") \t no coinciden autores:\nBBDD="+
						(libro.getAutores() == null ? "null" : StringUtils.join(libro.getAutores(), ", "))+"\nISBN="+
						(datosLibro.getAutores() == null ? "null" : StringUtils.join(datosLibro.getAutores(), ", ")));
				correcto = false;
			}
		} else if (datosLibro.getAutores().size() != libro.getAutores().size()) {
			if (!datosLibro.getAutores().isEmpty() && datosLibro.getAutores().size() < libro.getAutores().size()) {
				// Si tenemos datos de autores y todos los datos de autores estan en el libro
				for (Autor autor : datosLibro.getAutores()) {
					boolean encontrado = false;
					for (Autor autorR : libro.getAutores()) {
						if (Objects.equals(autor.getNombre(), autorR.getNombre()) && autor.getApellidos().equals(autorR.getApellidos())) {
							encontrado = true;
						}
					}
					if (!encontrado) {
						System.err.println("Libro (ID="+libro.getId()+") \t no coinciden autores:\nBBDD="+StringUtils.join(libro.getAutores(), ", ")+"\nISBN="+StringUtils.join(datosLibro.getAutores(), ", "));
						correcto = false;
						break;
					}
				}
			} else {
				System.err.println("Libro (ID="+libro.getId()+") \t no coinciden autores:\nBBDD="+StringUtils.join(libro.getAutores(), ", ")+"\nISBN="+StringUtils.join(datosLibro.getAutores(), ", "));
				correcto = false;
			}
		} else if (libro.getAutores().size() == 1) {
			Autor autor = libro.getAutores().iterator().next();
			Autor autorR = datosLibro.getAutores().iterator().next();
			if (!Objects.equals(autor.getNombre(), autorR.getNombre()) || !autor.getApellidos().equals(autorR.getApellidos())) {
				System.err.println("Libro (ID="+libro.getId()+") \t no coinciden autores:\nBBDD="+autor+"\nISBN="+autorR);
				correcto = false;
			}
		} else if (libro.getAutores().size() > 1) {
			for (Autor autor : libro.getAutores()) {
				boolean encontrado = false;
				for (Autor autorR : datosLibro.getAutores()) {
					if (Objects.equals(autor.getNombre(), autorR.getNombre()) && autor.getApellidos().equals(autorR.getApellidos())) {
						encontrado = true;
					}
				}
				if (!encontrado) {
					System.err.println("Libro (ID="+libro.getId()+") \t no coinciden autores:\nBBDD="+StringUtils.join(libro.getAutores(), ", ")+"\nISBN="+StringUtils.join(datosLibro.getAutores(), ", "));
					correcto = false;
					break;
				}
			}
		}
//		// Fh publicacion
//		if (datosLibro.getAnnoPublicacion() != null && !Objects.equals(libro.getAnnoPublicacion(), datosLibro.getAnnoPublicacion())) {
//			System.err.println("Libro (ID="+libro.getId()+") \t no coincide año publicacion:\nBBDD="+libro.getAnnoPublicacion()+"\nISBN="+datosLibro.getAnnoPublicacion());
//			correcto = false;
//		}
//		// Num pag
//		if (datosLibro.getNumPaginas() != null && !Objects.equals(libro.getNumPaginas(), datosLibro.getNumPaginas())) {
//			System.err.println("Libro (ID="+libro.getId()+") \t no coinciden paginas:\nBBDD="+libro.getNumPaginas()+"\nISBN="+datosLibro.getNumPaginas());
//			correcto = false;
//		}
//		// Precio
//		if (datosLibro.getPrecio() != null && !Objects.equals(libro.getPrecio(), datosLibro.getPrecio())) {
//			System.err.println("Libro (ID="+libro.getId()+") \t no coincide precio:\nBBDD="+libro.getPrecio()+"\nISBN="+datosLibro.getPrecio());
//			correcto = false;
//		}
		// Editorial
		if (libro.getEditorial() != null && (datosLibro.getEditorial() == null || !Objects.equals(libro.getEditorial().getId(), datosLibro.getEditorial().getId()))) {
			System.err.println("Libro (ID="+libro.getId()+") \t no coincide editorial:\nBBDD="+libro.getEditorial()+"\nISBN="+datosLibro.getEditorial());
			correcto = false;
		}
		return correcto;
	}
	
	private boolean isValidIsbn(String strIsbn) {
		return ISBNValidator.getInstance().isValid(strIsbn);
		
//		char charIsbn[] = strIsbn.replace("-", "").toCharArray();
//		int isbn[] = new int[charIsbn.length];
//		for (int i = 0; i < isbn.length; i++) {
//			isbn[i] = charIsbn[i] - '0';
//			
//		}
//	    int sum = 0;
//	    if(isbn.length == 10) {
//	        for(int i = 0; i < 10; i++) {
//	            sum += i * isbn[i]; //asuming this is 0..9, not '0'..'9'
//	        }
//
//	        if(isbn[9] == sum % 11) return true;
//	    } else if(isbn.length == 13) {
//
//	        for(int i = 0; i < 12; i++) {
//	            if(i % 2 == 0) {
//	                sum += isbn[i]; //asuming this is 0..9, not '0'..'9'
//	            } else {
//	                sum += isbn[i] * 3;
//	            }
//	        }
//
//	        int tocheck = 10 - (sum % 10);
//	        if (tocheck == 10) {
//	        	tocheck = 0;
//	        }
//			if(isbn[12] == tocheck) return true;
//	    }
//
//	    return false;
	}

	private String isbn10_2isbn13(String isbn10) {
		return ISBNValidator.getInstance().convertToISBN13(isbn10);
	}
}
