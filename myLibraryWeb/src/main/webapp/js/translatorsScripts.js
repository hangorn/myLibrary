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
var newTranslatorIdCount = 0;
var translatorsList = document.getElementById("translatorsList");
// Registramos el evento de quitar un traductor
var buttonQuitTranslator = document.getElementById("quitTranslator");
buttonQuitTranslator.onclick = function() {
	var translatorsIdToRemove = "";
	var selectedTranslators = translatorsList.selectedOptions;
	// Recorremos todos los traductores seleccionados
	for (var i = 0; i < selectedTranslators.length; i++) {
		translatorsIdToRemove += selectedTranslators[i].value + ",";
		translatorsList.removeChild(selectedTranslators[i]);
		i--;
	}
	// Si tenemos traductores que quitar de la lista
	if (translatorsIdToRemove && translatorsIdToRemove != "") {
		// Creamos un peticion AJAX
		var ajaxRequest = new XMLHttpRequest();
		// Configuramos y enviamos la peticion
		ajaxRequest.open("POST", booksLink+"quitTranslator", true);
		ajaxRequest.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		ajaxRequest.send("quitTranslator=" + translatorsIdToRemove+"&"+csrfParameterName+"="+csrfToken);
	}
};

// Registramos el evento de asignar un traductor
var buttonAddTranslator = document.getElementById("addTranslator");
buttonAddTranslator.onclick = function() {
	document.getElementById("addTranslatorFormBackground").style.display = "inline";
};

// Registramos el evento de cancelar la asignacion de un traductor
var buttonAddTranslator = document.getElementById("closeButtonTranslators");
buttonAddTranslator.onclick = function() {
	document.getElementById("addTranslatorFormBackground").style.display = "none";
};

// Registramos las peticiones AJAX para la busqueda de traductores
var inputTranslatorSearch = document.getElementById("translatorSearch");
inputTranslatorSearch.oninput = function() {
	// Creamos un peticion AJAX
	var ajaxRequest = new XMLHttpRequest();
	// Creamos la funcion que se lanzara cuando los datos esten listos
	var processSearch = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			// Transformamos los datos obtenidos a notacion JSON
			var translatorsData = JSON.parse(ajaxRequest.responseText);
			// Obtenemos la tabla donde mostraremos los traductores,
			var list = document.getElementById("translatorsDataTable");
			// Vaciamos la lista
			while (list.firstChild) {
				list.removeChild(list.firstChild);
			}
			// Si no tenemos datos no hacemos nada
			if (!translatorsData || translatorsData.length == 0) {
				list.innerHTML = "<tr class='translatorDataRow'><td>"
						+ messageNoTranslators + "</td></tr>";
				return;
			}
			// Recorremos todos los datos obtenidos
			for (var i = 0; i < translatorsData.length; i++) {
				var tdID = "";
				var translatorData = translatorsData[i];
				// Obtenemos el texto a mostrar de cada traductor
				var translatorTxt = translatorData.nombre;
				tdID = translatorTxt;
				// Creamos la estructura para la tabla
				var tr = document.createElement("tr");
				tr.className = "translatorDataRow";
				var td = document.createElement("td");
				td.innerHTML = translatorTxt;
				td.title = translatorTxt;
				// Guardamos en el ID de la fila tanto el nombre del traductor
				// como el ID del traductor, para luego poder obtenerlos si se
				// elige el traductor
				td.id = tdID + "#$#$#" + translatorData.id;
				tr.appendChild(td);
				list.appendChild(tr);
				// Registramos el evento para cuando se haga click en un
				// traductor
				td.onclick = function(event) {
					// Obtenemos los datos a partir del id (nombre y id)
					var data = event.target.id.split("#$#$#");
					// Ocultamos el dialogo
					document.getElementById("addTranslatorFormBackground").style.display = "none";
					// Creamos la opcion para la lista de traductores
					var option = document.createElement("option");
					option.innerHTML = option.title = data[0];
					option.value = data[1];
					translatorsList.appendChild(option);
					// Creamos una peticion AJAX para indicar que se ha asignado
					// un traductor
					var ajaxRequest = new XMLHttpRequest();
					// Configuramos y enviamos la peticion
					ajaxRequest.open("POST", booksLink+"addTranslator", true);
					ajaxRequest.setRequestHeader("Content-type",
							"application/x-www-form-urlencoded");
					ajaxRequest.send("addTranslator=" + data[1]+"&"+csrfParameterName+"="+csrfToken);
				};
			}
		}
	};
	// Configuramos y enviamos la peticion
	ajaxRequest.open("POST", translatorsLink+"getdata", true);
	ajaxRequest.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = processSearch;
	ajaxRequest.send("getdata=" + inputTranslatorSearch.value+"&"+csrfParameterName+"="+csrfToken);
};

// Añadimos el evento de abrir el dialogo para crear un nuevo traductor
document.getElementById("addNewTranslator").onclick = function() {
	// Reseteamos el aspecto de los campos obligatorios
	document.getElementById("newTranslatorName").className = "";
	// Ocultamos el menu de busqueda y mostramos el de creacion
	document.getElementById("addTranslatorFormBackground").style.display = "none";
	document.getElementById("newTranslatorFormBackground").style.display = "inline";
};

// Registramos el evento para cuando se cancele la creacion de un nuevo
// traductor
document.getElementById("cancelNewTranslator").onclick = function() {
	document.getElementById("newTranslatorFormBackground").style.display = "none";
};

// Registramos el evento para cuando se cree un nuevo traductor
document.getElementById("acceptNewTranslator").onclick = function() {
	var nombre = document.getElementById("newTranslatorName").value;
	// Si no tenemos un nombre unicamente mostramos un alert
	if (!nombre || nombre == "") {
		document.getElementById("newTranslatorName").className += "fieldError";
		alert(messageTranslatorEmpty);
		return;
	}
	var id = --newTranslatorIdCount;
	var json = '{"id" : "' + id + '","nombre" : "' + nombre + '"}';
	var ajaxRequest = new XMLHttpRequest();
	var addTranslator = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200
				&& ajaxRequest.responseText == "OK") {
			var nombre = document.getElementById("newTranslatorName").value;
			// Creamos un nuevo elemento en la lista de traductores
			var listItem = document.createElement("option");
			listItem.title = listItem.innerHTML = nombre;
			listItem.value = newTranslatorIdCount;
			translatorsList.appendChild(listItem);
		}
	};
	// Enviamos una peticion AJAX al servidor para indicar que se ha creado un
	// nuevo traductor
	ajaxRequest.open("POST", booksLink + "newTranslator", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = addTranslator;
	ajaxRequest.send("json="+json+"&"+csrfParameterName+"="+csrfToken);
	document.getElementById("newTranslatorFormBackground").style.display = "none";
};