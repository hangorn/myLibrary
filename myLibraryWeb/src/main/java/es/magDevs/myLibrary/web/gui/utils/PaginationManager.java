package es.magDevs.myLibrary.web.gui.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Gestiona las opciones de paginacion de un apartado
 * 
 * @author javi
 * 
 */
public class PaginationManager {
	// Spring Message Source
	@Autowired
	private MessageSource messageSource;

	private int page;
	private int pageCount;
	private int pageSize;
	private int elementsCount;

	public PaginationManager(MessageSource messageSource, int elementsCount) {
		if (getPage() < 1) {
			setPage(1);
		}
		if (getPageSize() < 1) {
			try {
				setPageSize(Integer.parseInt(messageSource.getMessage(
						"menu.pag.defaultSize", null, null)));
			} catch (Exception e) {
				setPageSize(30);
			}
		}
		setElementsCount(elementsCount);
		setPageCount((int) Math.ceil((double) getElementsCount()
				/ (double) getPageSize()));
	}

	/**
	 * Cambia los datos de la paginacion para pasar a la pagina siguiente
	 * 
	 * @return true si se cambia de pagina, false si no
	 */
	public boolean next() {
		if (getPage() < getPageCount()) {
			page++;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Cambia los datos de la paginacion para pasar a la pagina anterior
	 * 
	 * @return true si se cambia de pagina, false si no
	 */
	public boolean previous() {
		if (getPage() > 1) {
			page--;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Cambia los datos de la paginacion para pasar a la primera pagina
	 * 
	 * @return true si se cambia de pagina, false si no
	 */
	public boolean start() {
		if (getPage() != 1) {
			setPage(1);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Cambia los datos de la paginacion para pasar a la ultima pagina
	 * 
	 * @return true si se cambia de pagina, false si no
	 */
	public boolean end() {
		if (getPage() != getPageCount()) {
			setPage(getPageCount());
			return true;
		} else {
			return false;
		}
	}

	public boolean setPageSize(String pageSize) {
		int pagesizeTmp;
		try {
			pagesizeTmp = Integer.parseInt(pageSize);
			setPageSize(pagesizeTmp);
			setPage(1);
			setPageCount((int) Math.ceil((double) getElementsCount()
					/ (double) getPageSize()));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Obtiene le texto a mostrar en la parte izquierda del texto de la
	 * paginacion
	 * 
	 * @return
	 */
	public String getPageLabel() {
		int iniPag = (getPage() - 1) * getPageSize() + 1;
		int endPag = Math.min((getPage() - 1) * getPageSize() + getPageSize(),
				getElementsCount());
		return "" + iniPag + "-" + endPag;

	}

	/**
	 * Obtiene el texto a mostrar en la parte derecha del texto de la paginacion
	 * 
	 * @return
	 */
	public String getPageCountLabel() {
		return "" + getElementsCount();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getElementsCount() {
		return elementsCount;
	}

	public void setElementsCount(int elementsCount) {
		this.elementsCount = elementsCount;
	}
}
