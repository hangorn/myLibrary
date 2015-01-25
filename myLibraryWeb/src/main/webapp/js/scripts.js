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
	//Si no tenemos un mensaje que mostrar no comprobamos si el indice es valido
	if(msg && msg != '') {
		if(index < 0) {
			alert(msg);
			return;
		}
	}
	//Si se trata de una eliminacion pedimos confirmacion antes de proceder
	if(paramName == "delete") {
		if(confirm(deleteConfirmMessage)) {
			var form = document.getElementById(formID);
			var hidden = document.createElement("input");
			hidden.type = "hidden";
			hidden.name = paramName;
			hidden.value = paramValue;
			form.action=form.action+paramName;
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
	form.action=form.action+paramName;
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


