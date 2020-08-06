CREATE TABLE ubicaciones(
	id INTEGER NOT NULL AUTO_INCREMENT,
	codigo VARCHAR(10),
	descripcion VARCHAR(50),
	CONSTRAINT pk_ubicaciones PRIMARY KEY(id)
);

CREATE TABLE tipos(
	id INTEGER NOT NULL AUTO_INCREMENT,
	descripcion VARCHAR(50) NOT NULL,
	CONSTRAINT pk_tipos PRIMARY KEY(id)
);

CREATE TABLE traductores(
	id INTEGER NOT NULL AUTO_INCREMENT,
	nombre VARCHAR(50) NOT NULL,
	CONSTRAINT pk_traductores PRIMARY KEY(id)
);

CREATE TABLE autores(
	id INTEGER NOT NULL AUTO_INCREMENT,
	nombre VARCHAR(50),
	apellidos VARCHAR(50),
	pais VARCHAR(50),
	ciudad VARCHAR(50),
	anno_nacimiento INTEGER,
	anno_fallecimiento INTEGER,
	notas VARCHAR(50),
	CONSTRAINT pk_autores PRIMARY KEY(id)
);

CREATE TABLE editoriales(
	id INTEGER NOT NULL AUTO_INCREMENT,
	nombre VARCHAR(50) NOT NULL,
	ciudad VARCHAR(50),
	CONSTRAINT pk_editoriales PRIMARY KEY(id)
);

CREATE TABLE colecciones(
	id INTEGER NOT NULL AUTO_INCREMENT,
	nombre VARCHAR(50) NOT NULL,
	editorial INTEGER,
	CONSTRAINT pk_colecciones PRIMARY KEY(id),
	CONSTRAINT fk_coleciones_editoriales FOREIGN KEY (editorial) REFERENCES editoriales(id)
);

CREATE TABLE libros(
	id INTEGER NOT NULL AUTO_INCREMENT,
	titulo VARCHAR(100),
	isbn VARCHAR(20),
	anno_compra INTEGER,
	anno_publicacion INTEGER,
	anno_copyright INTEGER,
	num_edicion INTEGER,
	num_paginas INTEGER,
	tomo INTEGER,
	precio FLOAT,
	notas VARCHAR(100),
	
	editorial INTEGER,
	tipo INTEGER,
	ubicacion INTEGER,
	coleccion INTEGER,
	
	CONSTRAINT fk_libros_editoriales FOREIGN KEY (editorial) REFERENCES editoriales(id),
	CONSTRAINT fk_libros_tipos FOREIGN KEY (tipo) REFERENCES tipos(id),
	CONSTRAINT fk_libros_ubicaciones FOREIGN KEY (ubicacion) REFERENCES ubicaciones(id),
	CONSTRAINT fk_libros_colecciones FOREIGN KEY (coleccion) REFERENCES colecciones(id),
	CONSTRAINT pk_libros PRIMARY KEY(id)
);

CREATE TABLE libros_autores(
	libro INTEGER,
	autor INTEGER,
	CONSTRAINT fk_libros_autores_libros FOREIGN KEY (libro) REFERENCES libros(id),
	CONSTRAINT fk_libros_autores_autores FOREIGN KEY (autor) REFERENCES autores(id)
);
	
CREATE TABLE libros_traductores(
	libro INTEGER,
	traductor INTEGER,
	CONSTRAINT fk_libros_traductores_libros FOREIGN KEY (libro) REFERENCES libros(id),
	CONSTRAINT fk_libros_traductores_traductores FOREIGN KEY (traductor) REFERENCES traductores(id)
);

CREATE TABLE modificaciones(
	id INTEGER NOT NULL AUTO_INCREMENT,
	id_dato INTEGER NOT NULL,
	tabla VARCHAR(30) NOT NULL,
	fecha VARCHAR(20) NOT NULL,
	CONSTRAINT pk_modificaciones PRIMARY KEY(id)
);

CREATE TABLE modificaciones_campos (
	id_modificacion INTEGER NOT NULL,
	columna VARCHAR(30) NOT NULL,
	valor VARCHAR(100) NOT NULL,
	CONSTRAINT pk_modificaciones PRIMARY KEY(id_modificacion, columna),
	CONSTRAINT fk_modificaciones_campos_modificaciones FOREIGN KEY (id_modificacion) REFERENCES modificaciones(id)
);

CREATE TABLE usuarios (
  username varchar(50) NOT NULL,
  password varchar(60) NOT NULL,
  email varchar(150) DEFAULT NULL,
  nombre varchar(60),
  enabled tinyint(4) NOT NULL DEFAULT '1',
  admin tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (username),
  UNIQUE KEY uk_email (email)
)

CREATE TABLE prestamos (
	id INTEGER NOT NULL AUTO_INCREMENT,
	libro INTEGER NOT NULL,
	usuario varchar(50) NOT NULL,
	fecha varchar(10) NOT NULL,
	CONSTRAINT pk_prestamos PRIMARY KEY(id),
	CONSTRAINT fk_prestamos_libros FOREIGN KEY (libro) REFERENCES libros(id),
	CONSTRAINT fk_prestamos_usuarios FOREIGN KEY (usuario) REFERENCES usuarios(username)
);

CREATE TABLE pendientes (
	id INTEGER NOT NULL AUTO_INCREMENT,
	libro INTEGER NOT NULL,
	usuario varchar(50) NOT NULL,
	fecha varchar(10) NOT NULL,
	CONSTRAINT pk_pendientes PRIMARY KEY(id),
	CONSTRAINT fk_pendientes_libros FOREIGN KEY (libro) REFERENCES libros(id),
	CONSTRAINT fk_pendientes_usuarios FOREIGN KEY (usuario) REFERENCES usuarios(username)
);

CREATE TABLE leidos (
	id INTEGER NOT NULL AUTO_INCREMENT,
	libro INTEGER NOT NULL,
	usuario varchar(50) NOT NULL,
	fecha INTEGER NOT NULL,
	CONSTRAINT pk_leidos PRIMARY KEY(id),
	CONSTRAINT fk_leidos_libros FOREIGN KEY (libro) REFERENCES libros(id),
	CONSTRAINT fk_leidos_usuarios FOREIGN KEY (usuario) REFERENCES usuarios(username)
);