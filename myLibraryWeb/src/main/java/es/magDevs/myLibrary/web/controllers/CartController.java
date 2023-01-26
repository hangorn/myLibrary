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
package es.magDevs.myLibrary.web.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

import es.magDevs.myLibrary.model.Constants.RELATED_ACTION;
import es.magDevs.myLibrary.model.Constants.SECTION;
import es.magDevs.myLibrary.model.DaoFactory;
import es.magDevs.myLibrary.model.beans.Bean;
import es.magDevs.myLibrary.model.beans.Libro;
import es.magDevs.myLibrary.model.dao.AbstractDao;
import es.magDevs.myLibrary.web.gui.utils.FragmentManager;

public class CartController extends AbstractController {

	public CartController(MessageSource messageSource) {
		super(messageSource);
	}

	// LOG
	private static final Logger log = Logger.getLogger(CartController.class);

	/* *****************************************
	 * ********** IMPLEMENTACIONES *************
	 * *****************************************
	 */

	/**
	 * {@inheritDoc}
	 */
	protected SECTION getSection() {
		return SECTION.BOOKS;
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
		return DaoFactory.getLibroDao();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean getNewFilter() {
		return new Libro();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Bean processFilter(Bean filter) {
		return filter;
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean processNewData(Bean newData) {
		return false;
	}

	public Class<Libro> getBeanClass() {
		return Libro.class;
	}

	// Libros en el carrito
	private List<Libro> cartBooks = new ArrayList<>();
	private Set<Integer> cartBooksIds = new HashSet<>();

	/* *****************************************
	 * ************** ACCIONES *****************
	 * *****************************************
	 */
	
	@Override
	public String list(Model model) {
		try {
			for (ListIterator<Libro> iterator = cartBooks.listIterator(); iterator.hasNext();) {
				iterator.set((Libro) getDao().get(iterator.next().getId()));
			}
		} catch (Exception e) {
			model.addAttribute("scriptMessage", manageException("create", e));
		}
		
		model.addAttribute("data", cartBooks);
		// Enlazamos fragmentos de plantillas
		model.addAllAttributes(FragmentManager.getEmptyBody(""));
		model.addAttribute("mainTemplate", "cart/cartList");
		model.addAttribute("mainFragment", "cartList");
		model.addAttribute("filter", new Libro());
		return "commons/body";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String filter(Bean f, Model model) {
		if (f != null && f instanceof Libro) {
			String bc = ((Libro) f).getCb();
			if (StringUtils.isNotBlank(bc)) {
				try {
					List<Libro> bcBooks = DaoFactory.getLibroDao().getWithPag(f, 0, 2);
					if (bcBooks.size() == 1) {
						addBook2Cart(bcBooks.get(0).getId());
					}
				} catch (Exception e) {
					model.addAttribute("scriptMessage", manageException("create", e));
				}
			}
		}
		// Actualizamos el numero de libros que hay en el carrito
		model.addAttribute("cartBooks", cartBooks.stream().map(book->book.getId()).collect(Collectors.toSet()));
		return list(model);
	}
	
	@Override
	public String delete(Integer bookId, Model model) {
		removeBookFromCart(bookId);
		// Para que se recargue el numero de libros en el carrito
		model.addAttribute("cartBooks", cartBooks.stream().map(book->book.getId()).collect(Collectors.toSet()));
		return list(model);
	}
	
	@Override
	public String multiupdate(Model model) {
		// Reutilizamos el evento de modificacion multiple para vaciar el carrito
		cartBooks.clear();
		cartBooksIds.clear();
		// Para que se recargue el numero de libros en el carrito
		model.addAttribute("cartBooks", cartBooks.stream().map(book->book.getId()).collect(Collectors.toSet()));
		return list(model);
	}
	
	@Override
	public List<Libro> getData(String hint) {
		return cartBooks;
	}

	@Override
	public String manageRelatedData(RELATED_ACTION action, String dataType, String data) {
		try {
			if(SECTION.BOOKS.get().equals(dataType)) {
				if (RELATED_ACTION.ADD.equals(action)) {
					addBook2Cart(Integer.valueOf(data));
				} else if (RELATED_ACTION.DELETE.equals(action)) {
					removeBookFromCart(Integer.valueOf(data));
				}
				return cartBooks.size() + "";
			}
			return super.manageRelatedData(action, dataType, data);
		} catch (Exception e) {
			manageException("related "+action+"/"+dataType+"="+data, e);
			return "FAIL";
		}
	}

	protected void addBook2Cart(Integer bookId) throws Exception {
		if (!cartBooksIds.contains(bookId)) {
			cartBooks.add((Libro) getDao().get(bookId));
			cartBooksIds.add(bookId);
		}
	}

	protected void removeBookFromCart(Integer bookId) {
		for (Iterator<Libro> iterator = cartBooks.iterator(); iterator.hasNext();) {
			if (iterator.next().getId().equals(bookId)) {
				cartBooksIds.remove(bookId);
				iterator.remove();
			}
		}
	}
}
