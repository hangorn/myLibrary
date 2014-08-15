package es.magDevs.myLibrary.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.dao.LibroDao;
import es.magDevs.myLibrary.model.dao.TipoDao;
@SuppressWarnings({ "unused" })
public class test {
	
	public static void main(String[] args) {
//		Set<Tipo> s1 = new HashSet<Tipo>();
//		s1.add(new Tipo(1, "mitipo"));
//		s1.add(new Tipo(2, "otro tipo"));
//		System.out.println(s1);
//		Set<Tipo> s2 = new HashSet<Tipo>();
//		s2.addAll(s1);
//		s2.clear();
//		System.out.println(s1);
//		System.out.println(s2);
		
		LibroDao a = DaoFactory.getLibroDao();
		
		Libro f = new Libro();
//		f.setAutores(new HashSet<Autor>());
//		f.getAutores().add(new Autor(null, "a", null, null, null, null, null, null));
		f.setTipo(new Tipo(null, "poesia"));
		
		List<Libro> l = a.getLibrosWithPag(f, 0,30);
//		System.out.println(a.getCountLibros(f));
		
		System.out.println(l);
		
	}
	
	public test() {
		
	}
	
}
