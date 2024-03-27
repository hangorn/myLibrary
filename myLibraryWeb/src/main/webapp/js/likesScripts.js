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

var likeBookButton = document.getElementById("likeBookButton");
var dislikeBookButton = document.getElementById("dislikeBookButton");
var likesTxt = document.getElementById("likesTxt");
var dislikesTxt = document.getElementById("dislikesTxt");
if (likeBookButton != null) {
//Añadimos el evento para marcar como me gusta un libro
var likeBook = function() {
	showLoading();
	var ajaxRequest = new XMLHttpRequest();
	var processLike = function() {
		if (ajaxRequest.readyState === 4) {
			// Comprobamos los datos obtenidos
			var response = ajaxRequest.responseText;
			if (ajaxRequest.status === 200 && response && response.startsWith('OK') && response.length == 3) {
				// Marcamos los botones segun el dato recibido +=creado -=borrado *=cambiado(like por dislike o vicebersa)
				if (response.charAt(2) == '+') {
					likeBookButton.classList.add('selected');
					likesTxt.innerHTML = (Number.parseInt(likesTxt.innerHTML)+1);
				} else if (response.charAt(2) == '-') {
					likeBookButton.classList.remove('selected');
					likesTxt.innerHTML = (Number.parseInt(likesTxt.innerHTML)-1);
				} else if (response.charAt(2) == '*') {
					likeBookButton.classList.add('selected');
					dislikeBookButton.classList.remove('selected');
					likesTxt.innerHTML = (Number.parseInt(likesTxt.innerHTML)+1);
					dislikesTxt.innerHTML = (Number.parseInt(dislikesTxt.innerHTML)-1);
				}
				likesBookButton.style.display = 'inline-block';
			} else {
				alert(errorMessage);
			}
			hideLoading();
		}
	};
	// Configuramos y enviamos la peticion
	ajaxRequest.open("POST", booksLink+"_related_add_like", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = processLike;
	ajaxRequest.send("data=1&"+csrfParameterName+"="+csrfToken);
};
likeBookButton.onclick = likeBook;

//Añadimos el evento para marcar como no me gusta un libro
var dislikeBook = function() {
	showLoading();
	var ajaxRequest = new XMLHttpRequest();
	var processDisLike = function() {
		if (ajaxRequest.readyState === 4) {
			// Comprobamos los datos obtenidos
			var response = ajaxRequest.responseText;
			if (ajaxRequest.status === 200 && response && response.startsWith('OK') && response.length == 3) {
				// Marcamos los botones segun el dato recibido +=creado -=borrado *=cambiado(like por dislike o vicebersa)
				if (response.charAt(2) == '+') {
					dislikeBookButton.classList.add('selected');
					dislikesTxt.innerHTML = (Number.parseInt(dislikesTxt.innerHTML)+1);
				} else if (response.charAt(2) == '-') {
					dislikeBookButton.classList.remove('selected');
					dislikesTxt.innerHTML = (Number.parseInt(dislikesTxt.innerHTML)-1);
				} else if (response.charAt(2) == '*') {
					dislikeBookButton.classList.add('selected');
					likeBookButton.classList.remove('selected');
					likesTxt.innerHTML = (Number.parseInt(likesTxt.innerHTML)-1);
					dislikesTxt.innerHTML = (Number.parseInt(dislikesTxt.innerHTML)+1);
				}
				likesBookButton.style.display = 'inline-block';
			} else {
				alert(errorMessage);
			}
			hideLoading();
		}
	};
	// Configuramos y enviamos la peticion
	ajaxRequest.open("POST", booksLink+"_related_add_like", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = processDisLike;
	ajaxRequest.send("data=0&"+csrfParameterName+"="+csrfToken);
};
dislikeBookButton.onclick = dislikeBook;
} else {
	console.debug('no haty botones');
}
/* ************************************************************************************
			Gestion de historico de megusta
** ************************************************************************************/

var likesBookButton = document.getElementById("likesBookButton");
if (likesBookButton != null) {
	// Registramos el evento para mostrar el historial
	var showLikes = function() {
		document.getElementById("likesFormBackground").style.display = "inline";
		recargarLikes();
	}
	likesBookButton.onclick = showLikes;

	//Registramos el evento de salir del historial
	var buttonCloseUser = document.getElementById("closeButtonLikes");
	buttonCloseUser.onclick = function() {
		document.getElementById("likesFormBackground").style.display = "none";
	};

	//Metodo para buscar el historial de prestamos
	var recargarLikes = function() {
		//Creamos un peticion AJAX
		var ajaxRequest = new XMLHttpRequest();
		// Creamos la funcion que se lanzara cuando los datos esten listos
		var processSearch = function() {
			if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
				// Transformamos los datos obtenidos a notacion JSON
				var likesData = JSON.parse(ajaxRequest.responseText);
				// Obtenemos la tabla donde mostraremos los prestamos
				var list = document.getElementById("likesDataTable");
				// Vaciamos la lista
				while (list.firstChild) {
					list.removeChild(list.firstChild);
				}
				// Si no tenemos datos no hacemos nada
				if (!likesData || likesData.length == 0) {
					return;
				}
				// Recorremos todos los datos obtenidos
				for (var i = 0; i < likesData.length; i++) {
					var historyData = likesData[i];
					addLikeToList(historyData.usuario.nombre, historyData.megusta, historyData.libro.meGustaUsrTxt, list);
				}
			} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
				alert(errorMessage);
			}
		};
		// Configuramos y enviamos la peticion
		ajaxRequest.open("POST", booksLink+"_related_new_like", true);
		ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		ajaxRequest.onreadystatechange = processSearch;
		ajaxRequest.send("data=" + document.getElementById("bookId").value+"&"+csrfParameterName+"="+csrfToken);
	}

	var addLikeToList = function(nombre, likeVal, likeTxt, list, asFirst) {
		// Creamos la estructura para la tabla
		var tr = document.createElement("tr");
		
		var td = document.createElement("td");
		td.innerHTML = nombre;
		td.title = nombre;
		tr.appendChild(td);
		
		var td = document.createElement("td");
		if(likeVal) {
			td.innerHTML = '<svg viewBox="0 0 16 16" preserveAspectRatio="xMidYMid meet" focusable="false" style="fill: #7aa834; width: 22px; height: 22px;">'
								+'<g>'
									+'<path d="M15.6 8.2c0 0 0.5-0.5 0.4-1.6 0-1.5-1.6-1.6-1.6-1.6h-2.4c-0.2 0-0.3-0.2-0.3-0.4 0.3-0.7 0.8-2.1 0.6-3.1-0.3-1.4-1.5-1.5-1.9-1.5-0.1 0-0.2 0.1-0.2 0.2l-1 2.8c0 0 0 0.1-0.1 0.1l-2.6 2.8c-0.1 0.1-0.2 0.1-0.3 0.1h-0.2v7h0.2c0.7 0 3.2 2 5.4 2s2.7-0.3 3.1-1c0.4-0.7 0.1-1.3 0.1-1.3s0.5-0.3 0.6-1c0.1-0.7-0.1-1.1-0.1-1.1s0.5-0.4 0.5-1.2c0.1-0.9-0.2-1.2-0.2-1.2z"></path>'
									+'<path d="M0 14h5v-8h-5v8zM2.5 10.5c0.6 0 1 0.4 1 1s-0.4 1-1 1-1-0.4-1-1c0-0.6 0.4-1 1-1z"></path>'
								+'</g>'
							+'</svg>';
		} else {
			td.innerHTML = '<svg viewBox="0 0 16 16" preserveAspectRatio="xMidYMid meet" focusable="false" style="fill: #c83c3c; width: 22px; height: 22px;">'
								+'<g>'
									+'<path d="M15.6 7.8c0 0 0.5 0.5 0.4 1.6 0 1.5-1.6 1.6-1.6 1.6h-2.4c-0.2 0-0.3 0.2-0.3 0.4 0.3 0.7 0.8 2.1 0.6 3.1-0.3 1.4-1.5 1.5-1.9 1.5-0.1 0-0.2-0.1-0.2-0.2l-1-2.8c0 0 0-0.1-0.1-0.1l-2.6-2.8c-0.1-0.1-0.2-0.1-0.3-0.1h-0.2v-7h0.2c0.7 0 3.2-2 5.4-2s2.7 0.3 3.1 1c0.4 0.7 0.1 1.3 0.1 1.3s0.5 0.3 0.6 1c0.1 0.7-0.1 1.1-0.1 1.1s0.5 0.4 0.5 1.2c0.1 0.9-0.2 1.2-0.2 1.2z"></path>'
									+'<path d="M0 11h5v-8h-5v8zM2.5 7.5c0.6 0 1 0.4 1 1s-0.4 1-1 1-1-0.4-1-1c0-0.6 0.4-1 1-1z"></path>'
								+'</g>'
							+'</svg>';
		}
		td.innerHTML += '<span style="vertical-align: top;height: 22px;display: inline-block;line-height: 22px;padding-left: 10px;">'+likeTxt+'<span/>';
		td.title = likeTxt;
		tr.appendChild(td);
		
		if (asFirst && list.childElementCount != 0) {
			list.insertBefore(tr, list.childNodes[0]);
		} else {
			list.appendChild(tr);
		}
		return td;
	}
}