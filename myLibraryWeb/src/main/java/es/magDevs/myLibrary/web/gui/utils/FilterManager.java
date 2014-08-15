package es.magDevs.myLibrary.web.gui.utils;

import org.apache.commons.lang3.StringUtils;

import es.magDevs.myLibrary.web.gui.beans.filters.BooksFilter;

/**
 * Realizara diversas operaciones sobre los filtros disponibles
 * 
 * @author javi
 * 
 */
public class FilterManager {
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
		// Comprobamos si tenemos algun filtro por autor
		boolean authorFiltered = filter.getAutores() != null
				&& filter.getAutores().size() > 0
				&& (!StringUtils.isBlank(filter.getAutor().getNombre())
						|| !StringUtils.isBlank(filter.getAutor()
								.getApellidos())
						|| !StringUtils.isBlank(filter.getAutor().getPais()) || filter
						.getAutor().getAnnoNacimiento() != null);
		// Si no tenemos ningun filtro activo devolvemos 'null'
		if (StringUtils.isBlank(filter.getTitulo())
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
				&& filter.getAnnoCompra() == null
				&& filter.getNumPaginas() == null) {
			return null;
		}
		return filter;
	}
}
