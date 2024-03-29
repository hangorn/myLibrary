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
//Registramos las peticiones AJAX para la busqueda de editoriales
var inputPublisherSearch = document.getElementById("publisherSearch");
inputPublisherSearch.oninput = function() {
	// Creamos un peticion AJAX
	var ajaxRequest = new XMLHttpRequest();
	// Creamos la funcion que se lanzara cuando los datos esten listos
	var processSearch = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			// Transformamos los datos obtenidos a notacion JSON
			var publishersData = JSON.parse(ajaxRequest.responseText);
			// Si no tenemos datos no hacemos nada
			if (!publishersData) {
				return;
			}
			// Obtenemos tanto la lista donde mostraremos las editoriales,
			// como la editorial por defecto, si existe
			var list = document.getElementById("editorial.id");
			var defaultItem = document.getElementById("defaultPublisher");
			var newItem = document.getElementById("newPublisher");
			var emptyItem = document.getElementById("emptyPublisher");
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
			for (var i = 0; i < publishersData.length; i++) {
				var publisherData = publishersData[i];
				var listItem = document.createElement("option");
				listItem.innerHTML = publisherData.nombre;
				listItem.value = publisherData.id;
				listItem.title = publisherData.nombre;
				list.appendChild(listItem);
			}
			list.multiple = true;
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
		}
	};
	// Configuramos y enviamos la peticion
	ajaxRequest.open("POST", publishersLink+"_getdata", true);
	ajaxRequest.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = processSearch;
	ajaxRequest.send("getdata=" + encodeURIComponent(inputPublisherSearch.value)+"&"+csrfParameterName+"="+csrfToken);
};

// Añadimos la opcion de abrir el dialogo para crear una nueva editorial
document.getElementById("addNewPublisher").onclick = function() {
	document.getElementById("newPublisherName").className = "";
	document.getElementById("newPublisherFormBackground").style.display = "inline";
	document.getElementById("newPublisherFormBackground").getElementsByTagName('input')[0].focus();
};

// Registramos el evento para cuando se cree una nueva editorial
document.getElementById("acceptNewPublisher").onclick = function() {
	var nombre = document.getElementById("newPublisherName").value;

	// Si no tenemos un nombre unicamente mostramos un alert
	if (!nombre || nombre == "") {
		document.getElementById("newPublisherName").className += "fieldError";
		alert(messagePublisherEmpty);
		return;
	}
	var ciudad = document.getElementById("newPublisherCity").value;
	var id = -1;
	var json = '{"id" : "' + id + '","ciudad" : "' + ciudad + '","nombre" : "'
			+ nombre + '"}';
	var ajaxRequest = new XMLHttpRequest();
	var addPublisher = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200
				&& ajaxRequest.responseText == "OK") {
			var nombre = document.getElementById("newPublisherName").value;
			// Creamos un nuevo elemento o modificamos el existente de la lista
			// de editoriales
			if (!document.getElementById("newPublisher")) {
				var listItem = document.createElement("option");
				listItem.innerHTML = nombre;
				listItem.value = -1;
				listItem.title = nombre;
				listItem.selected = true;
				listItem.id = "newPublisher";
				var list = document.getElementById("editorial.id");
				list.insertBefore(listItem, list.firstChild);
			} else {
				var listItem = document.getElementById("newPublisher");
				listItem.innerHTML = nombre;
				listItem.title = nombre;
				listItem.selected = true;
			}
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
		}
	};
	// Enviamos una peticion AJAX al servidor para indicar que se ha creado una
	// nueva editorial
	ajaxRequest.open("POST", currentSectionLink + "_related_new_publishers", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = addPublisher;
	ajaxRequest.send("data="+encodeURIComponent(json)+"&"+csrfParameterName+"="+csrfToken);
	document.getElementById("newPublisherFormBackground").style.display = "none";
};
document.getElementById("newPublisherFormBackground").onkeydown =  function(event) {
	// Si se pulsa enter
	if (event.keyCode === 13) {
		document.getElementById("acceptNewPublisher").onclick();
		event.preventDefault();
		return false;
	}
};

// Registramos el evento para cuando se cancele la creacion de una nueva
// editorial
document.getElementById("cancelNewPublisher").onclick = function() {
	document.getElementById("newPublisherFormBackground").style.display = "none";
};

// Registramos el evento de cambio de editorial, para que cuando se seleccione
// una editorial se recarguen las colecciones
document.getElementById("editorial.id").onchange = function() {
	document.getElementById("collectionSearch").oninput();
};

document.getElementById("editorial.id").onclick = function() {
	document.getElementById("editorial.id").multiple = false;
};
