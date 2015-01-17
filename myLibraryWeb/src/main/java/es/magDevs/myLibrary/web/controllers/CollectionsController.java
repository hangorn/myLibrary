package es.magDevs.myLibrary.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;

import es.magDevs.myLibrary.model.Constants.ACTION;
import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Coleccion;
import es.magDevs.myLibrary.model.beans.Editorial;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.model.dao.ColeccionDao;
import es.magDevs.myLibrary.model.dao.EditorialDao;
import es.magDevs.myLibrary.web.gui.utils.FilterManager;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;
import es.magDevs.myLibrary.web.gui.utils.NewDataManager;
import es.magDevs.myLibrary.web.gui.utils.PaginationManager;

/**
 * Controlador para la seccion de colecciones
 * 
 * @author javier.vaquero
 * 
 */
public class CollectionsController extends AbstractController {

	public CollectionsController(MessageSource messageSource) {
		super(messageSource);
	}

	// LOG
	private static final Logger log = Logger
			.getLogger(CollectionsController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.COLLECTIONS;
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
		return DaoFactory.getColeccionDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Coleccion();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return FilterManager.processCollectionsFilter((Coleccion) filter);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return NewDataManager.processCollection((Coleccion) newData,
				messageSource);
	}

	/* *****************************************
	 * ************** ACCIONES *****************
	 * *****************************************
	 */
	private Editorial newPublisher;

	@Override
	public String read(Integer index, Model model) {
		Coleccion elementData = null;
		List<Libro> collectionBooks = null;
		String msg = "";
		// Si tenemos un indice valido
		if (index >= 0 && data != null) {
			// Obtenemos todos los datos del libro seleccionado
			ColeccionDao dao = DaoFactory.getColeccionDao();
			try {
				elementData = (Coleccion) dao.get(((Bean) data.get(index))
						.getId());
				collectionBooks = dao.getLibrosColeccion(((Bean) data
						.get(index)).getId());
			} catch (Exception e) {
				if (elementData == null) {
					elementData = new Coleccion();
				}
				collectionBooks = new ArrayList<Libro>();
				msg = manageException("read", e);
			}
		} else {
			// Creamos un coleccion vacio, para que no de fallos al intentar
			// acceder a algunos campos
			elementData = new Coleccion();
			collectionBooks = new ArrayList<Libro>();
			msg = messageSource.getMessage("collections.menu.read.noIndexMsg",
					null, LocaleContextHolder.getLocale());
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.READ,
				getSection()));
		model.addAttribute("elementData", elementData);
		model.addAttribute("collectionBooks", collectionBooks);
		return "commons/body";
	}

	@Override
	public String acceptCreation(Bean newElement, Model model) {
		Coleccion newCollection = (Coleccion) newElement;
		String msg = "";
		ColeccionDao dao = DaoFactory.getColeccionDao();
		try {
			dao.beginTransaction();
			// Si tenemos una editorial nueva la creamos
			if (newCollection.getEditorial().getId() == -1) {
				// Si tenemos una editorial valida
				if (NewDataManager
						.processPublisher(newPublisher, messageSource)) {
					EditorialDao editorialDao = DaoFactory.getEditorialDao();
					int editorialId = editorialDao.insert(newPublisher);
					newCollection.getEditorial().setId(editorialId);
				} else {
					msg += messageSource.getMessage(
							"error.publisher.fieldsNotRight", null,
							LocaleContextHolder.getLocale())
							+ " " + newPublisher.getNombre() + "\n";
				}
			}
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processCollection(newCollection, messageSource)) {
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager.get(
						messageSource.getMessage(
								"error.collection.fieldsNotRight", null,
								LocaleContextHolder.getLocale())
								+ " " + newCollection.getNombre(),
						ACTION.CREATE, getSection()));
				model.addAttribute("elementData", newCollection);
				dao.rollbackTransaction();
				return "commons/body";
			}
			// Guardamos el coleccion
			dao.insert(newCollection);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			msg = manageException("acceptCreation, al crear la coleccion", e);
		}

		try {
			// Iniciamos paginacion
			pagManager = new PaginationManager(messageSource, dao.getCount());
			// Buscamos los datos
			data = dao.getWithPag(pagManager.getPage() - 1,
					pagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			filter = null;
		} catch (Exception e) {
			msg = manageException("acceptCreation, cargando los datos", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				getSection()));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}

	@Override
	public String acceptUpdate(Bean newElement, Model model) {
		Coleccion newCollection = (Coleccion) newElement;
		newCollection.setId(modifiedElementId);
		String msg = "";
		// Iniciamos la transaccion para guardar el coleccion
		ColeccionDao dao = DaoFactory.getColeccionDao();
		try {
			dao.beginTransaction();
			// Si tenemos una editorial nueva la creamos
			if (newCollection.getEditorial().getId() == -1) {
				// Si tenemos una editorial valida
				if (NewDataManager
						.processPublisher(newPublisher, messageSource)) {
					EditorialDao editorialDao = DaoFactory.getEditorialDao();
					int editorialId = editorialDao.insert(newPublisher);
					newCollection.getEditorial().setId(editorialId);
				} else {
					msg += messageSource.getMessage(
							"error.publisher.fieldsNotRight", null,
							LocaleContextHolder.getLocale())
							+ "\n";
				}
			}
			// Si no son correctos los datos introducidos volvemos a mostrar el
			// formulario de creacion
			if (!NewDataManager.processCollection(newCollection, messageSource)) {
				// Enlazamos fragmentos de plantillas
				model.addAllAttributes(FragmentManager.get(
						messageSource.getMessage(
								"error.collection.fieldsNotRight", null,
								LocaleContextHolder.getLocale())
								+ " " + newCollection.getNombre(),
						ACTION.CREATE, getSection()));
				model.addAttribute("elementData", newCollection);
				dao.rollbackTransaction();
				return "commons/body";
			}
			// Guardamos el libro
			dao.update(newCollection);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			msg = manageException("acceptUpdate, al modificar la coleccion", e);
		}

		try {
			// Iniciamos paginacion
			pagManager = new PaginationManager(messageSource, dao.getCount());
			// Buscamos los datos
			data = dao.getWithPag(pagManager.getPage() - 1,
					pagManager.getPageSize());
			// Reiniciamos el filtro de busqueda
			filter = null;
		} catch (Exception e) {
			msg = manageException("acceptUpdate, cargando los datos", e);
		}
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.get(msg, ACTION.LIST,
				SECTION.COLLECTIONS));
		// Fijamos variables para la vista
		setModelData(model);
		return "commons/body";
	}

	/**
	 * Metodo para gestionar peticiones AJAX para obtener datos de colecciones
	 * 
	 * @param hint
	 * @return
	 */
	public List<Coleccion> getData(String hint, Integer publisherId) {
		ColeccionDao dao = DaoFactory.getColeccionDao();
		try {
			return dao.getColecciones(hint, publisherId);
		} catch (Exception e) {
			log.error("Error los datos mediante peticion AJAX"
					+ " de las colecciones", e);
			return new ArrayList<Coleccion>();
		}
	}

	/**
	 * Metodo que registra la creacion de una editorial, cada vez que se cree
	 * una se remplazara por la ya existente
	 * 
	 * @param requestBody
	 * @return
	 */
	public String newPublisher(String requestBody) {
		try {
			// Tranformamos los datos JSON recibidos en el objeto Editorial
			newPublisher = new ObjectMapper().readValue(requestBody,
					Editorial.class);
		} catch (Exception e) {
			log.error(
					"Error al crear la nueva editorial con los datos JSON de la peticion AJAX",
					e);
			return "FAIL";
		}
		return "OK";
	}
}
