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
