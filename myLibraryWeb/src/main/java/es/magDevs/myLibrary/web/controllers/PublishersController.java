package es.magDevs.myLibrary.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;

import es.magDevs.myLibrary.model.Constants.ACTION;
import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.EditorialDao;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;

/**
 * Controlador para la seccion de editoriales
 * 
 * @author javier.vaquero
 * 
 */
public class PublishersController extends AbstractController {
	public PublishersController(MessageSource messageSource) {
		super(messageSource);
	}
	
	// LOG
	private static final Logger log = Logger.getLogger(PublishersController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.PUBLISHERS;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Logger getLog() {
		return log;
	}

	/**
	 * {@inheritDoc}
	 */
	protected AbstractDao getDao() {
		return DaoFactory.getEditorialDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Editorial();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processPublishersFilter((Editorial) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processPublisher((Editorial) newData, messageSource);
	}

	/* *****************************************
	 * ************* EDITORIALES ***************
	 * *****************************************
	 */
	@Override
	public String read(Integer index, Model model) {
		Editorial elementData = null;
		List<Libro> publisherBooks = null;
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && data != null) {
			// Obtenemos todos los datos del libro seleccionado
			EditorialDao dao = DaoFactory.getEditorialDao();
			try {
				elementData = (Editorial) dao.get(((Bean)data.get(index)).getId());
				publisherBooks =  dao.getLibrosEditorial(((Bean)data.get(index)).getId());
			} catch (Exception e) {
				if (elementData == null) {
					elementData = new Editorial();
				}
				publisherBooks = new ArrayList<Libro>();
				msg = manageException("read", e);
			}
		} else {
			// Creamos una editorial vacio, para que no de fallos al intentar acceder
			// a algunos campos
			elementData = new Editorial();
			publisherBooks = new ArrayList<Libro>();
			msg = messageSource.getMessage("publishers.menu.read.noIndexMsg", null,
					LocaleContextHolder.getLocale());
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.READ, getSection()));
		model.addAttribute("elementData", elementData);
		model.addAttribute("publisherBooks", publisherBooks);
		return "commons/body";
	}

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos de editoriales
	 * 
	 * @param hint
	 * @return
	 */
	public List<Editorial> getData(String hint) {
		EditorialDao dao = DaoFactory.getEditorialDao();
		try {
			return dao.getEditoriales(hint);
		} catch (Exception e) {
			log.error("Error los datos mediante peticion AJAX"
					+ " de las editoriales", e);
			return new ArrayList<Editorial>();
		}
	}
}
