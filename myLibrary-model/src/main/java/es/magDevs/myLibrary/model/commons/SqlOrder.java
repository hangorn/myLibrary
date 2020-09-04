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
package es.magDevs.myLibrary.model.commons;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

public class SqlOrder extends Order {

	public SqlOrder(String propertyName, boolean ascending) {
		super(propertyName, ascending);
	}
	
	@Override
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
		return getPropertyName() + " " + (isAscending() ? "asc" : "desc");
	}

}
