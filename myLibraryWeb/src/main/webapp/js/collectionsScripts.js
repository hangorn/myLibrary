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
//Registramos las peticiones AJAX para la busqueda de colecciones
var inputCollectionSearch = document.getElementById("collectionSearch");
inputCollectionSearch.oninput = function() {
	// Creamos un peticion AJAX
	var ajaxRequest = new XMLHttpRequest();
	// Creamos la funcion que se lanzara cuando los datos esten listos
	var processSearch = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			// Transformamos los datos obtenidos a notacion JSON
			var collectionsData = JSON.parse(ajaxRequest.responseText);
			// Si no tenemos datos no hacemos nada
			if (!collectionsData) {
				return;
			}
			// Obtenemos tanto la lista donde mostraremos las colecciones,
			// como la coleccion por defecto, si existe
			var list = document.getElementById("coleccion.id");
			var defaultItem = document.getElementById("defaultCollection");
			var newItem = document.getElementById("newCollection");
			var emptyItem = document.getElementById("emptyCollection");
			// Vaciamos la lista
			while (list.firstChild) {
				list.removeChild(list.firstChild);
			}
			list.appendChild(emptyItem);
			// Si tenemos un elemento por nuevo lo añadimos
			if (newItem) {
				list.appendChild(newItem);
			}
			// Si tenemos un elemento por defecto lo añadimos
			if (defaultItem) {
				list.appendChild(defaultItem);
			}
			// Recorremos todos los datos obtenidos
			for (var i = 0; i < collectionsData.length; i++) {
				var collectionData = collectionsData[i];
				var listItem = document.createElement("option");
				listItem.innerHTML = collectionData.nombre + " ("
						+ collectionData.editorial.nombre + ")";
				listItem.value = collectionData.id;
				listItem.title = collectionData.nombre + " ("
						+ collectionData.editorial.nombre + ")";
				list.appendChild(listItem);
			}
			list.multiple = true;
		}
	};
	// Configuramos y enviamos la peticion
	ajaxRequest.open("POST", collectionsLink+"_getdata", true);
	ajaxRequest.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = processSearch;
	var publishersList = document.getElementById("editorial.id");
	var publisherId = publishersList.options[publishersList.selectedIndex].value;
	ajaxRequest.send("getdata=" + inputCollectionSearch.value + "&id="
			+ publisherId+"&"+csrfParameterName+"="+csrfToken);
};

// Añadimos la opcion de abrir el dialogo para crear una nueva coleccion
document.getElementById("addNewCollection").onclick = function() {
	document.getElementById("newCollectionName").className = "";
	document.getElementById("newCollectionFormBackground").style.display = "inline";
	document.getElementById("newCollectionFormBackground").getElementsByTagName('input')[0].focus();
};

// Registramos el evento para cuando se cree una nueva coleccion
document.getElementById("acceptNewCollection").onclick = function() {
	var nombre = document.getElementById("newCollectionName").value;

	// Si no tenemos un nombre unicamente mostramos un alert
	if (!nombre || nombre == "") {
		document.getElementById("newCollectionName").className += "fieldError";
		alert(messageCollectionEmpty);
		return;
	}
	var id = -1;
	var json = '{"id" : "' + id + '","nombre" : "' + nombre + '"}';
	var ajaxRequest = new XMLHttpRequest();
	var addCollection = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200
				&& ajaxRequest.responseText == "OK") {
			var nombre = document.getElementById("newCollectionName").value;
			// Creamos un nuevo elemento o modificamos el existente de la lista
			// de colecciones
			if (!document.getElementById("newCollection")) {
				var listItem = document.createElement("option");
				listItem.innerHTML = nombre;
				listItem.value = -1;
				listItem.title = nombre;
				listItem.selected = true;
				listItem.id = "newCollection";
				var list = document.getElementById("coleccion.id");
				list.insertBefore(listItem, list.firstChild);
			} else {
				var listItem = document.getElementById("newCollection");
				listItem.innerHTML = nombre;
				listItem.title = nombre;
				listItem.selected = true;
			}
		}
	};
	// Enviamos una peticion AJAX al servidor para indicar que se ha creado una
	// nueva coleccion
	ajaxRequest.open("POST", booksLink + "_related_new_collections", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = addCollection;
	ajaxRequest.send("data="+json+"&"+csrfParameterName+"="+csrfToken);
	document.getElementById("newCollectionFormBackground").style.display = "none";
};
document.getElementById("newCollectionFormBackground").onkeydown =  function(event) {
	// Si se pulsa enter
	if (event.keyCode === 13) {
		document.getElementById("acceptNewCollection").onclick();
		event.preventDefault();
		return false;
	}
};

// Registramos el evento para cuando se cancele la creacion de una nueva
// coleccion
document.getElementById("cancelNewCollection").onclick = function() {
	document.getElementById("newCollectionFormBackground").style.display = "none";
};

document.getElementById("coleccion.id").onclick = function() {
	document.getElementById("coleccion.id").multiple = false;
};