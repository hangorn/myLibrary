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


//Añadimos el evento para añadir un libro al carrito
var addBookToCart = function(button, bookId) {
	var ajaxRequest = new XMLHttpRequest();
	ajaxRequest.open("POST", cartLink + "_related_add_books", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.send("data="+bookId+"&"+csrfParameterName+"="+csrfToken);
	ajaxRequest.onreadystatechange = function() {
		if (ajaxRequest.readyState === 4) {
			if (ajaxRequest.status === 200 && ajaxRequest.responseText != "FAIL") {
				// Actualizamos el numero de libros en la cesta
				document.getElementById("numBooksInCart").innerHTML = ajaxRequest.responseText;
				// Cambiamos el boton de añadir por el de quitar
				var imgBtn = button.getElementsByTagName("img")[0];
				imgBtn.src = imgBtn.src.replace("add", "remove");
				button.title = imgBtn.title = messageRemoveCart;
				button.onclick = function() {
					removeBookFromCart(button, bookId);
				};
			}
			hideLoading();	
		}
	};
	showLoading();
};


//Añadimos el evento para quitar un libro del carrito
var removeBookFromCart = function(button, bookId) {
	var ajaxRequest = new XMLHttpRequest();
	ajaxRequest.open("POST", cartLink + "_related_delete_books", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.send("data="+bookId+"&"+csrfParameterName+"="+csrfToken);
	ajaxRequest.onreadystatechange = function() {
		if (ajaxRequest.readyState === 4) {
			if (ajaxRequest.status === 200 && ajaxRequest.responseText != "FAIL") {
				// Actualizamos el numero de libros en la cesta
				document.getElementById("numBooksInCart").innerHTML = ajaxRequest.responseText;
				// Cambiamos el boton de quitar por el de añadir
				var imgBtn = button.getElementsByTagName("img")[0];
				imgBtn.src = imgBtn.src.replace("remove", "add"); 
				button.title = imgBtn.title = messageAddCart;
				button.onclick = function() {
					addBookToCart(button, bookId);
				}
			}
			hideLoading();	
		}
	};
	showLoading();
};
