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
var newAuthorIdCount = 0;
var authorsList = document.getElementById("authorsList");
// Registramos el evento de quitar un autor
var buttonQuitAuthor = document.getElementById("quitAuthor");
buttonQuitAuthor.onclick = function() {
	var authorsIdToRemove = "";
	var selectedAuthors = authorsList.selectedOptions;
	// Recorremos todos los autores seleccionados
	for (var i = 0; i < selectedAuthors.length; i++) {
		authorsIdToRemove += selectedAuthors[i].value + ",";
		authorsList.removeChild(selectedAuthors[i]);
		i--;
	}
	// Si tenemos autores que quitar de la lista
	if (authorsIdToRemove && authorsIdToRemove != "") {
		// Creamos un peticion AJAX
		var ajaxRequest = new XMLHttpRequest();
		// Configuramos y enviamos la peticion
		ajaxRequest.open("POST", booksLink+"_related_delete_authors", true);
		ajaxRequest.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		ajaxRequest.send("data=" + authorsIdToRemove+"&"+csrfParameterName+"="+csrfToken);
	}
};

// Registramos el evento de asignar un autor
var buttonAddAuthor = document.getElementById("addAuthor");
buttonAddAuthor.onclick = function() {
	document.getElementById("addAuthorFormBackground").style.display = "inline";
	var input = document.getElementById('authorSearch');
	input.focus();
	input.select();
};

// Registramos el evento de cancelar la asignacion de un autor
var buttonAddAuthor = document.getElementById("closeButtonAuthors");
buttonAddAuthor.onclick = function() {
	document.getElementById("addAuthorFormBackground").style.display = "none";
};

// Registramos las peticiones AJAX para la busqueda de autores
var inputAuthorSearch = document.getElementById("authorSearch");
inputAuthorSearch.oninput = function() {
	// Creamos un peticion AJAX
	var ajaxRequest = new XMLHttpRequest();
	// Creamos la funcion que se lanzara cuando los datos esten listos
	var processSearch = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			// Transformamos los datos obtenidos a notacion JSON
			var authorsData = JSON.parse(ajaxRequest.responseText);
			// Obtenemos la tabla donde mostraremos los autores,
			var list = document.getElementById("authorsDataTable");
			// Vaciamos la lista
			while (list.firstChild) {
				list.removeChild(list.firstChild);
			}
			// Si no tenemos datos no hacemos nada
			if (!authorsData || authorsData.length == 0) {
				list.innerHTML = "<tr><td>"
						+ messageNoAuthors + "</td></tr>";
				return;
			}
			// Recorremos todos los datos obtenidos
			for (var i = 0; i < authorsData.length; i++) {
				var tdID = "";
				var authorData = authorsData[i];
				// Obtenemos el texto a mostrar de cada autor
				var authorTxt = (!authorData.nombre || authorData.nombre == "") ? ""
						: authorData.nombre + " ";
				tdID = (authorTxt += (!authorData.apellidos || authorData.apellidos == "") ? ""
						: authorData.apellidos + " ");
				authorTxt += (!authorData.pais || authorData.pais == "") ? ""
						: "- " + authorData.pais + " ";
				authorTxt += (!authorData.annoNacimiento || authorData.annoNacimiento == 0) ? ""
						: "- " + authorData.annoNacimiento + " ";
				// Creamos la estructura para la tabla
				var tr = document.createElement("tr");
				var td = document.createElement("td");
				td.innerHTML = authorTxt;
				td.title = authorTxt;
				// Guardamos en el ID de la fila tanto el nombre y apellidos del
				// autor como el ID del autor, para luego poder obtenerlos si se
				// elige el autor
				td.id = tdID + "#$#$#" + authorData.id;
				tr.appendChild(td);
				list.appendChild(tr);
				// Registramos el evento para cuando se haga click en un autor
				td.onclick = function(event) {
					// Obtenemos los datos a partir del id (nombre y apellidos y
					// id)
					var data = event.target.id.split("#$#$#");
					// Ocultamos el dialogo
					document.getElementById("addAuthorFormBackground").style.display = "none";
					// Creamos la opcion para la lista de autores
					var option = document.createElement("option");
					option.innerHTML = option.title = data[0];
					option.value = data[1];
					authorsList.appendChild(option);
					// Creamos una peticion AJAX para indicar que se ha asignado
					// un autor
					var ajaxRequest = new XMLHttpRequest();
					// Configuramos y enviamos la peticion
					ajaxRequest.open("POST", booksLink+"_related_add_authors", true);
					ajaxRequest.setRequestHeader("Content-type",
							"application/x-www-form-urlencoded");
					ajaxRequest.send("data=" + data[1]+"&"+csrfParameterName+"="+csrfToken);
				};
			}
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
		}
	};
	// Configuramos y enviamos la peticion
	ajaxRequest.open("POST", authorsLink+"_getdata", true);
	ajaxRequest.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = processSearch;
	ajaxRequest.send("getdata=" + inputAuthorSearch.value+"&"+csrfParameterName+"="+csrfToken);
};

// Añadimos el evento de abrir el dialogo para crear un nuevo autor
document.getElementById("addNewAuthor").onclick = function() {
	// Reseteamos el aspecto de los campos obligatorios
	document.getElementById("newAuthorSurname").className = "";
	// Ocultamos el menu de busqueda y mostramos el de creacion
	document.getElementById("addAuthorFormBackground").style.display = "none";
	document.getElementById("newAuthorFormBackground").style.display = "inline";
	document.getElementById("newAuthorFormBackground").getElementsByTagName('input')[0].focus();
};

// Registramos el evento para cuando se cancele la creacion de un nuevo autor
document.getElementById("cancelNewAuthor").onclick = function() {
	document.getElementById("newAuthorFormBackground").style.display = "none";
};

// Registramos el evento para cuando se cree un nuevo autor
document.getElementById("acceptNewAuthor").onclick = function() {
	var apellidos = document.getElementById("newAuthorSurname").value;
	// Si no tenemos un nombre unicamente mostramos un alert
	if (!apellidos || apellidos == "") {
		document.getElementById("newAuthorSurname").className += "fieldError";
		alert(messageAuthorEmpty);
		return;
	}
	var nombre = document.getElementById("newAuthorName").value;
	var pais = document.getElementById("newAuthorCountry").value;
	var ciudad = document.getElementById("newAuthorCity").value;
	var id = --newAuthorIdCount;
	var birthyear = document.getElementById("newAuthorBirthyear").value;
	var deathyear = document.getElementById("newAuthorDeathyear").value;
	var notas = document.getElementById("newAuthorNotes").value;
	var json = '{"id" : "' + id + '","ciudad" : "' + ciudad + '","nombre" : "'
			+ nombre + '","apellidos" : "' + apellidos + '","pais" : "' + pais
			+ '","annoNacimiento" : "' + birthyear
			+ '","annoFallecimiento" : "' + deathyear + '","notas" : "' + notas
			+ '"}';
	var ajaxRequest = new XMLHttpRequest();
	var addAuthor = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200
				&& ajaxRequest.responseText == "OK") {
			var nombre = document.getElementById("newAuthorName").value;
			var apellidos = document.getElementById("newAuthorSurname").value;
			// Creamos un nuevo elemento en la lista de autores
			var listItem = document.createElement("option");
			var txt = (!nombre || nombre == "" ? "" : nombre + " ") + apellidos;
			listItem.title = listItem.innerHTML = txt;
			listItem.value = newAuthorIdCount;
			authorsList.appendChild(listItem);
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
		}
	};
	// Enviamos una peticion AJAX al servidor para indicar que se ha creado un
	// nuevo autor
	ajaxRequest.open("POST", booksLink+"_related_new_authors", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = addAuthor;
	ajaxRequest.send("data="+json+"&"+csrfParameterName+"="+csrfToken);
	document.getElementById("newAuthorFormBackground").style.display = "none";
};
document.getElementById("newAuthorFormBackground").onkeydown =  function(event) {
	// Si se pulsa enter
	if (event.keyCode === 13) {
		document.getElementById("acceptNewAuthor").onclick();
		event.preventDefault();
		return false;
	}
};