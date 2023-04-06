# M06 - Actividad 3 - OBDC

Este documento describe el código necesario para la tercera actividad del M06, en la que se debe construir una aplicación que permita acceder a profesores y alumnos para realizar diferentes acciones relacionadas con las notas. El programa genera por defecto un usuario administrador con nombre de usuario ‘admin’ y contraseña ‘admin’. Este usuario permite acceder a la gestión de profesores y alumnos, así como a la gestión del historial de eventos.

## Estructuras de datos

La aplicación se basa en una base de datos construida en un esquema BDOR con DB4o y mediante 4 estructuras de datos: profesor, historial, modulo y alumno.

### Clases POJO

Las siguientes clases POJO representan las estructuras de datos:

- Profesor: albergará los datos de los profesores que iniciarán sesión en la aplicación. Estos datos son un id, nombre, nombre de usuario y contraseña. El id será un número que identificará de forma única a los profesores.
- Historial: guardará diferentes eventos que se realicen sobre la aplicación. Los datos son un id, tipo de evento (identificado por una letra), usuario y detalle. El id será un número que identificará de forma única a los eventos. El usuario será el id del usuario (profesor o alumno) que ha generado el evento.
- Modulo: contendrá los datos de cada uno de los módulos de la aplicación. Estos datos son un id y el nombre del módulo (el formato del nombre del módulo será Mxx, donde xx será un número de 2 dígitos). El id será un número que identificará de forma única a los módulos.
- Alumno: guardará los datos de cada una los alumnos y sus notas. Estos datos son un id, nombre, nombre de usuario, password, nota y módulo. El id será un número que identificará de forma única a los alumnos. El módulo será el id del módulo al que se corresponde la nota del alumno.

### Backend

Se debe crear un backend apropiado para la gestión de profesores, llamado ‘NotasBDOR’. Este backend permitirá:

- Insertar un nuevo profesor.
- Eliminar profesor.
- Validar la entrada de un profesor.
- Validar la entrada de un alumno.

El backend anterior se debe modificar para que, al validar la entrada de un profesor, muestre un menú con las siguientes opciones:

- Insertar módulo.
- Listar TODOS los módulos.
- Eliminar módulo.
- Insertar alumno.
- Listar TODOS los alumnos.
- Listar alumnos por módulo.

El backend anterior se debe modificar para que, al validar la entrada de un alumno, muestre un menú con las siguientes opciones:

- Consultar notas.
- Listar módulos de los que es alumno.

También se debe crear un backend apropiado para la gestión del historial, llamado ‘HistorialBDOR’ que permita:

- Listar todos los eventos guardados en el historial.
- Listar los eventos por tipo.

## Programa de demostración

Se debe construir una aplicación Java que demuestre el uso de los diferentes elementos programados, llamado ‘TestBDOR’. Este programa debe mostrar al usuario un menú con las opciones que sean necesarias para poder probar cada una de las funcionalidades.