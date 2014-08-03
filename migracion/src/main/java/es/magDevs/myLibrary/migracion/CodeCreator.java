package es.magDevs.myLibrary.migracion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

public class CodeCreator {
	public static void crearBeans() {
		File dbFile;
		Database db = null;
		try {
			URL url = CodeCreator.class.getResource("/BIBLIOTECA.mdb");
			dbFile = new File(url.toURI());
			db = DatabaseBuilder.open(dbFile);
			Set<String> tables = db.getTableNames();

			String beanDir = "." + File.separator + "src" + File.separator
					+ "main" + File.separator + "java" + File.separator + "es"
					+ File.separator + "magDevs" + File.separator + "myLibrary"
					+ File.separator + "migracion" + File.separator + "beans"
					+ File.separator;

			for (String table : tables) {
				String estructura = "";
				Table t = db.getTable(table);
				String tableName = t.getName().replace(" ", "_").toLowerCase();
				tableName = tableName.substring(0, 1).toUpperCase()
						+ tableName.substring(1) + "Bean";
				estructura += construirInicioClase(tableName);
				List<? extends Column> columns = t.getColumns();
				for (Column col : columns) {
					String atrib = construirAtributo(col.getName()
							.toLowerCase().replace('-', '_').replace('/', '_')
							.replace(' ', '_'), type2String(col.getType()));
					estructura += atrib;
				}
				for (Column col : columns) {
					String gt = construirGettetYsetter(col.getName()
							.toLowerCase().replace('-', '_').replace('/', '_')
							.replace(' ', '_'), type2String(col.getType()));
					estructura += gt + "\n";
				}

				estructura += "\n\n\tpublic String toString() {\n\t\treturn ";
				for (Column col : columns) {
					String var = col.getName().toLowerCase().replace('-', '_')
							.replace('/', '_').replace(' ', '_');
					estructura += "\""+var+"=\"+"+var+"+\" \"+";
					
				}
				estructura += "\"\";\n\t}\n";

				estructura += construirFinClase();

				File tableFile = new File(beanDir + tableName + ".java");
				tableFile.createNewFile();
				BufferedWriter w = new BufferedWriter(new FileWriter(tableFile));
				w.write(estructura);
				w.flush();
				w.close();
				// System.out.println(estructura);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void crearRellenadores() {
		File dbFile;
		Database db = null;
		try {
			URL url = CodeCreator.class.getResource("/BIBLIOTECA.mdb");
			dbFile = new File(url.toURI());
			db = DatabaseBuilder.open(dbFile);
			Set<String> tables = db.getTableNames();

			String fillerDir = "." + File.separator + "src" + File.separator
					+ "main" + File.separator + "java" + File.separator + "es"
					+ File.separator + "magDevs" + File.separator + "myLibrary"
					+ File.separator + "migracion" + File.separator;
			String estructura = "package es.magDevs.myLibrary.migracion;\n\n"
					+ "import com.healthmarketscience.jackcess.Row;\n"
					+ "import com.healthmarketscience.jackcess.Database;\n"
					+ "import com.healthmarketscience.jackcess.Table;\n"
					+ "import java.math.BigDecimal;\nimport java.util.Date;\n"
					+ "import java.io.IOException;\n"
					+ "import java.util.ArrayList;\n"
					+ "import java.util.List;\n" + "\n\n";

			for (String table : tables) {
				Table t = db.getTable(table);
				String tableName = t.getName().replace(" ", "_").toLowerCase();
				tableName = tableName.substring(0, 1).toUpperCase()
						+ tableName.substring(1) + "Bean";
				estructura += "import es.magDevs.myLibrary.migracion.beans."
						+ tableName + ";\n";
			}
			estructura += "\npublic class Rellenador {\n";

			for (String table : tables) {
				Table t = db.getTable(table);
				String tableName = t.getName().replace(" ", "_").toLowerCase();
				tableName = tableName.substring(0, 1).toUpperCase()
						+ tableName.substring(1) + "Bean";
				estructura += "\tpublic static " + tableName + " rellenador"
						+ tableName + "(Row row) {\n\t\t" + tableName
						+ " d = new " + tableName + "();\n";
				List<? extends Column> columns = t.getColumns();
				for (Column col : columns) {
					String nombreAtributo = col.getName().toLowerCase()
							.replace('-', '_').replace('/', '_')
							.replace(' ', '_');
					String nombreMayus = nombreAtributo.substring(0, 1)
							.toUpperCase() + nombreAtributo.substring(1);
					estructura += "\t\td.set" + nombreMayus + "(("
							+ type2String(col.getType()) + ")row.get(\""
							+ col.getName() + "\"));\n";
				}
				estructura += "\t\treturn d;\n\t}\n";

				estructura += "\tpublic static List<" + tableName
						+ "> listador" + tableName + "(Database db) {\n";
				estructura += "\t\tTable table;\n" + "\t\ttry {\n"
						+ "\t\t\ttable = db.getTable(\"" + table + "\");\n"
						+ "\t\t} catch (IOException e) {\n"
						+ "\t\t\treturn null;\n" + "\t\t}\n";
				estructura += "\t\tList<" + tableName
						+ "> list = new ArrayList<" + tableName + ">();\n"
						+ "\t\tfor (Row row : table) {\n" + "\t\t\t"
						+ tableName + " p = Rellenador.rellenador" + tableName
						+ "(row);\n\t\t\tlist.add(p);\n" + "\t\t}\n\t\treturn list;\n\t}\n";
			}
			estructura += "}";
			// System.out.println(estructura);
			File tableFile = new File(fillerDir + "Rellenador" + ".java");
			tableFile.createNewFile();
			BufferedWriter w = new BufferedWriter(new FileWriter(tableFile));
			w.write(estructura);
			w.flush();
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void crearManager() {
		File dbFile;
		Database db = null;
		try {
			URL url = CodeCreator.class.getResource("/BIBLIOTECA.mdb");
			dbFile = new File(url.toURI());
			db = DatabaseBuilder.open(dbFile);
			Set<String> tables = db.getTableNames();

			String manDir = "." + File.separator + "src" + File.separator
					+ "main" + File.separator + "java" + File.separator + "es"
					+ File.separator + "magDevs" + File.separator + "myLibrary"
					+ File.separator + "migracion" + File.separator
					+ "managers" + File.separator;

			for (String table : tables) {
				String estructura = "";
				Table t = db.getTable(table);
				String tableName = t.getName().replace(" ", "_").toLowerCase();
				String claseName = tableName.substring(0, 1).toUpperCase()
						+ tableName.substring(1) + "Manager";
				estructura += "package es.magDevs.myLibrary.migracion.managers;\n\nimport java.math.BigDecimal;\nimport java.util.Date;\n\npublic class ";
				estructura += claseName;
				estructura += " {\n";

				List<? extends Column> columns = t.getColumns();
				for (Column col : columns) {
					String atrib = construirAtributo(col.getName()
							.toLowerCase().replace('-', '_').replace('/', '_')
							.replace(' ', '_'), type2String(col.getType()));
					estructura += atrib;
				}
				for (Column col : columns) {
					String gt = construirGettetYsetter(col.getName()
							.toLowerCase().replace('-', '_').replace('/', '_')
							.replace(' ', '_'), type2String(col.getType()));
					estructura += gt + "\n";
				}
				estructura += construirFinClase();

				File tableFile = new File(manDir + tableName + ".java");
				tableFile.createNewFile();
				BufferedWriter w = new BufferedWriter(new FileWriter(tableFile));
				w.write(estructura);
				w.flush();
				w.close();
				// System.out.println(estructura);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	static public String construirInicioClase(String nombreClase) {
		String inicio = "package es.magDevs.myLibrary.migracion.beans;\n\nimport java.math.BigDecimal;\nimport java.util.Date;\n\npublic class ";
		inicio += nombreClase;
		inicio += " {\n";
		return inicio;
	}

	static public String construirFinClase() {
		return "}";
	}

	static public String construirAtributo(String nombreAtributo, String tipo) {
		String inicio = "\tprivate " + tipo + " " + nombreAtributo + ";\n";
		return inicio;
	}

	static public String construirGettetYsetter(String nombreAtributo,
			String tipo) {
		String nombreMayus = nombreAtributo.substring(0, 1).toUpperCase()
				+ nombreAtributo.substring(1);
		String getter = "\tpublic " + tipo + " get" + nombreMayus
				+ "() {\n\t\treturn this." + nombreAtributo + ";\n\t}\n";
		String setter = "\tpublic void set" + nombreMayus + "(" + tipo + " "
				+ nombreAtributo + ") {\n\t\tthis." + nombreAtributo + "="
				+ nombreAtributo + ";\n\t}";
		return getter + setter;
	}

	static public String type2String(DataType tipo) {
		if (tipo.equals(DataType.BINARY) || tipo.equals(DataType.OLE)) {
			return "byte[]";
		} else if (tipo.equals(DataType.BOOLEAN)) {
			return "Boolean";
		} else if (tipo.equals(DataType.BYTE)) {
			return "Byte";
		} else if (tipo.equals(DataType.COMPLEX_TYPE)
				|| tipo.equals(DataType.LONG)) {
			return "Integer";
		} else if (tipo.equals(DataType.DOUBLE)) {
			return "Double";
		} else if (tipo.equals(DataType.FLOAT)) {
			return "Float";
		} else if (tipo.equals(DataType.INT)) {
			return "Short";
		} else if (tipo.equals(DataType.MONEY) || tipo.equals(DataType.NUMERIC)) {
			return "BigDecimal";
		} else if (tipo.equals(DataType.SHORT_DATE_TIME)) {
			return "Date";
		} else {
			return "String";
		}
	}
}
