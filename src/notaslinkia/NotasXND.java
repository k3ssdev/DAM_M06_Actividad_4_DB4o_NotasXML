
/*
 * Clase que accede a la bbdd
 */
package notaslinkia;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.transform.OutputKeys;

import org.exist.dom.ContextItem;
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
            String consulta = "for $t in //profesores/profesor where $t/nomUser='" + user + "' and $t/password='"
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

    public void insertarProfesor() throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        imprimirProfesores(listarProfesores());
        System.out.println();
        // Solicitamos los datos del alumno, color verde
        System.out.print("\033[32mIntroduzca el ID del profesor:\033[0m ");
        String id = sc.nextLine();
        System.out.print("\033[32mIntroduzca el nombre del profesor:\033[0m ");
        String nombreAlumno = sc.nextLine();
        System.out.print("\033[32mIntroduzca el usuario del profesor:\033[0m ");
        String usuarioAlumno = sc.nextLine();
        System.out.print("\033[32mIntroduzca la contraseña del profesor:\033[0m ");
        String contrasenaAlumno = sc.nextLine();
        System.out.println();
        // Comprobamos si existe el alumno
        if (!existeProfesor(contrasenaAlumno)) {
            // Creamos la consulta
            String consulta = "update insert <profesor><id>" + id + "</id><nombre>" + nombreAlumno
                    + "</nombre><nomUser>" + usuarioAlumno + "</nomUser><password>" + contrasenaAlumno
                    + "</password></profesor> into //profesores";
            // Ejecutamos la consulta
            try {
                ejecutarConsultaUpdate(colecNotas, consulta);
            } catch (XMLDBException e) {
                e.printStackTrace();
            }
        } else {
            // Si existe un alumno con el mismo id, mostramos un mensaje de error en rojo
            System.out.println("\033[31m¡Ya existe un alumno con el id " + id + "!\033[0m");
            System.out.println();
            pausa();
        }
    }

    public void eliminarProfesor() throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        imprimirProfesores(listarProfesores());
        System.out.println();
        // Solicitamos los datos del módulo, color verde
        System.out.print("\033[32mIntroduzca el ID del profesor:\033[0m ");
        String idModulo = sc.nextLine();
        System.out.println();
        // Comprobamos si existe el profesor
        if (existeProfesor(Integer.parseInt(idModulo))) {
            // Creamos la consulta
            String consulta = "update delete //profesores/profesor[id='" + idModulo + "']";
            // Ejecutamos la consulta
            try {
                ejecutarConsultaUpdate(colecNotas, consulta);
            } catch (XMLDBException e) {
                e.printStackTrace();
            }
        } else {
            // Si existe un módulo con el mismo id, mostramos un mensaje de error en rojo
            System.out.println("\033[31m¡No existe profesor con el id " + idModulo + "!\033[0m");
            System.out.println();
            pausa();
        }
    }

    private boolean existeProfesor(String contrasenaAlumno) {
        try {
            String consulta = "for $t in //profesores/profesor where $t/nomUser='" + contrasenaAlumno + "' return $t";
            ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
            return resultado.getSize() > 0;
        } catch (XMLDBException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean existeProfesor(int id) {
        try {
            String consulta = "for $t in //profesores/profesor where $t/id='" + id + "' return $t";
            ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
            return resultado.getSize() > 0;
        } catch (XMLDBException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    public void modificarProfesor(Profesor miProfesor) throws XMLDBException {
        // Deberíamos verificar antes que el libro existe
        String consulta = "update replace /profesores/profesor[nomUser='" + miProfesor.getNomUser() + "']/nombre "
                + "with <nombre>" + miProfesor.getNombre() + "</nombre>";
        ejecutarConsultaUpdate(colecNotas, consulta);
        consulta = "update replace /profesores/profesor[nomUser='" + miProfesor.getNomUser() + "']/password "
                + "with <password>" + miProfesor.getPassword() + "</password>";
        ejecutarConsultaUpdate(colecNotas, consulta);
    }

    public List<Profesor> listarProfesores() throws XMLDBException {
        String consulta = "for $l in //profesores/profesor return $l";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator iterador = resultado.getIterator();
        List<Profesor> todosLosProfesores = new ArrayList<>();
        while (iterador.hasMoreResources()) {
            XMLResource res = (XMLResource) iterador.nextResource();
            // Tenemos que leer el resultado como un DOM
            Node nodo = res.getContentAsDOM();
            // Leemos la lista de hijos que son tipo alunmo
            NodeList hijo = nodo.getChildNodes();
            // Leemos los hijos del alumno
            NodeList datosLibro = hijo.item(0).getChildNodes();
            Profesor a = leerDomProfesor(datosLibro);
            todosLosProfesores.add(a);
        }
        return todosLosProfesores;
    }

    private Profesor leerDomProfesor(NodeList datos) {
        int contador = 1;
        Profesor m = new Profesor();
        for (int i = 0; i < datos.getLength(); i++) {
            Node ntemp = datos.item(i);
            if (ntemp.getNodeType() == Node.ELEMENT_NODE) {
                switch (contador) {
                    case 1:
                        m.setIdProfesor(Integer.parseInt(ntemp.getChildNodes().item(0).getNodeValue()));
                        contador++;
                        break;
                    case 2:
                        m.setNombre(ntemp.getChildNodes().item(0).getNodeValue());
                        contador++;
                        break;
                    case 3:
                        m.setNomUser(ntemp.getChildNodes().item(0).getNodeValue());
                        contador++;
                        break;
                    case 4:
                        m.setPassword(ntemp.getChildNodes().item(0).getNodeValue());
                        contador++;
                        break;

                }
            }
        }
        return m;
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
                    + "</nombre><nom_user>" + usuarioAlumno + "</nom_user><password>" + contrasenaAlumno
                    + "</password></alumno> into //alumnos";
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
            // Leemos la lista de hijos que son tipo alunmo
            NodeList hijo = nodo.getChildNodes();
            // Leemos los hijos del alumno
            NodeList datosLibro = hijo.item(0).getChildNodes();
            Alumno a = leerDomAlumno(datosLibro);
            todosLosAlumnos.add(a);
        }
        return todosLosAlumnos;
    }

    public List<Alumno> listarAlumnos(String id) throws XMLDBException {
        String consulta = "for $l in //alumnos/alumno where $l/id='" + id + "' return $l";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator iterador = resultado.getIterator();
        List<Alumno> todosLosAlumnos = new ArrayList<>();
        while (iterador.hasMoreResources()) {
            XMLResource res = (XMLResource) iterador.nextResource();
            // Tenemos que leer el resultado como un DOM
            Node nodo = res.getContentAsDOM();
            // Leemos la lista de hijos que son tipo alunmo
            NodeList hijo = nodo.getChildNodes();
            // Leemos los hijos del alumno
            NodeList datosLibro = hijo.item(0).getChildNodes();
            Alumno a = leerDomAlumno(datosLibro);
            todosLosAlumnos.add(a);
        }
        return todosLosAlumnos;
    }

    public List<Alumno> listarAlumnosPorModulo() throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        imprimirModulos(listarModulos());
        System.out.println();
        // Solicitamos el id del módulo, color verde
        System.out.print("\033[32mIntroduzca el ID del módulo:\033[0m ");
        String id = sc.nextLine();
        System.out.println();

        String consulta = "for $l in //alumnos/alumno where $l/modulo='" + id + "' return $l";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator iterador = resultado.getIterator();
        List<Alumno> todosLosAlumnos = new ArrayList<>();
        while (iterador.hasMoreResources()) {
            XMLResource res = (XMLResource) iterador.nextResource();
            // Tenemos que leer el resultado como un DOM
            Node nodo = res.getContentAsDOM();
            // Leemos la lista de hijos que son tipo alunmo
            NodeList hijo = nodo.getChildNodes();
            // Leemos los hijos del alumno
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
    public void imprimirAlumnos(List<Alumno> listaAlumnos) {
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
            System.out.println("\033[32m(Si se deja en blanco, se queda el valor anterior)\033[0m");
            System.out.println();
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
            Alumno a = listarAlumnos(idAlumno).get(0);
            // Si no se introduce nada, se queda el valor anterior
            if (nombre.equals("")) {
                nombre = a.getNombre();
            }
            if (nomUser.equals("")) {
                nomUser = a.getNomUser();
            }
            if (password.equals("")) {
                password = a.getPassword();
            }
            if (nota.equals("")) {
                nota = String.valueOf(a.getNota());
            }
            if (idModulo.equals("")) {
                idModulo = a.getIdModulo();

            }
            // Creamos la consulta con el id del alumno ingresado
            String consulta = "update replace //alumnos/alumno[id=" + idAlumno + "]"
                    + " with <alumno> <id>" + idAlumno + "</id>"
                    + "<nombre>" + nombre + "</nombre><nomUser>" + nomUser + "</nomUser><password>"
                    + password + "</password><nota>" + nota + "</nota><idModulo>" + idModulo + "</idModulo></alumno>";
            // Creamos la colección

            // Ejecutamos la consulta
            try {
                ejecutarConsultaUpdate(colecNotas, consulta);
                System.out.println();
                System.out.println("\033[32m¡Alumno modificado con éxito!\033[0m");
                pausa();
            } catch (XMLDBException e) {
                e.printStackTrace();
                System.out.println();
                pausa();
            }
        } else {
            // Si el alumno no existe, mostramos un mensaje de error en rojo
            System.out.println("\033[31m¡No existe ningún alumno con el id " + idAlumno + "!\033[0m");
            System.out.println();
            pausa();
        }

    }

    public void eliminarAlumno() throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        imprimirAlumnos(listarAlumnos());
        System.out.println();
        // Solicitamos el id del alumno a eliminar
        System.out.print("\033[32mIntroduce el id del alumno a eliminar: \033[0m");
        String idAlumno = sc.nextLine();
        if (existeAlumno(idAlumno)) {
            // Si el alumno existe, se elimina
            String consulta = "update delete //alumnos/alumno[id=" + idAlumno + "]";
            // Ejecutamos la consulta
            try {
                ejecutarConsultaUpdate(colecNotas, consulta);
                System.out.println();
                System.out.println("\033[32m¡Alumno eliminado correctamente!\033[0m");
                System.out.println();
                pausa();
            } catch (XMLDBException e) {
                e.printStackTrace();
                System.out.println("\033[31m¡Error al eliminar el alumno!\033[0m");
                pausa();
            }
        } else {
            // Si no existe el alumno, mostramos un mensaje de error en rojo
            System.out.println("\033[31m¡No existe ningún alumno con el id " + idAlumno + "!\033[0m");
            System.out.println();
            pausa();
        }
    }

    public void listarAlumnosPorModulo(Object object) throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        // Mostramos los módulos
        System.out.println("\033[32mMódulos:\033[0m");
        System.out.println();
        imprimirModulos(listarModulos());
        System.out.println();
        // Solicitamos el id del módulo
        System.out.print("\033[32mIntroduce el id del módulo: \033[0m");
        String idModulo = sc.nextLine();
        // Creamos la consulta
        String consulta = "//alumnos/alumno[modulo='1']";
        // Ejecutamos la consulta
        ResourceSet result = ejecutarConsultaXQuery(colecNotas, consulta);
        // Creamos una lista de alumnos
        List<Alumno> listaAlumnos = new ArrayList<>();
        // Recorremos el resultado de la consulta
        ResourceIterator i = result.getIterator();
        while (i.hasMoreResources()) {
            Resource r = i.nextResource();
            listarAlumnos();

        }
        // Mostramos los alumnos
        System.out.println();
        System.out.println("\033[32mAlumnos del módulo " + idModulo + ":\033[0m");
        System.out.println();
        imprimirAlumnos(listaAlumnos);
        System.out.println();
        pausa();
    }

    public void modificarProfesor() throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        imprimirProfesores(listarProfesores());
        System.out.println();
        // Solicitamos los datos del alumno, color verde, si no se introduce nada, se
        // queda el valor anterior
        System.out.print("\033[32mIntroduce el id del profesor a modificar: \033[0m");
        String idProfesor = sc.nextLine();
        if (existeProfesor(Integer.parseInt(idProfesor))) {
            System.out.println("\033[32m(Si se deja en blanco, se queda el valor anterior)\033[0m");
            System.out.println();
            System.out.print("\033[32mIntroduce el nombre del profesor: \033[0m");
            String nombre = sc.nextLine();
            System.out.print("\033[32mIntroduce el nombre de profesor: \033[0m");
            String nomUser = sc.nextLine();
            System.out.print("\033[32mIntroduce la contraseña: \033[0m");
            String password = sc.nextLine();

            // Creamos el profesor
            Profesor a = listarProfesores().get(Integer.parseInt(idProfesor));
            // Si no se introduce nada, se queda el valor anterior
            if (nombre.equals("")) {
                a.setIdProfesor(Integer.parseInt(idProfesor));
                nombre = a.getNombre();
            }
            if (nomUser.equals("")) {
                a.setIdProfesor(Integer.parseInt(idProfesor));
                nomUser = a.getNomUser();
            }
            if (password.equals("")) {
                a.setIdProfesor(Integer.parseInt(idProfesor));
                password = a.getPassword();
            }
            // Creamos la consulta con el id del alumno ingresado
            String consulta = "update replace //profesores/profesor[id=" + idProfesor + "]"
                    + " with <profesor> <id>" + idProfesor + "</id>" + " <nombre>" + nombre + "</nombre>"
                    + " <nomUser>" + nomUser + "</nomUser>" + " <password>" + password + "</password>"
                    + " </profesor>";
            // Ejecutamos la consulta
            try {
                ejecutarConsultaUpdate(colecNotas, consulta);
                System.out.println();
                System.out.println("\033[32m¡Alumno modificado con éxito!\033[0m");
                System.out.println();
                pausa();
            } catch (XMLDBException e) {
                e.printStackTrace();
                System.out.println();
                pausa();
            }
        } else {
            // Si el alumno no existe, mostramos un mensaje de error en rojo
            System.out.println("\033[31m¡No existe ningún alumno con el id " + idProfesor + "!\033[0m");
            System.out.println();
            pausa();
        }

    }

    // Imprimir los datos alumnos en formato tabla
    public void imprimirProfesores(List<Profesor> listaProfesores) {
        // Imprimir encabezado de la tabla
        System.out.println("+-------+-----------------+-----------------+-----------------+");
        System.out.printf(
                "| \033[38;5;206m%-5s\033[0m | \033[38;5;206m%-15s\033[0m | \033[38;5;206m%-15s\033[0m | \033[38;5;206m%-15s\033[0m |%n",
                "ID", "NOMBRE", "NOMBRE USUARIO", "CONTRASEÑA");
        System.out.println("+-------+-----------------+-----------------+-----------------+");
        // Imprimir los datos de los alumnos
        for (Profesor p : listaProfesores) {
            System.out.printf(
                    "| \033[38;5;15m%-5s\033[0m | \033[38;5;15m%-15s\033[0m | \033[38;5;15m%-15s\033[0m | \033[38;5;15m%-15s\033[0m |%n",
                    p.getId(), p.getNombre(), p.getNomUser(), p.getPassword());

            System.out.println("+-------+-----------------+-----------------+-----------------+");

        }
    }

    public List<Modulo> listarModulosPorAlumno(String user2) throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        String consulta = "for $l in //alumnos/alumno where $l/nom_user='" + user2 + "' return $l";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator iterador = resultado.getIterator();
        List<Alumno> todosLosAlumnos = new ArrayList<>();
        while (iterador.hasMoreResources()) {
            XMLResource res = (XMLResource) iterador.nextResource();
            // Tenemos que leer el resultado como un DOM
            Node nodo = res.getContentAsDOM();
            // Leemos la lista de hijos que son tipo alunmo
            NodeList hijo = nodo.getChildNodes();
            // Leemos los hijos del alumno
            NodeList datosLibro = hijo.item(0).getChildNodes();
            Alumno a = leerDomAlumno(datosLibro);
            todosLosAlumnos.add(a);
        }
        List<Modulo> todosLosModulos = new ArrayList<>();
        for (Alumno a : todosLosAlumnos) {
            consulta = "for $l in //modulos/modulo where $l/id='" + a.getIdModulo() + "' return $l";
            resultado = ejecutarConsultaXQuery(colecNotas, consulta);
            iterador = resultado.getIterator();
            while (iterador.hasMoreResources()) {
                XMLResource res = (XMLResource) iterador.nextResource();
                // Tenemos que leer el resultado como un DOM
                Node nodo = res.getContentAsDOM();
                // Leemos la lista de hijos que son tipo alunmo
                NodeList hijo = nodo.getChildNodes();
                // Leemos los hijos del alumno
                NodeList datosLibro = hijo.item(0).getChildNodes();
                Modulo m = leerDomModulo(datosLibro);
                todosLosModulos.add(m);
            }
        }
        return todosLosModulos;
    }

    public Object listarNotasPorAlumno(String user2) throws XMLDBException {
        // Limpiamos la pantalla
        System.out.print("\033[H\033[2J");
        String consulta = "for $l in //alumnos/alumno where $l/nom_user='" + user2 + "' return $l";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator iterador = resultado.getIterator();
        List<Alumno> todosLosAlumnos = new ArrayList<>();
        while (iterador.hasMoreResources()) {
            XMLResource res = (XMLResource) iterador.nextResource();
            // Tenemos que leer el resultado como un DOM
            Node nodo = res.getContentAsDOM();
            // Leemos la lista de hijos que son tipo alunmo
            NodeList hijo = nodo.getChildNodes();
            // Leemos los hijos del alumno
            NodeList datosLibro = hijo.item(0).getChildNodes();
            Alumno a = leerDomNota(datosLibro);
            todosLosAlumnos.add(a);
        }
        List<Alumno> notasAlumno = new ArrayList<>();
        for (Alumno a : todosLosAlumnos) {
            consulta = "for $l in //alumnos/alumno where $l/id='" + a.getIdAlumno() + "' return $l";
            resultado = ejecutarConsultaXQuery(colecNotas, consulta);
            iterador = resultado.getIterator();
            while (iterador.hasMoreResources()) {
                XMLResource res = (XMLResource) iterador.nextResource();
                // Tenemos que leer el resultado como un DOM
                Node nodo = res.getContentAsDOM();
                // Leemos la lista de hijos que son tipo alunmo
                NodeList hijo = nodo.getChildNodes();
                // Leemos los hijos del alumno
                NodeList datosLibro = hijo.item(0).getChildNodes();
                Alumno n = leerDomNota(datosLibro);
                notasAlumno.add(n);
            }
        }
        return notasAlumno;
    }

    private Alumno leerDomNota(NodeList datosLibro) {

        Alumno n = new Alumno();
        for (int i = 0; i < datosLibro.getLength(); i++) {
            Node nodo = datosLibro.item(i);
            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                switch (nodo.getNodeName()) {
                    case "id":
                        n.setIdAlumno(nodo.getTextContent());
                        break;
                    case "modulo":
                        n.setIdModulo((nodo.getTextContent()));
                        break;
                    case "nota":
                        n.setNota(Double.parseDouble(nodo.getTextContent()));
                        break;
                }
            }
        }
        return n;
    }

    public void imprimirNotas(Object listarNotasPorAlumno) {

        List<Alumno> notas = (List<Alumno>) listarNotasPorAlumno;
        System.out.println("+-------+-----------------+-----------------+-----------------+");
        System.out.println("|  ID   |  ID MODULO      |  ID ALUMNO      |  NOTA           |");
        System.out.println("+-------+-----------------+-----------------+-----------------+");
        for (Alumno n : notas) {
            System.out.printf(
                    "| \033[38;5;15m%-5s\033[0m | \033[38;5;15m%-15s\033[0m | \033[38;5;15m%-15s\033[0m | \033[38;5;15m%-15s\033[0m |%n",
                    n.getIdAlumno(), n.getIdModulo(), n.getIdAlumno(), n.getNota());

            System.out.println("+-------+-----------------+-----------------+-----------------+");
        }
    }

    public void insertarProfesor(int id, String nombre, String user, String password) {
     
        // Comprobamos si existe el alumno
        if (!existeProfesor(id)) {
            // Creamos la consulta
            String consulta = "update insert <profesor><id>" + id + "</id><nombre>" + nombre
                    + "</nombre><nomUser>" + user + "</nomUser><password>" + password
                    + "</password></profesor> into //profesores";
            // Ejecutamos la consulta
            try {
                ejecutarConsultaUpdate(colecNotas, consulta);
            } catch (XMLDBException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("El profesor ya existe");
        }
    }

}