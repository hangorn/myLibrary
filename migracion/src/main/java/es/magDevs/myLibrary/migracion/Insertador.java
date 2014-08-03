package es.magDevs.myLibrary.migracion;

import es.magDevs.myLibrary.migracion.beans.AutorBean;
import es.magDevs.myLibrary.migracion.beans.CodificacionBean;
import es.magDevs.myLibrary.migracion.beans.ColeccionBean;
import es.magDevs.myLibrary.migracion.beans.EditorialBean;
import es.magDevs.myLibrary.migracion.beans.LibrosBean;
import es.magDevs.myLibrary.migracion.beans.UbicacionBean;

public class Insertador {
	private static int cTipos = 0;
	private static int cUbicaciones = 0;
	private static int cAutores = 0;
	private static int cColecciones = 0;
	private static int cEditoriales = 0;
	private static int cLibros = 0;

	public static String insertsCodificaciones(CodificacionBean bean) {
		String query = "INSERT INTO tipos (descripcion) VALUES ("
				+ str2StrOrNull(bean.getCodigo()) + ");";
		cTipos++;
		bean.setNúmero_codificacion(cTipos);
		return query;
	}

	public static String insertsUbicaciones(UbicacionBean bean) {
		String query = "INSERT INTO ubicaciones (codigo) VALUES ("
				+ str2StrOrNull(bean.getSitio()) + ");";
		cUbicaciones++;
		bean.setNº_ubicacion(cUbicaciones);
		return query;
	}

	public static String insertAutores(AutorBean bean) {
		String query = "INSERT INTO autores (nombre,apellidos,pais,"
				+ "ciudad,anno_nacimiento,anno_fallecimiento,notas)"
				+ " VALUES (" + str2StrOrNull(bean.getNombre()) + ","
				+ str2StrOrNull(bean.getApellidos()) + ","
				+ str2StrOrNull(bean.getPais_de_nacimiento()) + ","
				+ str2StrOrNull(bean.getLugar_de_nacimiento()) + ","
				+ bean.getAño__de_nacimiento() + ","
				+ bean.getAño_fallecimiento() + ","
				+ str2StrOrNull(bean.getNotas()) + ");";
		cAutores++;
		bean.setNº_autor(cAutores);
		return query;
	}

	public static String insertEditoriales(EditorialBean bean) {
		String query = "INSERT INTO editoriales (nombre,ciudad) VALUES ("
				+ str2StrOrNull(bean.getNombre_editorial()) + ","
				+ str2StrOrNull(bean.getCiudad_de_ubicacion()) + ");";
		cEditoriales++;
		bean.setNº_editorial(cEditoriales);
		return query;
	}

	public static String insertColecciones(ColeccionBean bean) {
		String query;
		String editorial;
		try {
			editorial = "" + Integer.parseInt(bean.getNombre_editorial());
		} catch (NumberFormatException e) {
			editorial = "null";
		}
		query = "INSERT INTO colecciones (nombre, editorial) VALUES ("
				+ str2StrOrNull(bean.getNombre_coleccion()) + "," + editorial
				+ ");";
		cColecciones++;
		bean.setNº_coleccion(cColecciones);
		return query;
	}

	public static String insertLibros(LibrosBean bean) {
		String editorial;
		String tipo;
		String ubicacion;
		String coleccion;
		String annoCompra;
		String precio;
		try {
			editorial = "" + Integer.parseInt(bean.getNombre_editorial());
		} catch (NumberFormatException e) {
			editorial = "null";
		}
		try {
			tipo = "" + Integer.parseInt(bean.getCodigo());
		} catch (NumberFormatException e) {
			tipo = "null";
		}
		try {
			ubicacion = "" + Integer.parseInt(bean.getSitio());
		} catch (NumberFormatException e) {
			ubicacion = "null";
		}
		try {
			coleccion = "" + Integer.parseInt(bean.getNombre_coleccion());
		} catch (NumberFormatException e) {
			coleccion = "null";
		}
		try {
			annoCompra = "" + Integer.parseInt(bean.getAño_compra());
		} catch (NumberFormatException e) {
			annoCompra = "null";
		}
		try {
			precio = "" + Float.parseFloat("" + bean.getPrecio().floatValue());
		} catch (Exception e) {
			precio = "null";
		}
		String query = "INSERT INTO libros (titulo, isbn, anno_compra,"
				+ " anno_publicacion, anno_copyright, num_edicion,"
				+ " num_paginas, tomo, precio, notas, editorial, tipo,"
				+ " ubicacion, coleccion) VALUES ("
				+ str2StrOrNull(bean.getTitulo())
				+ ","
				+ str2StrOrNull(bean.getNº_isbn())
				+ ","
				+ annoCompra
				+ ","
				+ bean.getAño_publicacion()
				+ ","
				+ bean.getAño_c()
				+ ","
				+ bean.getNº_edicion()
				+ ","
				+ bean.getNº_paginas()
				+ ","
				+ bean.getTomo()
				+ ","
				+ precio
				+ ","
				+ str2StrOrNull(bean.getNotas())
				+ ","
				+ editorial
				+ ","
				+ tipo
				+ ","
				+ ubicacion + "," + coleccion + ");";
		cLibros++;
		bean.setNº_libro(cLibros);
		return query;
	}

	public static String insertTraductores(String nombre) {
		return "INSERT INTO traductores (nombre) VALUES ('" + nombre + "');";
	}

	public static String insertLibrosAutores(Integer libro, Integer autor) {
		return "INSERT INTO libros_autores (libro, autor) VALUES (" + libro
				+ "," + autor + ");";
	}

	public static String insertLibrosTraductores(Integer libro,
			Integer traductor) {
		return "INSERT INTO libros_traductores (libro, traductor) VALUES ("
				+ libro + "," + traductor + ");";
	}

	private static String str2StrOrNull(String s) {
		if (s == null || s.toUpperCase().equals("NULL")) {
			return "null";
		} else {
			return "'" + s + "'";
		}

	}

}
