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
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.beans.Traductor;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.TraductorDao;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;

/**
 * Controlador para la seccion de traductores
 * 
 * @author javier.vaqueror
 * 
 */
public class TranslatorsController extends AbstractController {

	public TranslatorsController(MessageSource messageSource) {
		super(messageSource);
	}

	// LOG
	private static final Logger log = Logger
			.getLogger(TranslatorsController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.TRANSLATORS;
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
		return DaoFactory.getTraductorDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Traductor();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processTranslatorsFilter((Traductor) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processTranslator((Traductor) newData,
				messageSource);
	}

	/* *****************************************
	 * ************** ACCIONES *****************
	 * *****************************************
	 */
	@Override
	public String read(Integer index, Model model) {
		Traductor elementData = null;
		List<Libro> translatorBooks = null;
		String msg = "";

		// Si tenemos un indice valido
		if (index >= 0 && data != null) {
			// Obtenemos todos los datos del libro seleccionado
			TraductorDao dao = DaoFactory.getTraductorDao();
			try {
				elementData = (Traductor) dao.get(((Bean) data.get(index))
						.getId());
				translatorBooks = dao.getLibrosTraductor(((Bean) data
						.get(index)).getId());
			} catch (Exception e) {
				if (elementData == null) {
					elementData = new Traductor();
				}
				translatorBooks = new ArrayList<Libro>();
				msg = manageException("read", e);
			}
		} else {
			// Creamos un traductor vacio, para que no de fallos al intentar
			// acceder
			// a algunos campos
			elementData = new Traductor();
			translatorBooks = new ArrayList<Libro>();
			msg = messageSource.getMessage("translators.menu.read.noIndexMsg",
					null, LocaleContextHolder.getLocale());
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.READ,
				getSection()));
		model.addAttribute("elementData", elementData);
		model.addAttribute("translatorBooks", translatorBooks);
		return "commons/body";
	}

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos de traductores
	 * 
	 * @param hint
	 * @return
	 */
	public List<Traductor> getData(String hint) {
		TraductorDao dao = DaoFactory.getTraductorDao();
		try {
			return dao.getTraductores(hint);
		} catch (Exception e) {
			return new ArrayList<Traductor>();
		}
	}
}
