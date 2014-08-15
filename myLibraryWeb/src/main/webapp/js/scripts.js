index = -1;

// Metodo para fijar un fila como seleccionada
function setSelected(domElement) {
	// Primero buscamos la fila anteriormente seleccionada
	var rows = document.getElementsByClassName("selectedRow");
	for (var i = 0; i < rows.length; i++) {
		rows[i].className = "";
	}
	// Añadimos la clase al elemento
	domElement.className += " selectedRow";
	index = domElement.id.split("_")[1];
}

// submit the specified form with the params
function submit(formID, paramName, paramValue) {
	var form = document.getElementById(formID);
	var hidden = document.createElement("input");
	hidden.type = "hidden";
	hidden.name = paramName;
	hidden.value = paramValue;
	form.appendChild(hidden);
	form.submit();
}

function clearSearch() {
	// Primero buscamos todos los contenedores del formulario de busqueda
	var rows = document.getElementsByClassName("searchField");
	for (var i = 0; i < rows.length; i++) {
		// Recorremos todos los elementos de cada contenedor
		for (var j = 0; j < rows[i].children.length; j++) {
			var element = rows[i].children[j];
			// Si es una entrada de texto
			if (element.tagName = "INPUT") {
				element.value = "";
			}
			// Si es una entrada de texto
			if (element.tagName = "SELECT") {
				element.selectedIndex = 0;
			}
		}
	}
}