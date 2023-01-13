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

var allowAccessButton = document.getElementById("allowAccessButton");
//Añadimos el evento para dar acceso a un usuario
var allowAccessEvent = function() {
	document.getElementById("allowAccessFormBackground").style.display = "inline";
	var input = document.getElementById('newPassword');
	input.focus();
	input.select();
	document.getElementById("acceptAllowAccess").onclick = acceptAllowAccess;

	//Registramos el evento de salir del proceso de marcar como leido
	document.getElementById("cancelAllowAccess").onclick = function() {
		document.getElementById("allowAccessFormBackground").style.display = "none";
	};
};
if (allowAccessButton != null) {
	allowAccessButton.onclick = allowAccessEvent;
}

var acceptAllowAccess = function() {
	var valuePassword = document.getElementById("newPassword").value;
	var valueUserId = document.getElementById("userId").value;
	
	var ajaxRequest = new XMLHttpRequest();
	ajaxRequest.open("POST", usersLink + "_related_new_access", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = function() {
		if (ajaxRequest.readyState === 4) {
			if (ajaxRequest.status === 200 && ajaxRequest.responseText == "OK") {
				document.getElementById("allowAccessFormBackground").style.display = "none";
				allowAccessButton.style.display = "none";
				hideLoading();
			} else {
				alert(errorMessage);
				hideLoading();
			}
		}
	};
	ajaxRequest.send("data=" + valueUserId + "|" + valuePassword + "&" + csrfParameterName + "=" + csrfToken);
	showLoading();
};