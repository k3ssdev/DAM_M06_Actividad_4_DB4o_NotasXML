
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
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XQueryService;

import resources.Modulo;
import resources.Profesor;

public class NotasXND {

    private final Database database;
    private final String uri = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private final String user = "admin";
    private final String pass = "Oct4rin0";
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

    public void insertarModulo() {
        // Creamos un objeto de tipo Modulo y pedimos datos por teclado
        Modulo miModulo = new Modulo();
        System.out.println("Introduzca el ID del módulo:");
        miModulo.setIdModulo(sc.nextInt());
        sc.nextLine();
        System.out.println("Introduzca el nombre del módulo:");
        miModulo.setNombre(sc.nextLine());
        // Comprobamos que no existe un módulo con ese ID
        if (!existeModulo(miModulo.getIdModulo())) {
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
            System.out.println("Ya existe un módulo con ese ID.");
        }
    }

    private boolean existeModulo(Integer idModulo) {
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


    public void listarModulos() {
        try {
            // Creamos la consulta
            String consulta = "for $t in //modulos/modulo return $t";
            // Ejecutamos la consulta
            ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
            // Recorremos el resultado
            ResourceIterator iterador = resultado.getIterator();
            while (iterador.hasMoreResources()) {
                XMLResource recurso = (XMLResource) iterador.nextResource();
                Node nodo = recurso.getContentAsDOM();
                NodeList listaNodos = nodo.getChildNodes();
                Integer idModulo = Integer.parseInt(listaNodos.item(0).getTextContent());
                String nombre = listaNodos.item(1).getTextContent();
                Modulo miModulo = new Modulo(idModulo, nombre);
                System.out.println(miModulo);
            }
        } catch (XMLDBException e) {
            e.printStackTrace();
        }
    }

    public void eliminarModulo() {
    }

    public void insertarAlumno() {
    }

    public void listarAlumnos() {
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
