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
package es.magDevs.myLibrary.web.conf;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.thymeleaf.spring4.view.ThymeleafView;

import es.magDevs.myLibrary.web.gui.utils.MailManager;

public class CustomThymeleafView extends ThymeleafView {
	
	private static final Logger log = Logger.getLogger(CustomThymeleafView.class);

	public CustomThymeleafView() {
	}

	public CustomThymeleafView(String templateName) {
		super(templateName);
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			super.render(model, request, response);
		} catch (Exception e) {
			log.error("Error al renderizar la vista '" + getTemplateName()+"'", e);
			MailManager.enviarError(e, request.getHeader("user-agent"));
			throw e;
		}
	}
}
