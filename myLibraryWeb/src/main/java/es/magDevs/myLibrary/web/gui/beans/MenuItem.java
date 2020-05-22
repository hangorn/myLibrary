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
package es.magDevs.myLibrary.web.gui.beans;

import java.util.List;

/**
 * Bean para todos los datos de una entrada del menu (texto, imagen, link, ...)
 * 
 * @author javier.vaquero
 * 
 */
public class MenuItem {
	private String text;
	private String img;
	private String link;
	private List<MenuItem> submenu;
	private Integer index;
	
	public boolean isSelected(Integer index) {
		if (index == null) {
			return false;
		}
		if (this.index != null) {
			return this.index == index;
		}
		if (submenu != null) {
			for (MenuItem item : submenu) {
				if (item.isSelected(index)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public List<MenuItem> getSubmenu() {
		return submenu;
	}

	public void setSubmenu(List<MenuItem> submenu) {
		this.submenu = submenu;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

}
