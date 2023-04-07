
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
import resources.Historial;
import resources.Modulo;
import resources.Profesor;

public class HistorialXND {

    private final Database database;
    private final String uri = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private final String user = "admin";
    private final String pass = "admin";
    private final String colecNotas = "/db/Notas";

    // Scanner para leer datos por teclado
    Scanner sc = new Scanner(System.in);

    public HistorialXND() throws ClassNotFoundException, InstantiationException, IllegalAccessException, XMLDBException {
        String driver = "org.exist.xmldb.DatabaseImpl";
        Class c1 = Class.forName(driver);
        database = (Database) c1.newInstance();
        DatabaseManager.registerDatabase(database);
    }

    public void cerrarConexion() throws XMLDBException {
        DatabaseManager.deregisterDatabase(database);
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

    // Función interna para ejecutar consultas XPath
    private ResourceSet ejecutarConsultaXPath(String coleccion, String consulta) throws XMLDBException {
        XQueryService servicio = prepararConsulta(coleccion);
        ResourceSet resultado = servicio.query(consulta);
        return resultado;
    }


    /*<historial>
	<evento>
		<id>1</id>
		<tipo>P</tipo>
		<user>furia</user>
		<detalle>Inicio de sesion Nick Furia 09-09-2009</detalle>
	</evento>
	<evento>
		<id>2</id>
		<tipo>A</tipo>
		<user>capitan</user>
		<detalle>Inicio de sesiñon Steve Rogers 09-09-2009</detalle>
	</evento>
	<evento>
		<id>3</id>
		<tipo>P</tipo>
		<user>maria</user>
		<detalle>Inicio de sesion Maria Hill 10-09-2009</detalle>
	</evento>
	<evento>
		<id>4</id>
		<tipo>N</tipo>
		<user>furia</user>
		<detalle>Nota M01 Steve Rogers 9.00</detalle>
	</evento>
	<evento>
		<id>6</id>
		<tipo>N</tipo>
		<user>furia</user>
		<detalle>Nota M03 Steve Rogers 7.50</detalle>
	</evento>
</historial> */

 public void insertarHistorial(String tipo, String user, String detalle) throws XMLDBException {

        String consulta = "update insert <evento><id>{count(/historial/evento)+1}</id><tipo>"+tipo+"</tipo><user>"+user+"</user><detalle>"+detalle+"</detalle></evento> into /historial";
        ejecutarConsultaUpdate(colecNotas, consulta);
    }

    public List<Historial> mostrarHistorial() throws XMLDBException {
        String consulta = "for $x in /historial/evento return $x";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator iterador = resultado.getIterator();
        List<Historial> todoElHistorial = new ArrayList<>();
        while (iterador.hasMoreResources()) {
            XMLResource res = (XMLResource) iterador.nextResource();
            // Tenemos que leer el resultado como un DOM
            Node nodo = res.getContentAsDOM();
            // Leemos la lista de hijos que son tipo Libro
            NodeList hijo = nodo.getChildNodes();
            // Leemos los hijos del Libro
            NodeList datosLibro = hijo.item(0).getChildNodes();
            Historial m = leerDomHistorial(datosLibro);
            todoElHistorial.add(m);
        }

        return todoElHistorial;
    }



    private Historial leerDomHistorial(NodeList datosLibro) {
        Historial m = new Historial();
        for (int i = 0; i < datosLibro.getLength(); i++) {
            Node n = datosLibro.item(i);
            if (n.getNodeName().equals("id")) {
                m.setId(n.getTextContent());
            } else if (n.getNodeName().equals("tipo")) {
                m.setTipo(n.getTextContent());
            } else if (n.getNodeName().equals("user")) {
                m.setUser(n.getTextContent());
            } else if (n.getNodeName().equals("detalle")) {
                m.setDetalle(n.getTextContent());
            }
        }
        return m;
    }

    public void mostrarHistorial(String tipo) throws XMLDBException {
        String consulta = "for $x in /historial/evento where $x/tipo='"+tipo+"' return $x";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator i = resultado.getIterator();
        Resource res = null;
        while (i.hasMoreResources()) {
            res = i.nextResource();
            System.out.println(res.getContent());
        }
    }

    public void mostrarHistorial(String tipo, String user) throws XMLDBException {
        String consulta = "for $x in /historial/evento where $x/tipo='"+tipo+"' and $x/user='"+user+"' return $x";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator i = resultado.getIterator();
        Resource res = null;
        while (i.hasMoreResources()) {
            res = i.nextResource();
            System.out.println(res.getContent());
        }
    }

    public void mostrarHistorial(String tipo, String user, String detalle) throws XMLDBException {
        String consulta = "for $x in /historial/evento where $x/tipo='"+tipo+"' and $x/user='"+user+"' and $x/detalle='"+detalle+"' return $x";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator i = resultado.getIterator();
        Resource res = null;
        while (i.hasMoreResources()) {
            res = i.nextResource();
            System.out.println(res.getContent());
        }
    }

    public void mostrarHistorial(String tipo, String user, String detalle, String id) throws XMLDBException {
        String consulta = "for $x in /historial/evento where $x/tipo='"+tipo+"' and $x/user='"+user+"' and $x/detalle='"+detalle+"' and $x/id='"+id+"' return $x";
        ResourceSet resultado = ejecutarConsultaXQuery(colecNotas, consulta);
        ResourceIterator i = resultado.getIterator();
        Resource res = null;
        while (i.hasMoreResources()) {
            res = i.nextResource();
            System.out.println(res.getContent());
        }
    }

    public void imprimirHistorial(List<Historial> listaModulos) {
        // Imprimir encabezado de la tabla id, tipo, user, detalle
        System.out.println("+---------+----------+------------+--------------------------------------------------------------+");
        System.out.printf("| %-7s | %-9s | %-9s | %-60s |%n", "ID", "TIPO", "USER", "DETALLE");
        System.out.println("+---------+----------+------------+--------------------------------------------------------------+");
        // Imprimir los datos de los módulos
        for (Historial m : listaModulos) {
            System.out.printf("| %-7s | %-9s | %-9s | %-60s |%n", m.getId(), m.getTipo(), m.getUser(), m.getDetalle());
        }
        System.out.println("+---------+----------+------------+--------------------------------------------------------------+");
    }

}
