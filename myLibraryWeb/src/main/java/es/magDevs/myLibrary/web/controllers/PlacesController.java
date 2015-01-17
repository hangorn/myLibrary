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
import es.magDevs.myLibrary.model.beans.Ubicacion;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.UbicacionDao;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;

/**
 * Controlador para la seccion de ubicaciones
 * 
 * @author javier.vaquero
 * 
 */
public class PlacesController extends AbstractController {
	public PlacesController(MessageSource messageSource) {
		super(messageSource);
	}

	// LOG
	private static final Logger log = Logger.getLogger(PlacesController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.PLACES;
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
		return DaoFactory.getUbicacionDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Ubicacion();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processPlacesFilter((Ubicacion) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processPlace((Ubicacion) newData, messageSource);
	}

	/* *****************************************
	 * ************** ACCIONES *****************
	 * *****************************************
	 */
	@Override
	public String read(Integer index, Model model) {
		Ubicacion elementData = null;
		List<Libro> placeBooks = null;
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && data != null) {
			// Obtenemos todos los datos del libro seleccionado
			UbicacionDao dao = DaoFactory.getUbicacionDao();
			try {
				elementData = (Ubicacion) dao.get(((Bean) data.get(index))
						.getId());
				placeBooks = dao.getLibrosUbicacion(((Bean) data.get(index))
						.getId());
			} catch (Exception e) {
				if (elementData == null) {
					elementData = new Ubicacion();
				}
				placeBooks = new ArrayList<Libro>();
				msg = manageException("read", e);
			}
		} else {
			// Creamos un ubicacion vacio, para que no de fallos al intentar
			// acceder a algunos campos
			elementData = new Ubicacion();
			placeBooks = new ArrayList<Libro>();
			msg = messageSource.getMessage("places.menu.read.noIndexMsg", null,
					LocaleContextHolder.getLocale());
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.READ,
				getSection()));
		model.addAttribute("elementData", elementData);
		model.addAttribute("placeBooks", placeBooks);
		return "commons/body";
	}
}
