
/*
 * Clase que accede a la bbdd
 */
package notaslinkia;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.transform.OutputKeys;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XQueryService;

import resources.Alumno;
import resources.Modulo;
import resources.Profesor;

public class NotasXND {

    private final Database database;
    private final String uri = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private final String user = "admin";
    private final String pass = "admin";
    private final String colecNotas = "/db/Notas";

    // Scanner para leer datos por teclado
    Scanner sc = new Scanner(System.in);

    public NotasXND() throws ClassNotFoundException, InstantiationException, IllegalAccessException, XMLDBException {
        String driver = "org.exist.xmldb.DatabaseImpl";
        Class c1 = Class.forName(driver);
        database = (Database) c1.newInstance();
        DatabaseManager.registerDatabase(database);
    }

    public void cerrarConexion() throws XMLDBException {
        DatabaseManager.deregisterDatabase(database);
    }

    public boolean comprobarProfesor(String user, String pass) {
        try {
            String consulta = "for $t in //profesores/profesor where $t/nom_user='" + user + "' and $t/password='"
                    + pass + "' return $t";
            ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
            return resultado.getSize() > 0;
        } catch (XMLDBException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean comprobarAlumno(String user, String pass) {
        try {
            String consulta = "for $t in //alumnos/alumno where $t/nom_user='" + user + "' and $t/password='"
                    + pass + "' return $t";
            ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
            return resultado.getSize() > 0;
        } catch (XMLDBException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarProfesor() throws XMLDBException {
        // Solicitamos los datos del profesor por teclado
        Profesor miProfesor = new Profesor();
        System.out.println("Introduzca el ID del profesor:");
        miProfesor.setId(sc.nextInt());
        System.out.println("Introduzca el nombre del profesor:");
        miProfesor.setNombre(sc.nextLine());
        System.out.println("Introduzca el nombre de usuario:");
        miProfesor.setNomUser(sc.nextLine());
        System.out.println("Introduzca la contraseña:");
        miProfesor.setPassword(sc.nextLine());
        // Comprobamos que no existe un profesor con el mismo nombre de usuario
        if (!existeProfesor(miProfesor)) {
            String consulta = "update insert <Profesor><Id>" + miProfesor.getId() + "</Id><Nombre>"
                    + miProfesor.getNombre() + "</Nombre><NomUser>" + miProfesor.getNomUser() + "</NomUser><Password>"
                    + miProfesor.getPassword() + "</Password></Profesor> into /Profesores";
            ejecutarConsultaUpdate(colecNotas, consulta);
            return true;
        } else {
            return false;
        }
    }

    public void borrarProfesor() throws XMLDBException {
        // Solicitamos el nombre de usuario del profesor a borrar
        System.out.println("Introduzca el nombre de usuario del profesor a borrar:");
        String nomUser = sc.nextLine();
        // Comprobamos que existe un profesor con ese nombre de usuario
        if (existeProfesor(nomUser)) {
            String consulta = "update delete /Profesores/Profesor[NomUser='" + nomUser + "']";
            ejecutarConsultaUpdate(colecNotas, consulta);
        } else {
            System.out.println("No existe ningún profesor con ese nombre de usuario.");
        }
    }

    private boolean existeProfesor(String nomUser) {
        try {
            String consulta = "for $t in //Profesores/Profesor where $t/NomUser='" + nomUser + "' return $t";
            ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
            return resultado.getSize() > 0;
        } catch (XMLDBException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean existeProfesor(Profesor miProfesor) throws XMLDBException {
        String consulta = "for $t in //Libros/Libro/Titulo where $t='" + miProfesor.getNomUser() + "' return $t";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        return resultado.getSize() > 0;
    }

    public void modificarProfesor(Profesor miProfesor) throws XMLDBException {
        // Deberíamos verificar antes que el libro existe
        String consulta = "update replace /Profesores/Profesor[NomUser='" + miProfesor.getNomUser() + "']/Nombre "
                + "with <Nombre>" + miProfesor.getNombre() + "</Nombre>";
        ejecutarConsultaUpdate(colecNotas, consulta);
        consulta = "update replace /Profesores/Profesor[NomUser='" + miProfesor.getNomUser() + "']/Password "
                + "with <Password>" + miProfesor.getPassword() + "</Password>";
        ejecutarConsultaUpdate(colecNotas, consulta);
    }

    public List<Profesor> listarProfesores() throws XMLDBException {
        List<Profesor> listaProfesores = new ArrayList<>();
        String consulta = "for $t in //Profesores/Profesor return $t";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator iterador = resultado.getIterator();
        while (iterador.hasMoreResources()) {
            XMLResource recurso = (XMLResource) iterador.nextResource();
            Node nodo = recurso.getContentAsDOM();
            NodeList listaNodos = nodo.getChildNodes();
            String nombre = listaNodos.item(0).getTextContent();
            String nomUser = listaNodos.item(1).getTextContent();
            String password = listaNodos.item(2).getTextContent();
            Profesor miProfesor = new Profesor(nombre, nomUser, password);
            listaProfesores.add(miProfesor);
        }
        return listaProfesores;
    }

    public void mostrarProfesores() throws XMLDBException {
        List<Profesor> listaProfesores = listarProfesores();
        for (Profesor miProfesor : listaProfesores) {
            System.out.println(miProfesor);
        }
    }

    // Función interna para ejecutar consultas de tipo update
    private void ejecutarConsultaUpdate(String coleccion, String consulta) throws XMLDBException {
        XQueryService servicio = prepararConsulta(coleccion);
        CompiledExpression consultaCompilada = servicio.compile(consulta);
        servicio.execute(consultaCompilada);
    }

    private XQueryService prepararConsulta(String coleccion) throws XMLDBException {
        Collection col = DatabaseManager.getCollection(uri + coleccion, user, pass);
        XQueryService servicio = (XQueryService) col.getService("XQueryService", "1.0");
        servicio.setProperty(OutputKeys.INDENT, "yes");
        servicio.setProperty(OutputKeys.ENCODING, "UTF-8");
        return servicio;
    }

    // Función interna para ejecutar consultas XQuery
    private ResourceSet ejecutarConsultaXQuery(String coleccion, String consulta) throws XMLDBException {
        XQueryService servicio = prepararConsulta(coleccion);
        ResourceSet resultado = servicio.query(consulta);
        return resultado;
    }

    public void pausa() {
        try {
            System.out.println("Pulsa una tecla para continuar...");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertarModulo() throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        imprimirModulos(listarModulos());
        System.out.println();
        // Solicitamos los datos del módulo, color verde
        System.out.print("\033[32mIntroduzca el ID del modulo:\033[0m ");
        String idModulo = sc.nextLine();
        System.out.print("\033[32mIntroduzca el nombre del modulo:\033[0m ");
        String nombreModulo = sc.nextLine();
        System.out.println();
        // Creamos el módulo
        Modulo miModulo = new Modulo(idModulo, nombreModulo);
        // Comprobamos que no existe un módulo con el mismo id
        if (!existeModulo(idModulo)) {
            // Creamos la consulta
            String consulta = "update insert <modulo><id>" + miModulo.getIdModulo() + "</id><nombre>"
                    + miModulo.getNombre() + "</nombre></modulo> into /modulos";
            // Ejecutamos la consulta
            try {
                ejecutarConsultaUpdate(colecNotas, consulta);
            } catch (XMLDBException e) {
                e.printStackTrace();
            }
        } else {
            // Si existe un módulo con el mismo id, mostramos un mensaje de error en rojo
            System.out.println("\033[31m¡Ya existe un módulo con el id " + idModulo + "!\033[0m");
            System.out.println();
            pausa();
        }
    }

    private boolean existeModulo(String idModulo) {
        try {
            // Creamos la consulta
            String consulta = "for $t in //modulos/modulo where $t/id='" + idModulo + "' return $t";
            // Ejecutamos la consulta
            ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
            // Devolvemos true si el resultado tiene algún elemento
            return resultado.getSize() > 0;
        } catch (XMLDBException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Metodo para listar los módulos con una consulta for
    public List<Modulo> listarModulos() throws XMLDBException {
        String consulta = "for $l in //modulos/modulo return $l";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator iterador = resultado.getIterator();
        List<Modulo> todosLosModulos = new ArrayList<>();
        while (iterador.hasMoreResources()) {
            XMLResource res = (XMLResource) iterador.nextResource();
            // Tenemos que leer el resultado como un DOM
            Node nodo = res.getContentAsDOM();
            // Leemos la lista de hijos que son tipo Libro
            NodeList hijo = nodo.getChildNodes();
            // Leemos los hijos del Libro
            NodeList datosLibro = hijo.item(0).getChildNodes();
            Modulo m = leerDomModulo(datosLibro);
            todosLosModulos.add(m);
        }

        return todosLosModulos;

    }

    // Imprimir los datos de un módulo en tabla al pasarle lista de módulos
    public void imprimirModulos(List<Modulo> listaModulos) {
        // Imprimir encabezado de la tabla
        System.out.println("+-------+---------+");
        System.out.printf("| \033[38;5;206m%-5s\033[0m | \033[38;5;206m%-7s\033[0m |\n", "ID", "Nombre");
        System.out.println("+-------+---------+");
        // Imprimir los datos de los módulos
        for (Modulo m : listaModulos) {
            System.out.printf("| \033[38;5;15m%-5s\033[0m | \033[38;5;15m%-7s\033[0m |\n", m.getIdModulo(),
                    m.getNombre());
        }
        // Imprimir pie de la tabla
        System.out.println("+-------+---------+");
    }

    // Método auxiliar que lee los datos de un Libro
    private Modulo leerDomModulo(NodeList datos) {
        int contador = 1;
        Modulo m = new Modulo();
        for (int i = 0; i < datos.getLength(); i++) {
            Node ntemp = datos.item(i);
            if (ntemp.getNodeType() == Node.ELEMENT_NODE) {
                switch (contador) {
                    case 1:
                        m.setIdModulo(ntemp.getChildNodes().item(0).getNodeValue());
                        contador++;
                        break;
                    case 2:
                        m.setNombre(ntemp.getChildNodes().item(0).getNodeValue());
                        contador++;
                        break;
                    default:
                        break;
                }
            }
        }
        return m;
    }

    public void eliminarModulo() throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        imprimirModulos(listarModulos());
        System.out.println();
        // Solicitamos los datos del módulo, color verde
        System.out.print("\033[32mIntroduzca el ID del modulo:\033[0m ");
        String idModulo = sc.nextLine();
        System.out.println();
        // Comprobamos si existe el módulo
        if (existeModulo(idModulo)) {
            // Creamos la consulta
            String consulta = "update delete //modulos/modulo[id='" + idModulo + "']";
            // Ejecutamos la consulta
            try {
                ejecutarConsultaUpdate(colecNotas, consulta);
            } catch (XMLDBException e) {
                e.printStackTrace();
            }
        } else {
            // Si existe un módulo con el mismo id, mostramos un mensaje de error en rojo
            System.out.println("\033[31m¡No existe un módulo con el id " + idModulo + "!\033[0m");
            System.out.println();
            pausa();
        }
    }

    public void insertarAlumno() throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        imprimirAlumnos(listarAlumnos());
        System.out.println();
        // Solicitamos los datos del alumno, color verde
        System.out.print("\033[32mIntroduzca el ID del alumno:\033[0m ");
        String idAlumno = sc.nextLine();
        System.out.print("\033[32mIntroduzca el nombre del alumno:\033[0m ");
        String nombreAlumno = sc.nextLine();
        System.out.print("\033[32mIntroduzca el usuario del alumno:\033[0m ");
        String usuarioAlumno = sc.nextLine();
        System.out.print("\033[32mIntroduzca la contraseña del alumno:\033[0m ");
        String contrasenaAlumno = sc.nextLine();
        System.out.println();
        // Comprobamos si existe el alumno
        if (!existeAlumno(idAlumno)) {
            // Creamos la consulta
            String consulta = "update insert <alumno><id>" + idAlumno + "</id><nombre>" + nombreAlumno
                    + "</nombre><usuario>" + usuarioAlumno + "</usuario><contrasena>" + contrasenaAlumno
                    + "</contrasena></alumno> into //alumnos";
            // Ejecutamos la consulta
            try {
                ejecutarConsultaUpdate(colecNotas, consulta);
            } catch (XMLDBException e) {
                e.printStackTrace();
            }
        } else {
            // Si existe un alumno con el mismo id, mostramos un mensaje de error en rojo
            System.out.println("\033[31m¡Ya existe un alumno con el id " + idAlumno + "!\033[0m");
            System.out.println();
            pausa();
        }
    }

    private boolean existeAlumno(String idAlumno) {
        try {
            // Creamos la consulta
            String consulta = "for $t in //alumnos/alumno where $t/id='" + idAlumno + "' return $t";
            // Ejecutamos la consulta
            ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
            // Devolvemos true si el resultado tiene algún elemento
            return resultado.getSize() > 0;
        } catch (XMLDBException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Alumno> listarAlumnos() throws XMLDBException {
        String consulta = "for $l in //alumnos/alumno return $l";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator iterador = resultado.getIterator();
        List<Alumno> todosLosAlumnos = new ArrayList<>();
        while (iterador.hasMoreResources()) {
            XMLResource res = (XMLResource) iterador.nextResource();
            // Tenemos que leer el resultado como un DOM
            Node nodo = res.getContentAsDOM();
            // Leemos la lista de hijos que son tipo Libro
            NodeList hijo = nodo.getChildNodes();
            // Leemos los hijos del Libro
            NodeList datosLibro = hijo.item(0).getChildNodes();
            Alumno a = leerDomAlumno(datosLibro);
            todosLosAlumnos.add(a);
        }
        return todosLosAlumnos;
    }

    // Método auxiliar que lee los datos de un alumno
    private Alumno leerDomAlumno(NodeList datos) throws XMLDBException {
        int contador = 1;
        Alumno a = new Alumno();
        for (int i = 0; i < datos.getLength(); i++) {
            Node ntemp = datos.item(i);
            if (ntemp.getNodeType() == Node.ELEMENT_NODE) {
                switch (contador) {
                    case 1:
                        a.setIdAlumno(ntemp.getChildNodes().item(0).getNodeValue());
                        contador++;
                        break;
                    case 2:
                        a.setNombre(ntemp.getChildNodes().item(0).getNodeValue());
                        contador++;
                        break;
                    case 3:
                        a.setNomUser(ntemp.getChildNodes().item(0).getNodeValue());
                        contador++;
                        break;
                    case 4:
                        a.setPassword(ntemp.getChildNodes().item(0).getNodeValue());
                        contador++;
                        break;
                    case 5:
                    if (ntemp.getChildNodes().item(0).getNodeValue().equals("null")) {
                        a.setNota(0.0);
                    } else {
                        a.setNota(Double.parseDouble(ntemp.getChildNodes().item(0).getNodeValue()));
                    }
                        contador++;
                        break;
                    case 6:
                        a.setIdModulo(ntemp.getChildNodes().item(0).getNodeValue());
                        contador++;
                        break;
                }
            }
        }
        return a;
    }


    // Imprimir los datos alumnos en formato tabla
    public void imprimirAlumnos(List<Alumno> listaAlumnos)  {
        // Imprimir encabezado de la tabla
        System.out.println("+-------+-----------------+-----------------+-----------------+---------+----------+");
        System.out.printf(
                "| \033[38;5;206m%-5s\033[0m | \033[38;5;206m%-15s\033[0m | \033[38;5;206m%-15s\033[0m | \033[38;5;206m%-15s\033[0m | \033[38;5;206m%-7s\033[0m | \033[38;5;206m%-8s\033[0m |\n",
                "ID", "Nombre", "NomUser", "Password", "Nota", "IDModulo");
        System.out.println("+-------+-----------------+-----------------+-----------------+---------+----------+");
        // Imprimir los datos de los alumnos
        for (Alumno a : listaAlumnos) {
            System.out.printf(
                    "| \033[38;5;15m%-5s\033[0m | \033[38;5;15m%-15s\033[0m | \033[38;5;15m%-15s\033[0m | \033[38;5;15m%-15s\033[0m | \033[38;5;15m%-7s\033[0m | \033[38;5;15m%-8s\033[0m |\n",
                    a.getIdAlumno(), a.getNombre(), a.getNomUser(), a.getPassword(), a.getNota(), a.getIdModulo());
        }
        System.out.println("+-------+-----------------+-----------------+-----------------+---------+----------+");
        System.out.println();
    }

    public void modificarAlumno() throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        imprimirAlumnos(listarAlumnos());
        System.out.println();
        // Solicitamos los datos del alumno, color verde, si no se introduce nada, se
        // queda el valor anterior
        System.out.print("\033[32mIntroduce el id del alumno a modificar: \033[0m");
        String idAlumno = sc.nextLine();
        if (existeAlumno(idAlumno)) {
            System.out.print("\033[32mIntroduce el nombre del alumno: \033[0m");
            String nombre = sc.nextLine();
            System.out.print("\033[32mIntroduce el nombre de usuario: \033[0m");
            String nomUser = sc.nextLine();
            System.out.print("\033[32mIntroduce la contraseña: \033[0m");
            String password = sc.nextLine();
            System.out.print("\033[32mIntroduce la nota: \033[0m");
            String nota = sc.nextLine();
            System.out.print("\033[32mIntroduce el id del módulo: \033[0m");
            String idModulo = sc.nextLine();
            // Creamos el alumno
            Alumno a = new Alumno();
            a.setIdAlumno(idAlumno);
            // Se comprueba si existe, si no existe muestra un mensaje de error
            if (existeAlumno(idAlumno)) {
                // Se comprueba si se ha introducido algo, si no se ha introducido nada, se
                // queda el valor anterior
                if (idAlumno.equals("")) {
                    idAlumno=a.getIdAlumno();
                }
                if (nombre.equals("")) {
                    nombre=a.getNombre();
                }
                if (!nomUser.equals("")) {
                    nomUser=a.getNomUser();
                }
                if (password.equals("")) {
                    password=a.getPassword();
                }
                if (nota.equals("")) {
                    nota=String.valueOf(a.getNota());
                }
                if (idModulo.equals("")) {
                    idModulo=a.getIdModulo();
                }
                // Creamos la consulta
                String consulta = "update replace //alumnos/alumno[id='" + idAlumno + "']"
                        + " with <alumno>" + "<id>" + idAlumno + "</id>"
                        + "<nombre>" + nombre + "</nombre>"
                        + "<nom_user>" + nomUser + "</nom_user>"
                        + "<password>" + password + "</password>"
                        + "<nota>" + nota + "</nota>"
                        + "<modulo>" + idModulo + "</modulo></alumno>";
                // Ejecutamos la consulta
                ejecutarConsultaUpdate(colecNotas, consulta);
                System.out.println("\033[32mAlumno modificado correctamente\033[0m");
            } else {
                System.out.println("\033[31mEl alumno no existe\033[0m");
            }
        } else {
            System.out.println("\033[31mEl alumno no existe\033[0m");
        }
    }

    public void listarAlumnosPorModulo(Object object) {
    }

    public void eliminarAlumno() {
    }

    public void listarNotas2() {
    }

    public void listarNotasPorAlumno(int i) {
    }

    public void insertarNota() {
    }

    public void modificarNota() {
    }

    public void eliminarNota() {
    }

    public void modificarProfesor() {
    }

}
