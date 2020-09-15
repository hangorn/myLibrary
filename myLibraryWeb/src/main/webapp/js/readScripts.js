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


// ID ficticio para los libros que se marquen como leidos
var idMarkRead = 0;
var markReadButton = document.getElementById("markReadButton");
if (markReadButton != null) {
//Añadimos el evento para marcar como leido un libro
var markRead = function() {
	document.getElementById("selectDateFormBackground").style.display = "inline";
	var input = document.getElementById('selectedDate');
	input.focus();
	input.select();
	document.getElementById("selectedDate").valueAsDate = new Date();
	document.getElementById("acceptSelectDate").onclick = acceptMarkRead;
	document.getElementById("selectDateFormContainer").onkeydown = function() {
		// Si se ha presionado enter
		if (event.keyCode === 13) {
			acceptMarkRead();
			event.preventDefault();
			return false;
		}
	};

	//Registramos el evento de salir del proceso de marcar como leido
	document.getElementById("cancelSelectDate").onclick = function() {
		document.getElementById("selectDateFormBackground").style.display = "none";
	};
};

if(markReadButton != null) {
	markReadButton.onclick = markRead;
}

//Registramos el evento para cuando se confirme marcar un libro como leido
var acceptMarkRead = function() {
	var valueDate = document.getElementById("selectedDate").value;
	var formattedDate = formatDate(valueDate);
	
	var ajaxRequest = new XMLHttpRequest();
	ajaxRequest.open("POST", readLink + "_acceptCreation", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			document.getElementById("selectDateFormBackground").style.display = "none";

			var img = markReadButton.getElementsByTagName("img")[0];
			var imgSrc = img.src.replace("mark_read","unmark_read");
			var txt = messageUnmarkRead.replace("{0}",formattedDate);
			var button = document.createElement('button');
			button.title = txt;
			button.type = "button";
			button.className = "unmarkReadBookButton";
			button.id = "unmarkReadBookButton-"+idMarkRead;
			button.innerHTML = 
				'<img width="32px" height="32px" alt="'+txt+'" title="'+txt+'" src="'+imgSrc+'"/>'+
				'<label>'+txt+'</label>';
			button.onclick = unmarkRead;
			markReadButton.parentElement.appendChild(button);
			
			// Si el libro esta marcado como pendiente, cambiamos el boton de desmarcar por el de marcar (ya no estara pendiente)
			if (buttonPending.onclick == unmarkPending) {
				buttonPending.onclick = markPending;
				buttonPending.title = messageMarkPending;
				var img = buttonPending.getElementsByTagName("img")[0];
				img.src = img.src.replace("no_pending","pending");
				img.title = messageMarkPending;
				img.alt = messageMarkPending;
				var label = buttonPending.getElementsByTagName("label")[0];
				label.innerText = messageMarkPending;
			}
			hideLoading();
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
			hideLoading();
		}
	};
	ajaxRequest.send("libro.id=" + document.getElementById("bookId").value + "&id="+ --idMarkRead + "&fechaTxt="+ valueDate +"&acceptCreation&"+csrfParameterName+"="+csrfToken);
	showLoading();
};

//Añadimos el evento para desmarcar un libro como leido
var unmarkRead = function(event) {
	var button = event.target;
	while (button.tagName != "BUTTON") {
		button = button.parentElement;
	}
	
	var ajaxRequest = new XMLHttpRequest();
	ajaxRequest.open("POST", readLink+"_delete", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			button.remove();
			hideLoading();
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
			hideLoading();
		}
	};
	var clickedId = button.id.replace("unmarkReadBookButton-", "");
	ajaxRequest.send("delete="+ clickedId +"&"+csrfParameterName+"="+csrfToken);
	showLoading();
};

var unmarkReadButtons = document.getElementsByClassName("unmarkReadBookButton");
for (unmarkReadButton of unmarkReadButtons) {
	unmarkReadButton.onclick = unmarkRead;
}
}