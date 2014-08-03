package es.magDevs.myLibrary.migracion;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import es.magDevs.myLibrary.migracion.beans.AutorBean;
import es.magDevs.myLibrary.migracion.beans.CodificacionBean;
import es.magDevs.myLibrary.migracion.beans.ColeccionBean;
import es.magDevs.myLibrary.migracion.beans.EditorialBean;
import es.magDevs.myLibrary.migracion.beans.LibrosBean;
import es.magDevs.myLibrary.migracion.beans.RevistasBean;
import es.magDevs.myLibrary.migracion.beans.Titulo_revistasBean;
import es.magDevs.myLibrary.migracion.beans.UbicacionBean;

public class Rellenador {
	public static AutorBean rellenadorAutorBean(Row row) {
		AutorBean d = new AutorBean();
		d.setNº_autor((Integer)row.get("Nº AUTOR"));
		d.setApellidos((String)row.get("APELLIDOS"));
		d.setNombre((String)row.get("NOMBRE"));
		d.setPais_de_nacimiento((String)row.get("PAIS DE NACIMIENTO"));
		d.setLugar_de_nacimiento((String)row.get("LUGAR DE NACIMIENTO"));
		d.setAño__de_nacimiento((Integer)row.get("AÑO  DE NACIMIENTO"));
		d.setAño_fallecimiento((Integer)row.get("AÑO FALLECIMIENTO"));
		d.setNotas((String)row.get("NOTAS"));
		return d;
	}
	public static List<AutorBean> listadorAutorBean(Database db) {
		Table table;
		try {
			table = db.getTable("AUTOR");
		} catch (IOException e) {
			return null;
		}
		List<AutorBean> list = new ArrayList<AutorBean>();
		for (Row row : table) {
			AutorBean p = Rellenador.rellenadorAutorBean(row);
			list.add(p);
		}
		return list;
	}
	public static CodificacionBean rellenadorCodificacionBean(Row row) {
		CodificacionBean d = new CodificacionBean();
		d.setNúmero_codificacion((Integer)row.get("NÚMERO CODIFICACION"));
		d.setCodigo((String)row.get("CODIGO"));
		return d;
	}
	public static List<CodificacionBean> listadorCodificacionBean(Database db) {
		Table table;
		try {
			table = db.getTable("CODIFICACION");
		} catch (IOException e) {
			return null;
		}
		List<CodificacionBean> list = new ArrayList<CodificacionBean>();
		for (Row row : table) {
			CodificacionBean p = Rellenador.rellenadorCodificacionBean(row);
			list.add(p);
		}
		return list;
	}
	public static ColeccionBean rellenadorColeccionBean(Row row) {
		ColeccionBean d = new ColeccionBean();
		d.setNº_coleccion((Integer)row.get("Nº COLECCION"));
		d.setNombre_coleccion((String)row.get("NOMBRE COLECCION"));
		d.setNombre_editorial((String)row.get("NOMBRE EDITORIAL"));
		return d;
	}
	public static List<ColeccionBean> listadorColeccionBean(Database db) {
		Table table;
		try {
			table = db.getTable("COLECCION");
		} catch (IOException e) {
			return null;
		}
		List<ColeccionBean> list = new ArrayList<ColeccionBean>();
		for (Row row : table) {
			ColeccionBean p = Rellenador.rellenadorColeccionBean(row);
			list.add(p);
		}
		return list;
	}
	public static EditorialBean rellenadorEditorialBean(Row row) {
		EditorialBean d = new EditorialBean();
		d.setNº_editorial((Integer)row.get("Nº EDITORIAL"));
		d.setNombre_editorial((String)row.get("NOMBRE EDITORIAL"));
		d.setCiudad_de_ubicacion((String)row.get("CIUDAD DE UBICACION"));
		return d;
	}
	public static List<EditorialBean> listadorEditorialBean(Database db) {
		Table table;
		try {
			table = db.getTable("EDITORIAL");
		} catch (IOException e) {
			return null;
		}
		List<EditorialBean> list = new ArrayList<EditorialBean>();
		for (Row row : table) {
			EditorialBean p = Rellenador.rellenadorEditorialBean(row);
			list.add(p);
		}
		return list;
	}
	public static LibrosBean rellenadorLibrosBean(Row row) {
		LibrosBean d = new LibrosBean();
		d.setNº_libro((Integer)row.get("Nº LIBRO"));
		d.setTitulo((String)row.get("TITULO"));
		d.setApellidos((String)row.get("APELLIDOS"));
		d.setNombre((String)row.get("NOMBRE"));
		d.setNombre_editorial((String)row.get("NOMBRE EDITORIAL"));
		d.setNombre_coleccion((String)row.get("NOMBRE COLECCION"));
		d.setNº_isbn((String)row.get("Nº ISBN"));
		d.setAño_publicacion((Integer)row.get("AÑO PUBLICACION"));
		d.setNº_edicion((Integer)row.get("Nº EDICION"));
		d.setAño_c((Integer)row.get("AÑO C"));
		d.setNº_paginas((Integer)row.get("Nº PAGINAS"));
		d.setTomo((Integer)row.get("TOMO"));
		d.setTraductor((String)row.get("TRADUCTOR"));
		d.setCodigo((String)row.get("CODIGO"));
		d.setSitio((String)row.get("SITIO"));
		d.setAño_compra((String)row.get("AÑO COMPRA"));
		d.setPrecio((BigDecimal)row.get("PRECIO"));
		d.setNotas((String)row.get("NOTAS"));
		return d;
	}
	public static List<LibrosBean> listadorLibrosBean(Database db) {
		Table table;
		try {
			table = db.getTable("LIBROS");
		} catch (IOException e) {
			return null;
		}
		List<LibrosBean> list = new ArrayList<LibrosBean>();
		for (Row row : table) {
			LibrosBean p = Rellenador.rellenadorLibrosBean(row);
			list.add(p);
		}
		return list;
	}
	public static RevistasBean rellenadorRevistasBean(Row row) {
		RevistasBean d = new RevistasBean();
		d.setId_revista((Integer)row.get("Id REVISTA"));
		d.setTitulo_revista((String)row.get("TITULO REVISTA"));
		d.setNumero((Integer)row.get("NUMERO"));
		d.setMes_año((Integer)row.get("MES/AÑO"));
		d.setCodificacion((String)row.get("CODIFICACION"));
		d.setNº_isbn((String)row.get("Nº ISBN"));
		d.setNº_paginas((Integer)row.get("Nº PAGINAS"));
		d.setTemas((String)row.get("TEMAS"));
		d.setUbicacion((String)row.get("UBICACION"));
		d.setAño_compra((Integer)row.get("AÑO COMPRA"));
		d.setPrecio((Integer)row.get("PRECIO"));
		d.setNotas((String)row.get("NOTAS"));
		return d;
	}
	public static List<RevistasBean> listadorRevistasBean(Database db) {
		Table table;
		try {
			table = db.getTable("REVISTAS");
		} catch (IOException e) {
			return null;
		}
		List<RevistasBean> list = new ArrayList<RevistasBean>();
		for (Row row : table) {
			RevistasBean p = Rellenador.rellenadorRevistasBean(row);
			list.add(p);
		}
		return list;
	}
	public static Titulo_revistasBean rellenadorTitulo_revistasBean(Row row) {
		Titulo_revistasBean d = new Titulo_revistasBean();
		d.setNº_revista((Integer)row.get("Nº REVISTA"));
		d.setTitulo((String)row.get("TITULO"));
		d.setNombre_editorial((String)row.get("NOMBRE EDITORIAL"));
		return d;
	}
	public static List<Titulo_revistasBean> listadorTitulo_revistasBean(Database db) {
		Table table;
		try {
			table = db.getTable("TITULO REVISTAS");
		} catch (IOException e) {
			return null;
		}
		List<Titulo_revistasBean> list = new ArrayList<Titulo_revistasBean>();
		for (Row row : table) {
			Titulo_revistasBean p = Rellenador.rellenadorTitulo_revistasBean(row);
			list.add(p);
		}
		return list;
	}
	public static UbicacionBean rellenadorUbicacionBean(Row row) {
		UbicacionBean d = new UbicacionBean();
		d.setNº_ubicacion((Integer)row.get("Nº UBICACION"));
		d.setSitio((String)row.get("SITIO"));
		return d;
	}
	public static List<UbicacionBean> listadorUbicacionBean(Database db) {
		Table table;
		try {
			table = db.getTable("UBICACION");
		} catch (IOException e) {
			return null;
		}
		List<UbicacionBean> list = new ArrayList<UbicacionBean>();
		for (Row row : table) {
			UbicacionBean p = Rellenador.rellenadorUbicacionBean(row);
			list.add(p);
		}
		return list;
	}
}