/*
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
index = -1;
window.onload = initSearchDialog;

// Metodo para fijar un fila como seleccionada
function setSelected(domElement) {
	// Primero buscamos la fila anteriormente seleccionada
	var rows = document.getElementsByClassName("selectedRow");
	for (var i = 0; i < rows.length; i++) {
		rows[i].className = "";
	}
	// Añadimos la clase al elemento
	domElement.className += " selectedRow";
	index = domElement.id.split("_")[1];
}

// submit the specified form with the params
function submit(formID, paramName, paramValue, msg) {
	// Si no tenemos un mensaje que mostrar no comprobamos si el indice es
	// valido
	if (msg && msg != '') {
		if (index < 0) {
			alert(msg);
			return;
		}
	}
	// Si se trata de una eliminacion pedimos confirmacion antes de proceder
	if (paramName == "delete") {
		if (confirm(deleteConfirmMessage)) {
			var form = document.getElementById(formID);
			var hidden = document.createElement("input");
			hidden.type = "hidden";
			hidden.name = paramName;
			hidden.value = paramValue;
			form.action = form.action + paramName;
			form.appendChild(hidden);
			form.submit();
		}
		return;
	}
	var form = document.getElementById(formID);
	var hidden = document.createElement("input");
	hidden.type = "hidden";
	hidden.name = paramName;
	hidden.value = paramValue;
	form.action = form.action + paramName;
	form.appendChild(hidden);
	form.submit();
}

// Vacia todos los campos de busqueda (todos los tags INPUT o SELECT que esten
// dentro de un contenedor de clase "searchField")
function clearSearch() {
	// Primero buscamos todos los contenedores del formulario de busqueda
	var rows = document.getElementsByClassName("searchField");
	for (var i = 0; i < rows.length; i++) {
		// Recorremos todos los elementos de cada contenedor
		for (var j = 0; j < rows[i].children.length; j++) {
			var element = rows[i].children[j];
			// Si es una entrada de texto
			if (element.tagName = "INPUT") {
				element.value = "";
			}
			// Si es una entrada de texto
			if (element.tagName = "SELECT") {
				element.selectedIndex = 0;
			}
		}
	}
}

// Inicia el comportamiento del dialogo de busqueda, si existe
function initSearchDialog() {
	// Si no tenemos dialogo de busqueda, salimos
	if (document.getElementsByClassName('searchContainer').length == 0) {
		return;
	}
	var container = document.getElementsByClassName('searchContainer')[0];
	var button = document.getElementsByClassName('searchIcon')[0];
	var buttonElements = [];
	for(var i=0; i<button.children.length; i++) {
		buttonElements[i] = button.children[i];
	}
	var hideImg = document.createElement('img');
	hideImg.src = 'img/searchmenu/hideSearch.png';
	
	//accion del evento de ocultar
	var showSearch = function() {
		//le añadimos la clase CSS correspondiente
		container.classList.add("searchContainerOpen");
		button.classList.add('closeSearch');
		// Registramos el evento de hacer click en el boton de ocultar
		//y desactivamos el de mostrar
		container.onmouseenter = null;
		button.onclick = buttonClicked;
		// Borramos imagenes del boton
		for(var i=0; i<buttonElements.length; i++) {
			button.removeChild(buttonElements[i]);
		}
		// Añadimos la imagen de ocultar
		button.appendChild(hideImg);
	}
	//accion del evento de ocultar
	var buttonClicked = function() {
		//le quitamos la clase CSS correspondiente
		container.classList.remove("searchContainerOpen");
		button.classList.remove('closeSearch');
		//borramos el evento de ocultar, si esta oculto no se podra ocultar
		//y activamos el de mostrar
		button.onclick = null;
		container.onmouseenter = showSearch;
		// Añadimos imagenes del boton
		for(var i=0; i<buttonElements.length; i++) {
			button.appendChild(buttonElements[i]);
		}
		// Borramos la imagen de ocultar
		button.removeChild(hideImg);
	}
	
	// Registramos el evento de mover el raton sobre el dialogo de busqueda
	container.onmouseenter = showSearch;
}
