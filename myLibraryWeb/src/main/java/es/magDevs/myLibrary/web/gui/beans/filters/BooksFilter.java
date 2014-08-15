package es.magDevs.myLibrary.web.gui.beans.filters;

import java.util.HashSet;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.beans.Ubicacion;

@SuppressWarnings("serial")
public class BooksFilter extends Libro {
	
	public BooksFilter() {
		setAutores(new HashSet<Autor>());
		getAutores().add(new Autor());
		setTraductores(new HashSet<Traductor>());
		getTraductores().add(new Traductor());
		setEditorial(new Editorial());
		setColeccion(new Coleccion());
		setTipo(new Tipo());
		setUbicacion(new Ubicacion());
	}
	
	public void setAutor(Autor autor) {
		getAutores().clear();
		getAutores().add(autor);
	}
	public Autor getAutor() {
		return (Autor) getAutores().toArray()[0];
	}
	
	public void setTraductor(Traductor traductor) {
		getTraductores().clear();
		getTraductores().add(traductor);
	}
	public Traductor getTraductor() {
		return (Traductor) getTraductores().toArray()[0];
	}

}
