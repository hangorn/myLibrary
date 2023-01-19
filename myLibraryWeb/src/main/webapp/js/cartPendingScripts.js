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
if (markPendingButton != null) {
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
	markPendingButton.onclick = markPending;
}

//Registramos el evento para cuando se confirme marcar un libro como pendiente
var acceptMarkPending = function() {
	var form = document.createElement("form");
	form.action = pendingLink + "_cartBooks";
	form.method = "post";
	document.getElementById("selectDateFormBackground").appendChild(form);
	
	var params = [
		["libro.id", "1"],
		["fecha", document.getElementById("selectedDate").value],
		[csrfParameterName, csrfToken]
	]; 
	for (var param of params) {
		var hidden = document.createElement("input");
		hidden.type = "hidden";
		hidden.name = param[0];
		hidden.value = param[1];
		form.appendChild(hidden);
	}
	form.submit();
	showLoading();
};
}