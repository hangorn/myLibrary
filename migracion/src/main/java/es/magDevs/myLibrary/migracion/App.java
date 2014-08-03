package es.magDevs.myLibrary.migracion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

import es.magDevs.myLibrary.migracion.beans.AutorBean;
import es.magDevs.myLibrary.migracion.beans.CodificacionBean;
import es.magDevs.myLibrary.migracion.beans.ColeccionBean;
import es.magDevs.myLibrary.migracion.beans.EditorialBean;
import es.magDevs.myLibrary.migracion.beans.LibrosBean;
import es.magDevs.myLibrary.migracion.beans.UbicacionBean;

/**
 * Hello world!
 * 
 */
public class App {
	static Database db;
	static Table table;

	public static void main(String[] args) {
		// CodeCreator.crearBeans();
		// CodeCreator.crearRellenadores();

		URL url = App.class.getResource("/BIBLIOTECA.mdb");
		File dbFile;
		try {
			String resourceDir = "." + File.separator + "src" + File.separator
					+ "main" + File.separator + "resources" + File.separator;
			File file;
			BufferedWriter w;
			dbFile = new File(url.toURI());
			db = DatabaseBuilder.open(dbFile);

			String insertsTipos = "";
			Map<String, Integer> mapTipos = new HashMap<String, Integer>();
			List<CodificacionBean> codificaciones = Rellenador
					.listadorCodificacionBean(db);
			for (CodificacionBean bean : codificaciones) {
				String idTipo = "" + bean.getNúmero_codificacion();
				insertsTipos += Insertador.insertsCodificaciones(bean) + "\n";
				mapTipos.put(idTipo, bean.getNúmero_codificacion());
			}
//			file = new File(resourceDir + "insertCodificaciones" + ".sql");
//			file.createNewFile();
//			w = new BufferedWriter(new FileWriter(file));
//			w.write(insertsTipos);
//			w.flush();
//			w.close();

			String insertsUbi = "";
			Map<String, Integer> mapUbicaciones = new HashMap<String, Integer>();
			List<UbicacionBean> ubis = Rellenador.listadorUbicacionBean(db);
			for (UbicacionBean bean : ubis) {
				String idUbicacion = "" + bean.getNº_ubicacion();
				insertsUbi += Insertador.insertsUbicaciones(bean) + "\n";
				mapUbicaciones.put(idUbicacion, bean.getNº_ubicacion());
			}
//			file = new File(resourceDir + "insertUbicaciones" + ".sql");
//			file.createNewFile();
//			w = new BufferedWriter(new FileWriter(file));
//			w.write(insertsUbi);
//			w.flush();
//			w.close();

			String insertsAutor = "";
			Map<String, Integer> mapAutores = new HashMap<String, Integer>();
			List<AutorBean> autor = Rellenador.listadorAutorBean(db);
			for (AutorBean bean : autor) {
				String idAutor = "" + bean.getNº_autor();
				insertsAutor += Insertador.insertAutores(bean) + "\n";
				mapAutores.put(idAutor, bean.getNº_autor());
			}
//			file = new File(resourceDir + "insertAutores" + ".sql");
//			file.createNewFile();
//			w = new BufferedWriter(new FileWriter(file));
//			w.write(insertsAutor);
//			w.flush();
//			w.close();

			String insertsEd = "";
			Map<String, Integer> mapEditoriales = new HashMap<String, Integer>();
			List<EditorialBean> ed = Rellenador.listadorEditorialBean(db);
			for (EditorialBean bean : ed) {
				String idEditorial = "" + bean.getNº_editorial();
				insertsEd += Insertador.insertEditoriales(bean) + "\n";
				mapEditoriales.put(idEditorial,
						new Integer(bean.getNº_editorial()));
			}
//			file = new File(resourceDir + "insertEditoriales" + ".sql");
//			file.createNewFile();
//			w = new BufferedWriter(new FileWriter(file));
//			w.write(insertsEd);
//			w.flush();
//			w.close();

			String insertsCol = "";
			Map<String, Integer> mapColecciones = new HashMap<String, Integer>();
			List<ColeccionBean> col = Rellenador.listadorColeccionBean(db);
			for (ColeccionBean bean : col) {
				String idColeccion = "" + bean.getNº_coleccion();
				bean.setNombre_editorial(""
						+ mapEditoriales.get(bean.getNombre_editorial()));
				insertsCol += Insertador.insertColecciones(bean) + "\n";
				mapColecciones.put(idColeccion,
						new Integer(bean.getNº_coleccion()));
			}
//			file = new File(resourceDir + "insertColecciones" + ".sql");
//			file.createNewFile();
//			w = new BufferedWriter(new FileWriter(file));
//			w.write(insertsCol);
//			w.flush();
//			w.close();

			String insertsLib = "";
			String insertsTra = "";
			String insertsLibAu = "";
			String insertsLibTra = "";
			int contadorTraductores = 1;
			Map<String, Integer> mapTraductores = new HashMap<String, Integer>();
			Map<String, Integer> mapLibros = new HashMap<String, Integer>();
			List<LibrosBean> lib = Rellenador.listadorLibrosBean(db);
			for (LibrosBean bean : lib) {
				// Fijamos la editorial asociada
				bean.setNombre_editorial(""
						+ mapEditoriales.get(bean.getNombre_editorial()));
				// Fijamos el tipo asociado
				bean.setCodigo("" + mapTipos.get(bean.getCodigo()));
				// Fijamos la ubicacion asociada
				bean.setSitio("" + mapUbicaciones.get(bean.getSitio()));
				// Fijamos la coleccion asociada
				bean.setNombre_coleccion(""
						+ mapColecciones.get(bean.getNombre_coleccion()));
				// Mapeo para los libros
				String idLibro = "" + bean.getNº_libro();
				insertsLib += Insertador.insertLibros(bean) + "\n";
				mapLibros.put(idLibro, new Integer(bean.getNº_libro()));

				// Si tenemos traductor valido
				if (bean.getTraductor() != null
						&& !bean.getTraductor().toUpperCase().equals("NULL")) {
					// Comprobamos si ya tenemos el traductor guardado
					if (!mapTraductores.containsKey(bean.getTraductor())) {
						mapTraductores.put(bean.getTraductor(),
								contadorTraductores);
						contadorTraductores++;
						insertsTra += Insertador.insertTraductores(bean
								.getTraductor()) + "\n";
					}
					// Añadimos la realcion libro/traductor
					insertsLibTra += Insertador.insertLibrosTraductores(
							bean.getNº_libro(),
							mapTraductores.get(bean.getTraductor())) + "\n";
				}
				// Si tenemos autor valido
				if (bean.getApellidos() != null
						&& !bean.getApellidos().toUpperCase().equals("NULL")) {
					// Añadimos la relacion libro/autor
					insertsLibAu += Insertador.insertLibrosAutores(
							bean.getNº_libro(),
							mapAutores.get(bean.getApellidos())) + "\n";
				}
			}
			file = new File(resourceDir + "insertLibros" + ".sql");
			file.createNewFile();
			w = new BufferedWriter(new FileWriter(file));
			w.write(insertsLib);
			w.flush();
			w.close();
//			file = new File(resourceDir + "insertTraductores" + ".sql");
//			file.createNewFile();
//			w = new BufferedWriter(new FileWriter(file));
//			w.write(insertsTra);
//			w.flush();
//			w.close();
			file = new File(resourceDir + "insertLibrosTraductores" + ".sql");
			file.createNewFile();
			w = new BufferedWriter(new FileWriter(file));
			w.write(insertsLibTra);
			w.flush();
			w.close();
			file = new File(resourceDir + "insertLibrosAutores" + ".sql");
			file.createNewFile();
			w = new BufferedWriter(new FileWriter(file));
			w.write(insertsLibAu);
			w.flush();
			w.close();

			for (CodificacionBean editorialBean : codificaciones) {
				System.out.println(editorialBean);
			}
			for (UbicacionBean editorialBean : ubis) {
				System.out.println(editorialBean);
			}
			for (AutorBean editorialBean : autor) {
				System.out.println(editorialBean);
			}
			for (EditorialBean editorialBean : ed) {
				System.out.println(editorialBean);
			}
			for (ColeccionBean editorialBean : col) {
				System.out.println(editorialBean);
			}
			//

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
