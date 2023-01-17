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
multiindex = [];
lastChecked = -1;
window.onload = initSearchDialog;

// Metodo para fijar un fila como seleccionada
function setSelected(domElement, event) {
	if(multiselect) {
		domElement.children[0].children[0].click();
	} else {
		// Primero buscamos la fila anteriormente seleccionada
		var rows = document.getElementsByClassName("selectedRow");
		for (var i = 0; i < rows.length; i++) {
			rows[i].className = "";
		}
		// Añadimos la clase al elemento
		domElement.className += " selectedRow";
		index = domElement.id.split("_")[1];
	}
}

//Metodo para fijar un fila como seleccionada/deseleccion al hacer click en el check
function setChecked(domElement, event) {
	if(multiselect) {
		var tr = domElement.parentNode.parentNode;
		var i = tr.id.split("_")[1];
		if(domElement.checked) {
			if(lastChecked != -1 && event.shiftKey) {
				var min = Math.min(i,lastChecked), max = Math.max(i,lastChecked);
				for (var j = min+1; j < max; j++) {
					tr.parentNode.children[j].click();
				}
			}
			lastChecked = i;
			multiindex.push(i);
			tr.className += " selectedRow";
		} else {
			var j = multiindex.indexOf(i);
			multiindex.splice(j,1);
			tr.className = "";
		}
	}
	event.stopPropagation();
}

//Metodo para seleccionar/deseleccionar todas las filas de la tabla
function setAllChecked(domElement, event) {
	if(multiselect) {
		var tbody = domElement.parentNode.parentNode.parentNode.parentNode.getElementsByTagName("tbody")[0];
		var trs = tbody.getElementsByTagName("tr");
		for(var i = 0; i < trs.length; i++) {
			var check = trs[i].children[0].children[0];
			if(domElement.checked != check.checked) {
				check.checked = domElement.checked;
				check.onclick(event);
			}
		}
	}
	event.stopPropagation();
}

// submit the specified form with the params
function submit(formID, paramName, paramValue, msg) {
	// Si no tenemos un mensaje que mostrar no comprobamos si el indice es
	// valido
	if (msg && msg != '') {
		if(multiselect) {
			if (multiindex.length == 0) {
				alert(msg);
				return;
			}
		} else {
			if (index < 0) {
				alert(msg);
				return;
			}
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
			form.action = form.action +'_'+ paramName;
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
	form.action = form.action +'_'+ paramName;
	form.appendChild(hidden);
	form.submit();
	showLoading();
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
// Muestra una pantalla de cargando
var loadingScreen;
function showLoading() {
	if (loadingScreen == null) {
		loadingScreen = document.createElement('div');
		loadingScreen.style.width='100%';
		loadingScreen.style.height='100%';
		loadingScreen.style.backgroundColor = '#000000aa';
		loadingScreen.style.position = 'fixed';
		loadingScreen.style.top = '0';
		loadingScreen.style.display = 'flex';
		loadingScreen.style.alignItems = 'center';
		loadingScreen.style.justifyContent = 'center';
		loadingScreen.style.zIndex = '6000';
		var loadingText = document.createElement('div');
		loadingScreen.appendChild(loadingText);
		loadingText.append('Cargando ...');
		loadingText.style.fontSize = '100px';
		loadingText.style.color = 'gold';
		loadingText.style.top = '50%';
		var loadingBall= document.createElement('div');
		loadingScreen.appendChild(loadingBall);
		loadingBall.id = 'loadingBall';
	}
	document.body.appendChild(loadingScreen);
}

function hideLoading() {
	if (loadingScreen != null && loadingScreen.parentElement == document.body) {
		document.body.removeChild(loadingScreen);
	}
}
// Formatea fecha
function formatDate(date) {
	if (date == null || date == "") {
		return date;
	}
	var splitted = date.split("-");
	if (splitted.length != 3) {
		return date;
	}
	return splitted[2]+"/"+splitted[1]+"/"+splitted[0];
}