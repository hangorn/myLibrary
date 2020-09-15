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


var markPendingButton = document.getElementById("markPendingBookButton");
var unmarkPendingButton = document.getElementById("unmarkPendingBookButton");
var buttonPending = markPendingButton || unmarkPendingButton;
if (buttonPending != null) {
//Añadimos el evento para marcar como pendiente un libro
var markPending = function() {
	document.getElementById("selectDateFormBackground").style.display = "inline";
	var input = document.getElementById('selectedDate');
	input.focus();
	input.select();
	document.getElementById("selectedDate").valueAsDate = new Date();
	document.getElementById("acceptSelectDate").onclick = acceptMarkPending;
	document.getElementById("selectDateFormContainer").onkeydown = function() {
		// Si se ha presionado enter
		if (event.keyCode === 13) {
			acceptMarkPending();
			event.preventDefault();
			return false;
		}
	};

	//Registramos el evento de salir del proceso de marcar como pendiente
	document.getElementById("cancelSelectDate").onclick = function() {
		document.getElementById("selectDateFormBackground").style.display = "none";
	};
};

if(markPendingButton != null) {
	buttonPending.onclick = markPending;
}

//Registramos el evento para cuando se confirme marcar un libro como pendiente
var acceptMarkPending = function() {
	var valueDate = document.getElementById("selectedDate").value;
	var formattedDate = formatDate(valueDate);
	
	var ajaxRequest = new XMLHttpRequest();
	ajaxRequest.open("POST", pendingLink + "_acceptCreation", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			document.getElementById("selectDateFormBackground").style.display = "none";
			
			buttonPending.onclick = unmarkPending;
			buttonPending.title = messageUnmarkPending.replace("{0}",formattedDate);
			var img = buttonPending.getElementsByTagName("img")[0];
			img.src = img.src.replace("pending","no_pending");
			img.title = buttonPending.title;
			img.alt = buttonPending.title;
			var label = buttonPending.getElementsByTagName("label")[0];
			label.innerText = buttonPending.title;
			hideLoading();
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
			hideLoading();
		}
	};
	ajaxRequest.send("libro.id=" + document.getElementById("bookId").value + "&fecha="+ valueDate +"&acceptCreation&"+csrfParameterName+"="+csrfToken);
	showLoading();
};

//Añadimos el evento para desmarcar un libro como leido
var unmarkPending = function() {
	var ajaxRequest = new XMLHttpRequest();
	ajaxRequest.open("POST", pendingLink+"_delete", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			buttonPending.onclick = markPending;
			buttonPending.title = messageMarkPending;
			var img = buttonPending.getElementsByTagName("img")[0];
			img.src = img.src.replace("no_pending","pending");
			img.title = messageMarkPending;
			img.alt = messageMarkPending;
			var label = buttonPending.getElementsByTagName("label")[0];
			label.innerText = messageMarkPending;
			hideLoading();
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
			hideLoading();
		}
	};
	ajaxRequest.send("delete="+ document.getElementById("bookId").value +"&"+csrfParameterName+"="+csrfToken);
	showLoading();
};

if (unmarkPendingButton != null) {
	buttonPending.onclick = unmarkPending;
}
}