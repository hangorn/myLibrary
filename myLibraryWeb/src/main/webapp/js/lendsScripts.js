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

var newUserIdCount = 0;
var selectedUser = null;

var lendBookButton = document.getElementById("lendBookButton");
var returnBookButton = document.getElementById("returnBookButton");
var buttonLendReturn = lendBookButton || returnBookButton;
if (buttonLendReturn != null) {
//Añadimos el evento para prestar un libro
var lendBook = function() {
	document.getElementById("lendBookFormBackground").style.display = "inline";
	var input = document.getElementById('usersSearch');
	input.focus();
	input.select();
	document.getElementById("lendDate").valueAsDate = new Date();
	recargarUsuarios();
	
	var ajaxRequest = new XMLHttpRequest();
	ajaxRequest.open("POST", lendsLink+"_create", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.send("create=-1&"+csrfParameterName+"="+csrfToken);
};

if(lendBookButton != null) {
	buttonLendReturn.onclick = lendBook;
}

//Registramos el evento de salir del proceso de prestamo
var buttonCloseUser = document.getElementById("closeButtonLendBook");
buttonCloseUser.onclick = function() {
	document.getElementById("lendBookFormBackground").style.display = "none";
};
document.getElementById("cancelLendBook").onclick = buttonCloseUser.onclick;

//Metodo para buscar usuarios
var recargarUsuarios = function() {
	//Creamos un peticion AJAX
	var ajaxRequest = new XMLHttpRequest();
	// Creamos la funcion que se lanzara cuando los datos esten listos
	var processSearch = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			// Transformamos los datos obtenidos a notacion JSON
			var usersData = JSON.parse(ajaxRequest.responseText);
			// Obtenemos la tabla donde mostraremos los usuarios,
			var list = document.getElementById("usersDataTable");
			// Vaciamos la lista
			while (list.firstChild) {
				list.removeChild(list.firstChild);
			}
			// Si no tenemos datos no hacemos nada
			if (!usersData || usersData.length == 0) {
				return;
			}
			// Recorremos todos los datos obtenidos
			for (var i = 0; i < usersData.length; i++) {
				var userData = usersData[i];
				addUserToList(userData.nombre, userData.username, list);
			}
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
		}
	};
	// Configuramos y enviamos la peticion
	ajaxRequest.open("POST", usersLink+"_getdata", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = processSearch;
	ajaxRequest.send("getdata=" + usersSearch.value+"&"+csrfParameterName+"="+csrfToken);
}

var addUserToList = function(nombre, username, list, asFirst) {
	// Creamos la estructura para la tabla
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	td.innerHTML = nombre;
	td.title = nombre;
	// Guardamos en el ID de la fila tanto el nombre del usuario
	// como el login, para luego poder obtenerlos si se
	// elige el usuario
	td.id = nombre + "#$#$#" + username;
	tr.appendChild(td);
	if (asFirst && list.childElementCount != 0) {
		list.insertBefore(tr, list.childNodes[0]);
	} else {
		list.appendChild(tr);
	}
	// Registramos el evento para cuando se haga click en un usuario
	td.onclick = function(event) {
		seleccionar(event.target);
	};
	return td;
}

var seleccionar = function(elemento) {
	if (selectedUser != null) {
		selectedUser.classList.remove("selectedRow");
	}
	selectedUser = elemento;
	selectedUser.classList.add("selectedRow");
}

//Registramos las peticiones AJAX para la busqueda de usuarios
var inputUserSearch = document.getElementById("usersSearch");
inputUserSearch.oninput = recargarUsuarios;

//Añadimos el evento de abrir el dialogo para crear un nuevo usuario
document.getElementById("addNewUser").onclick = function() {
	// Reseteamos el aspecto de los campos obligatorios
	document.getElementById("newUserName").className = "";
	document.getElementById("newUserFormBackground").style.display = "inline";
	document.getElementById("newUserFormBackground").getElementsByTagName('input')[0].focus();
};

//Registramos el evento para cuando se cancele la creacion de un nuevo usuario
document.getElementById("cancelNewUser").onclick = function() {
	document.getElementById("newUserFormBackground").style.display = "none";
};

//Registramos el evento para cuando se cree un nuevo usuario
document.getElementById("acceptNewUser").onclick = function() {
	var nombre = document.getElementById("newUserName").value;
	// Si no tenemos un nombre unicamente mostramos un alert
	if (!nombre || nombre == "") {
		document.getElementById("newUserName").className += "fieldError";
		alert(messageUserEmpty);
		return;
	}
	var id = --newUserIdCount;
	var json = '{"username" : "' + id + '","nombre" : "' + nombre + '"}';
	var ajaxRequest = new XMLHttpRequest();
	var addUser = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200 && ajaxRequest.responseText == "OK") {
			var nombre = document.getElementById("newUserName").value;
			// Creamos un nuevo elemento en la lista de usuarios y lo seleccionamos
			var newTd = addUserToList(nombre, ""+newUserIdCount, document.getElementById("usersDataTable"), true);
			seleccionar(newTd);
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
		}
	};
	// Enviamos una peticion AJAX al servidor para indicar que se ha creado un nuevo usuario
	ajaxRequest.open("POST", lendsLink + "_related_new_users", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = addUser;
	ajaxRequest.send("data="+json+"&"+csrfParameterName+"="+csrfToken);
	document.getElementById("newUserFormBackground").style.display = "none";
};
document.getElementById("newUserFormBackground").onkeydown = function() {
	// Si se ha presionado enter
	if (event.keyCode === 13) {
		document.getElementById("acceptNewUser").onclick();
		event.preventDefault();
		return false;
	}
};

//Registramos el evento para cuando se confirme el prestamo de un libro
document.getElementById("acceptLendBook").onclick = function() {
	// Si no hay un usuario seleccionado, mostrarmos error y salimos
	if (selectedUser == null) {
		alert(messageUserNoSelected);
		return;
	}
	var usernameSelected = selectedUser.id.split("#$#$#")[1];
	var nombreSelected = selectedUser.id.split("#$#$#")[0];
	
	var ajaxRequest = new XMLHttpRequest();
	ajaxRequest.open("POST", lendsLink + "_acceptCreation", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			document.getElementById("lendBookFormBackground").style.display = "none";
			
			buttonLendReturn.onclick = returnBook;
			buttonLendReturn.title = messageReturn.replace("{0}",nombreSelected);
			var img = buttonLendReturn.getElementsByTagName("img")[0];
			img.src = img.src.replace("lend","return");
			img.title = messageReturn.replace("{0}",nombreSelected);
			img.alt = messageReturn.replace("{0}",nombreSelected);
			var label = buttonLendReturn.getElementsByTagName("label")[0];
			label.innerText = messageReturn.replace("{0}",nombreSelected);
			hideLoading();
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
			hideLoading();
		}
	};
	ajaxRequest.send("libro.id=" + document.getElementById("bookId").value + "&usuario.username=" + usernameSelected
			+ "&fecha="+ document.getElementById("lendDate").value +"&acceptCreation&"+csrfParameterName+"="+csrfToken);
	showLoading();
};
document.getElementById("lendBookFormContainer").onkeydown = function() {
	// Si se ha presionado enter
	if (event.keyCode === 13) {
		document.getElementById("acceptLendBook").onclick();
		event.preventDefault();
		return false;
	}
};

//Añadimos el evento para devolver un libro
var returnBook = function() {
	var ajaxRequest = new XMLHttpRequest();
	ajaxRequest.open("POST", lendsLink+"_delete", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			buttonLendReturn.onclick = lendBook;
			buttonLendReturn.title = messageLend;
			var img = buttonLendReturn.getElementsByTagName("img")[0];
			img.src = img.src.replace("return","lend");
			img.title = messageLend;
			img.alt = messageLend;
			var label = buttonLendReturn.getElementsByTagName("label")[0];
			label.innerText = messageLend;
			hideLoading();
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
			hideLoading();
		}
	};
	ajaxRequest.send("delete="+ document.getElementById("bookId").value +"&"+csrfParameterName+"="+csrfToken);
	showLoading();
};

if (returnBookButton != null) {
	buttonLendReturn.onclick = returnBook;
}
}