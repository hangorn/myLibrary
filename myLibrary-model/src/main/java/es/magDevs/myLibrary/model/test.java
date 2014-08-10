package es.magDevs.myLibrary.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.dao.LibroDao;
import es.magDevs.myLibrary.model.dao.TipoDao;

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
		
		TipoDao a = DaoFactory.getTipoDao();
		
		
		
		Tipo mitipo = new Tipo(32, "este es mi segundo tipo modificado");
		a.update(mitipo);
		System.out.println(mitipo);
		
		
		List<Tipo> l = a.getTipos();
//		System.out.println(l);
		
		for (Tipo autor : l) {
			System.out.println(autor);
		}
		
	}
	
	public test() {
		
	}
	
}
