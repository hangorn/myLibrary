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
package es.magDevs.myLibrary.model.dao.hib;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * DAO basico que gestiona operaciones de la sesion
 * 
 * @author javier.vaquero
 *
 */
public abstract class HibBasicDao {
	protected SessionFactory sessionFactory;

	protected Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}

	public void beginTransaction() {
		this.getSession().beginTransaction();
	}

	public void commitTransaction() {
		this.getSession().getTransaction().commit();
	}

	public void rollbackTransaction() {
		this.getSession().getTransaction().rollback();
	}
}