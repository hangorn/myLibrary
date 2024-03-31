package myLibraryWeb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.dao.LibroDao;
import es.magDevs.myLibrary.model.dao.TraductorDao;

public class SeparadorTraductores {

	@Test
	public void separadorTraductores() throws Exception {
		TraductorDao dao = DaoFactory.getTraductorDao();
		LibroDao daoLibros = DaoFactory.getLibroDao();
		
		Traductor qryTrad = new Traductor();
		List<Traductor> traductoresJuntos = dao.getTraductores("/");
		for (Traductor traductor : traductoresJuntos) {
			System.err.println("Procesando traductor ID="+traductor.getId()+" nombre="+traductor.getNombre());
			List<Libro> books = dao.getLibrosTraductor(traductor.getId());
			
			List<Traductor> traductoresNuevos = new ArrayList<>(3);
			// Separamos el nombre del traductor por /
			String[] splitted = traductor.getNombre().split("/");
			try {
				dao.beginTransaction();
				boolean reaprovechado = false;
				for (String nombreSeparado : splitted) {
					qryTrad.setNombreExacto(nombreSeparado.trim());
					@SuppressWarnings("unchecked")
					List<Traductor> traductoresBBDD = dao.getWithTransaction(qryTrad, 0, 1);
					if (traductoresBBDD.isEmpty()) {
						if (!reaprovechado) {
							// Si podemos reaprovechamos el que ya existe cambiandole el nombre
							System.err.println("Actualizador traductor ID="+traductor.getId()+" nombreViejo="+traductor.getNombre()+" nombreNuevo="+nombreSeparado.trim());
							traductor.setNombre(nombreSeparado.trim());
							dao.update(traductor);
							traductoresNuevos.add(traductor);
							reaprovechado = true;
						} else {
							// Si no existe ya un traductor con el mismo nombre lo creamos
							Traductor trad = new Traductor(null, nombreSeparado.trim());
							dao.insert(trad);
							traductoresNuevos.add(trad);
							System.err.println("Creado traductor ID="+trad.getId()+" nombre="+trad.getNombre());
						}
					} else {
						traductoresNuevos.add(traductoresBBDD.get(0));
						System.err.println("Usando traductor que ya existe ID="+traductoresBBDD.get(0).getId()+" nombre="+traductoresBBDD.get(0).getNombre());
					}
				}
				
				// Actualizar los traductores de los libros
				for (Libro libro : books) {
					libro = (Libro) daoLibros.getWithTransaction(libro.getId());
					System.err.println("Procesando libro "+libro.getTitulo()+" ID="+libro.getId()+" con traductores= "+libro.getTraductores().stream().map(tr->""+tr.getId()).collect(Collectors.joining(",")));
					// Borramos el traductor que se esta procesando
					for (Iterator<Traductor> iterator = libro.getTraductores().iterator(); iterator.hasNext();) {
						Traductor tr = iterator.next();
						if (tr.getId().equals(traductor.getId())) {
							iterator.remove();
						}
					}
					//Añadimos los nuevos traductores
					libro.getTraductores().addAll(traductoresNuevos);
					System.err.println("Guardando traductores libro "+libro.getTitulo()+" ID="+libro.getId()+" con traductores= "+libro.getTraductores().stream().map(tr->""+tr.getId()).collect(Collectors.joining(",")));
					daoLibros.update(libro);
				}
				
				if (!reaprovechado) {
					System.err.println("Borrado traductor ID="+traductor.getId()+" nombre="+traductor.getNombre());
					dao.delete2(traductor);
				}
				dao.commitTransaction();
			}catch (Exception e) {
				dao.rollbackTransaction();
				throw e;
			}
		}
	}

}
