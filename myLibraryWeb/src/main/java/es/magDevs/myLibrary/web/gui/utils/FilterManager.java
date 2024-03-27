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
package es.magDevs.myLibrary.web.gui.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import es.magDevs.myLibrary.model.Constants;
import es.magDevs.myLibrary.model.beans.Autor;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Leido;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Pendiente;
import es.magDevs.myLibrary.model.beans.Prestamo;
import es.magDevs.myLibrary.model.beans.Tipo;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.beans.Usuario;
import es.magDevs.myLibrary.web.conf.ConfUserDetailsService.AuthenticatedUser;
import es.magDevs.myLibrary.web.controllers.main.MainController;
import es.magDevs.myLibrary.web.gui.beans.filters.BooksFilter;

/**
 * Realizara diversas operaciones sobre los filtros disponibles
 * 
 * @author javier.vaquero
 * 
 */
public class FilterManager {
	private static boolean sinOrdenacion(Bean bean) {
		return StringUtils.isEmpty(bean.getSortedColumn());
	}
	
	/**
	 * Procesa el filtro suministrado para obtener unicamente un filtro en caso
	 * de que haya que filtrar. En caso de que no existe ningun criterio de
	 * busqueda, devolvera null para evitar busquedas innecesarias.
	 * 
	 * @param filter
	 * @return el filtro tal cual si existe al menos un criterio para filtrar, o
	 *         {@code null} si no existe ningun criterio
	 */
	public static BooksFilter processBooksFilter(BooksFilter filter) {
		Integer user = MainController.getUserid();
		if (user != null) {
			if (filter == null) {
				filter = new BooksFilter();
			}
			filter.setUsuarioRegistrado(new Usuario(user, null, null, null, null, null, null));
		}
		if (filter == null) {
			return null;
		}
		// Comprobamos si tenemos algun filtro por autor
		boolean authorFiltered = filter.getAutores() != null
				&& filter.getAutores().size() > 0
				&& (!StringUtils.isBlank(filter.getAutor().getNombre())
						|| !StringUtils.isBlank(filter.getAutor()
								.getApellidos())
						|| !StringUtils.isBlank(filter.getAutor().getPais()) || filter
						.getAutor().getAnnoNacimiento() != null);
		// Si no tenemos ningun filtro activo devolvemos 'null'
		if (sinOrdenacion(filter)
				&& filter.getUsuarioRegistrado() == null
				&& StringUtils.isBlank(filter.getTitulo())
				&& !authorFiltered
				&& (filter.getEditorial() == null || StringUtils.isBlank(filter
						.getEditorial().getNombre()))
				&& (filter.getUbicacion() == null || StringUtils.isBlank(filter
						.getUbicacion().getCodigo()))
				&& (filter.getColeccion() == null || StringUtils.isBlank(filter
						.getColeccion().getNombre()))
				&& (filter.getTipo() == null || StringUtils.isBlank(filter
						.getTipo().getDescripcion()))
				&& StringUtils.isBlank(filter.getNotas())
				&& StringUtils.isBlank(filter.getIsbn())
				&& StringUtils.isBlank(filter.getCb())
				&& filter.getAnnoCompra() == null
				&& filter.getNumPaginas() == null
				&& filter.getMeGustaUsr() == null) {
			return null;
		}
		return filter;
	}

	/**
	 * Procesa el filtro suministrado para obtener unicamente un filtro en caso
	 * de que haya que filtrar. En caso de que no existe ningun criterio de
	 * busqueda, devolvera null para evitar busquedas innecesarias.
	 * 
	 * @param filter
	 * @return el filtro tal cual si existe al menos un criterio para filtrar, o
	 *         {@code null} si no existe ningun criterio
	 */
	public static Autor processAuthorsFilter(Autor filter) {
		if (filter == null) {
			return null;
		}
		// Si no tenemos ningun filtro activo devolvemos 'null'
		if (sinOrdenacion(filter)
				&& StringUtils.isBlank(filter.getNombre())
				&& StringUtils.isBlank(filter.getApellidos())
				&& StringUtils.isBlank(filter.getCiudad())
				&& StringUtils.isBlank(filter.getPais())
				&& StringUtils.isBlank(filter.getNotas())
				&& filter.getAnnoFallecimiento() == null
				&& filter.getAnnoNacimiento() == null) {
			return null;
		}
		return filter;
	}

	/**
	 * Procesa el filtro suministrado para obtener unicamente un filtro en caso
	 * de que haya que filtrar. En caso de que no existe ningun criterio de
	 * busqueda, devolvera null para evitar busquedas innecesarias.
	 * 
	 * @param filter
	 * @return el filtro tal cual si existe al menos un criterio para filtrar, o
	 *         {@code null} si no existe ningun criterio
	 */
	public static Editorial processPublishersFilter(Editorial filter) {
		if (filter == null) {
			return null;
		}
		// Si no tenemos ningun filtro activo devolvemos 'null'
		if (sinOrdenacion(filter)
				&& StringUtils.isBlank(filter.getNombre())
				&& StringUtils.isBlank(filter.getCiudad())) {
			return null;
		}
		return filter;
	}

	/**
	 * Procesa el filtro suministrado para obtener unicamente un filtro en caso
	 * de que haya que filtrar. En caso de que no existe ningun criterio de
	 * busqueda, devolvera null para evitar busquedas innecesarias.
	 * 
	 * @param filter
	 * @return el filtro tal cual si existe al menos un criterio para filtrar, o
	 *         {@code null} si no existe ningun criterio
	 */
	public static Coleccion processCollectionsFilter(Coleccion filter) {
		if (filter == null) {
			return null;
		}
		// Si no tenemos ningun filtro activo devolvemos 'null'
		if (sinOrdenacion(filter)
				&& StringUtils.isBlank(filter.getNombre())
				&& (filter.getEditorial() == null || StringUtils.isBlank(filter
						.getEditorial().getNombre()))) {
			return null;
		}
		return filter;
	}

	/**
	 * Procesa el filtro suministrado para obtener unicamente un filtro en caso
	 * de que haya que filtrar. En caso de que no existe ningun criterio de
	 * busqueda, devolvera null para evitar busquedas innecesarias.
	 * 
	 * @param filter
	 * @return el filtro tal cual si existe al menos un criterio para filtrar, o
	 *         {@code null} si no existe ningun criterio
	 */
	public static Tipo processTypesFilter(Tipo filter) {
		if (filter == null) {
			return null;
		}
		// Si no tenemos ningun filtro activo devolvemos 'null'
		if (sinOrdenacion(filter)
				&& StringUtils.isBlank(filter.getDescripcion())) {
			return null;
		}
		return filter;
	}

	/**
	 * Procesa el filtro suministrado para obtener unicamente un filtro en caso
	 * de que haya que filtrar. En caso de que no existe ningun criterio de
	 * busqueda, devolvera null para evitar busquedas innecesarias.
	 * 
	 * @param filter
	 * @return el filtro tal cual si existe al menos un criterio para filtrar, o
	 *         {@code null} si no existe ningun criterio
	 */
	public static Ubicacion processPlacesFilter(Ubicacion filter) {
		if (filter == null) {
			return null;
		}
		// Si no tenemos ningun filtro activo devolvemos 'null'
		if (sinOrdenacion(filter)
				&& StringUtils.isBlank(filter.getDescripcion())
				&& StringUtils.isBlank(filter.getCodigo())) {
			return null;
		}
		return filter;
	}

	/**
	 * Procesa el filtro suministrado para obtener unicamente un filtro en caso
	 * de que haya que filtrar. En caso de que no existe ningun criterio de
	 * busqueda, devolvera null para evitar busquedas innecesarias.
	 * 
	 * @param filter
	 * @return el filtro tal cual si existe al menos un criterio para filtrar, o
	 *         {@code null} si no existe ningun criterio
	 */
	public static Traductor processTranslatorsFilter(Traductor filter) {
		if (filter == null) {
			return null;
		}
		// Si no tenemos ningun filtro activo devolvemos 'null'
		if (sinOrdenacion(filter)
				&& StringUtils.isBlank(filter.getNombre())) {
			return null;
		}
		return filter;
	}

	public static Bean processLendsFilter(Prestamo filter) {
		boolean isAdmin = false, isUser = false;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			for (GrantedAuthority authority : authentication.getAuthorities()) {
				String a = authority.getAuthority();
				if (a != null && a.equals(Constants.ROLE_ROLE_ADMIN)) {
					isAdmin = true;
				}
				if (a != null && a.equals(Constants.ROLE_ROLE_USER)) {
					isUser = true;
				}
			}
		}
		if (filter != null && filter.getLibro() != null && StringUtils.isBlank(filter.getLibro().getTitulo())) {
			filter.setLibro(null);
		}
		if (isAdmin)  {
			// Si es administrador y no hay filtros, no filtramos por nada
			if (filter == null || (sinOrdenacion(filter)
					&& (filter.getLibro() == null || StringUtils.isBlank(filter.getLibro().getTitulo())) &&
					StringUtils.isEmpty(filter.getAutoresTxt()) &&
					(filter.getUsuario() == null || StringUtils.isBlank(filter.getUsuario().getNombre())))) {
				return null;
			}
		} else if (isUser) {
			// Si es usuario normal, añadimos el filtro para que solo muetre los pretamos al usuario
			if (filter == null) {
				filter = new Prestamo();
			}
			if (filter.getUsuario() == null) {
				filter.setUsuario(new Usuario());
			}
			filter.getUsuario().setId(((AuthenticatedUser) authentication.getPrincipal()).getId());
		} else {
			// Si no esta autenticado, filtramos por algo para que no se obtengan resultados
			filter = new Prestamo(null, new Libro(), null, null);
			filter.getLibro().setId(-1);
		}
		return filter;
	}

	public static Bean processUsersFilter(Usuario filter) {
		if (filter == null || (sinOrdenacion(filter)
				&& StringUtils.isBlank(filter.getUsername()) && StringUtils.isBlank(filter.getNombre()) && 
				StringUtils.isBlank(filter.getEmail()))) {
			return null;
		}
		return filter;
	}

	public static Bean processPendingFilter(Pendiente filter) {
		Integer userid = MainController.getUserid();
		if (filter == null) {
			filter = new Pendiente();
		}
		if (filter.getLibro() == null) {
			filter.setLibro(new Libro());
		}
		filter.getLibro().setId(null);
		if (userid != null) {
			if (filter.getUsuario() == null) {
				filter.setUsuario(new Usuario());
			}
			filter.getUsuario().setId(userid);
		} else {
			// Si no esta autenticado, filtramos por algo para que no se obtengan resultados
			filter = new Pendiente(null, new Libro(), null, null);
			filter.getLibro().setId(-1);
		}
		return filter;
	}

	public static Bean processReadFilter(Leido filter) {
		Integer userid = MainController.getUserid();
		if (filter == null) {
			filter = new Leido();
		}
		if (filter.getLibro() == null) {
			filter.setLibro(new Libro());
		}
		filter.getLibro().setId(null);
		
		if (StringUtils.isNotBlank(filter.getFechaMinTxt())) {
			filter.setFechaMin(DatesManager.string2Int(filter.getFechaMinTxt()));
		}
		if (StringUtils.isNotBlank(filter.getFechaMaxTxt())) {
			filter.setFechaMax(DatesManager.string2Int(filter.getFechaMaxTxt()));
		}
		
		if (userid != null) {
			if (filter.getUsuario() == null) {
				filter.setUsuario(new Usuario());
			}
			filter.getUsuario().setId(userid);
		} else {
			// Si no esta autenticado, filtramos por algo para que no se obtengan resultados
			filter = new Leido(null, new Libro(), null, null);
			filter.getLibro().setId(-1);
		}
		return filter;
	}
}
