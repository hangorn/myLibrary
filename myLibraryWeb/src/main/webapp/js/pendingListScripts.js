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

var listDate;
var modificarFechaLista = function(_this) {
	document.getElementById('selectDateFormBackground').style.display = 'block';
	listDate = _this.parentElement.children[0];
	var splittedDate = listDate.textContent.split("/");
	document.getElementById('selectedDate').valueAsDate = new Date(splittedDate[2], splittedDate[1]-1, splittedDate[0], 3);
	
	document.getElementById("acceptSelectDate").onclick = acceptUpdateDate;
	document.getElementById("cancelSelectDate").onclick = function() {
		document.getElementById("selectDateFormBackground").style.display = "none";
	};
}

var acceptUpdateDate = function() {
	var valueDate = document.getElementById("selectedDate").value;
	var formattedDate = formatDate(valueDate);
	
	var ajaxRequest = new XMLHttpRequest();
	ajaxRequest.open("POST", pendingLink + "_acceptUpdate", true);
	ajaxRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	ajaxRequest.onreadystatechange = function() {
		if (ajaxRequest.readyState === 4 && ajaxRequest.status === 200) {
			document.getElementById("selectDateFormBackground").style.display = "none";
			
			listDate.textContent = formattedDate;
			hideLoading();
		} else if (ajaxRequest.readyState === 4 && ajaxRequest.status !== 200) {
			alert(errorMessage);
			hideLoading();
		}
	};
	var dateVar = pendingLink.endsWith("/pending") ? "fecha" : "fechaTxt";
	ajaxRequest.send("id=" + listDate.parentElement.children[2].value + "&libro.id=" + listDate.parentElement.children[1].value + "&" + dateVar + "=" + valueDate +"&acceptUpdate&"+csrfParameterName+"="+csrfToken);
	showLoading();
};