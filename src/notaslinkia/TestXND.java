package notaslinkia;

import java.io.Console;
/**
 *
 * @author alber
 */
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmldb.api.base.XMLDBException;

import resources.Profesor;

public class TestXND {

    public static void main(String[] args)
            throws XMLDBException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        // Desactivar mensajes de registro de Hibernate
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);

        // Crear el gestor de sesiones

        // Crear un objeto Scanner para leer de la consola
        Scanner sc = new Scanner(System.in);

        // Crear un objeto Timestamp para obtener la fecha y hora actual
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Crear un objeto Console para leer la contraseña
        Console console = System.console();

        // Crear objetos NotasXND e HistorialXND para acceder a la bbdd
        NotasXND gestor = new NotasXND();
        HistorialXND historial = new HistorialXND();

        // Crear un objeto Profesor para almacenar el usuario logueado
        Profesor profesor = null;

        // Fecha y hora actual
        String fecha = dateFormat.format(timestamp);

        // Bucle infinito
        while (true) {

            // Limpiar pantalla
            System.out.print("\033[H\033[2J");

            // Banner en color verde
            System.out.println("\033[32m");
            System.out.print(
                    ".d88b. w       w                              8                           w   w                                  8                 w          \r");

            try (Scanner scanner = new Scanner(System.in)) {

                String opcion = "";
                String user = "";
                String pass = "";

                // Bucle infinito
                while (true) {
                    // Limpiar pantalla
                    System.out.print("\033[H\033[2J");

                    // Banner en color verde
                    System.out.println("\033[32m");
                    System.out.print(
                            ".d88b. w       w                              8                           w   w                                  8                 w          \n");
                    System.out.print(
                            "YPwww. w d88b w8ww .d88b 8d8b.d8b. .d88    .d88 .d88b    .d88 .d88b d88b w8ww w .d8b. 8d8b.    .d88 .d8b .d88 .d88 .d88b 8d8b.d8b. w .d8b .d88\n");
                    System.out.print(
                            "    d8 8 `Yb.  8   8.dP' 8P Y8P Y8 8  8    8  8 8.dP'    8  8 8.dP' `Yb.  8   8 8' .8 8P Y8    8  8 8    8  8 8  8 8.dP' 8P Y8P Y8 8 8    8  8\n");
                    System.out.print(
                            "`Y88P' 8 Y88P  Y8P `Y88P 8   8   8 `Y88    `Y88 `Y88P    `Y88 `Y88P Y88P  Y8P 8 `Y8P' 8   8    `Y88 `Y8P `Y88 `Y88 `Y88P 8   8   8 8 `Y8P `Y88\n");
                    System.out.print(
                            "                                                         wwdP                                                                                 \n");
                    System.out.println("\033[0m");
                    System.out.print("\033[35m");
                    System.out.println("Alberto Pérez del Río");
                    System.out.println("DAM - M06");
                    System.out.println("2023");

                    System.out.println();
                    System.out.println();

                    System.out.println("\033[33m ┌──────────────────┐\033[0m");
                    System.out.println("\033[33m │  MENU PRINCIPAL  │\033[0m");
                    System.out.println("\033[33m └──────────────────┘\033[0m");
                    System.out.println("\033[36m ╔══════════════════╗\033[0m");
                    System.out.println("\033[36m ║ 1. Menú profesor ║\033[0m");
                    System.out.println("\033[36m ║==================║\033[0m");
                    System.out.println("\033[36m ║ 2. Menú alumno   ║\033[0m");
                    System.out.println("\033[36m ╚══════════════════╝\033[0m");
                    System.out.println("\n\033[35m ╔══════════════════╗\033[0m");
                    System.out.println("\033[35m ║ 3. Menú admin    ║\033[0m");
                    System.out.println("\033[35m ╚══════════════════╝\033[0m");
                    System.out.println("\n\033[31m ╔═══════════════════╗\033[0m");
                    System.out.println("\033[31m ║ 0. Salir          ║\033[0m");
                    System.out.println("\033[31m ╚═══════════════════╝\033[0m");

                    System.out.print("\n\033[32m Selecciona una opción: \033[0m");

                    opcion = sc.nextLine();

                    switch (opcion) {

                        case "1":
                            // Limpiar pantalla
                            System.out.print("\033[H\033[2J");
                            System.out.print("\033[32m");
                            System.out.print("Ingrese su usuario: ");
                            System.out.print("\033[0m");
                            user = sc.nextLine();
                            System.out.print("\033[32m");
                            char[] password = console.readPassword("Ingresa la contraseña: ");
                            pass = new String(password);
                            System.out.print("\033[0m");

                            // Comprobar si el usuario existe
                            if (gestor.comprobarProfesor(user, pass)) {

                                historial.insertarHistorial("P", user, "Inicio de sesión " + fecha);

                                // Bucle para mostrar el menú de profesor
                                Boolean menuProfesor = true;

                                while (menuProfesor) {
                                    // Limpiar pantalla
                                    System.out.print("\033[H\033[2J");
                                    System.out.print("\033[33m");
                                    System.out.println(" Menú de profesor:\n");
                                    System.out.print("\033[0m");
                                    System.out.println(" 1. Insertar módulo");
                                    System.out.println(" 2. Listar TODOS los módulos");
                                    System.out.println(" 3. Eliminar módulo");
                                    System.out.println(" 4. Listar TODOS los alumnos");
                                    System.out.println(" 5. Modificar alumno");
                                    System.out.println(" 6. Insertar alumno");
                                    System.out.println(" 7. Eliminar alumno");
                                    System.out.println(" 8. Listar alumnos de un módulo");
                                    System.out.print("\033[31m");
                                    System.out.println("\n 0. Salir");
                                    System.out.print("\033[0m");

                                    System.out.print("\n\033[32m Selecciona una opción: \033[0m");
                                    int opcionProfesor = sc.nextInt();
                                    sc.nextLine();

                                    switch (opcionProfesor) {
                                        case 1:
                                            // Llamar a método para insertar módulo
                                            gestor.insertarModulo();
                                            historial.insertarHistorial("M", user, "Modulo insertado " + fecha);
                                            break;
                                        case 2:
                                            // Limpiar pantalla
                                            System.out.print("\033[H\033[2J");
                                            // Llamar a método para listar todos los módulos
                                            gestor.imprimirModulos(gestor.listarModulos());
                                            System.out.println();
                                            gestor.pausa();
                                            break;
                                        case 3:
                                            // Llamar a método para eliminar módulo
                                            gestor.eliminarModulo();
                                            historial.insertarHistorial("M", user, "Modulo eliminado " + fecha);
                                            break;
                                        case 4:
                                            // Limpiar pantalla
                                            System.out.print("\033[H\033[2J");
                                            // Llamar a método para listar todos los alumnos
                                            gestor.imprimirAlumnos(gestor.listarAlumnos());
                                            System.out.println();
                                            gestor.pausa();
                                            break;
                                        case 5:
                                            // Llamar a método para insertar alumno
                                            gestor.modificarAlumno();
                                            historial.insertarHistorial("P", user, "Alumno modificado " + fecha);
                                            break;
                                        case 6:
                                            // Llamar a método para insertar alumno
                                            gestor.insertarAlumno();
                                            historial.insertarHistorial("P", user, "Alumno insertado " + fecha  );
                                            break;
                                        case 7:
                                            // Llamar a método para eliminar alumnos por módulo
                                            gestor.eliminarAlumno();
                                            historial.insertarHistorial("P", user, "Alumno eliminado " + fecha);
                                            break;
                                        case 8:
                                            // Llamar a método para listar alumnos por módulo
                                            gestor.imprimirAlumnos(gestor.listarAlumnosPorModulo());
                                            gestor.pausa();
                                            break;
                                        case 0:
                                            System.out.print("\033[H\033[2J");
                                            System.out.println("Saliendo...");
                                            menuProfesor = false;
                                            gestor.pausa();
                                            break;
                                        default:
                                            System.out.println("Opción no válida.");
                                            break;
                                    }

                                }
                            } else {
                                // Limpiar pantalla
                                System.out.print("\033[H\033[2J");
                                // Mensaje de error en rojo
                                System.out.print("\033[31m");
                                System.out.println("Usuario o contraseña incorrectos.");
                                System.out.println("\033[0m");
                                historial.insertarHistorial("P", user, "Contraseña incorrecta" + fecha);
                                gestor.pausa();
                            }

                            break;

                        case "2":

                            // Limpiar pantalla
                            System.out.print("\033[H\033[2J");
                            System.out.print("\033[32m");
                            System.out.print("Ingrese su usuario: ");
                            System.out.print("\033[0m");
                            user = sc.nextLine();
                            System.out.print("\033[32m");
                            char[] password2 = console.readPassword("Ingresa la contraseña: ");
                            pass = new String(password2);
                            System.out.print("\033[0m");

                            // Comprobar usuario y contraseña

                            if (gestor.comprobarAlumno(user, pass)) {

                                historial.insertarHistorial("A", user, "Inicio de sesión " + fecha);
                                // gestorHistorial.insertarHistorial(user, "A", "Inicio de sesión alumno");

                                // Bucle para mostrar el menú de alumno
                                Boolean menuAlumno = true;

                                while (menuAlumno) {

                                    // Limpiar pantalla
                                    System.out.print("\033[H\033[2J");
                                    System.out.print("\033[33m");
                                    System.out.println(" Menú de alumno:\n");
                                    System.out.print("\033[0m");
                                    System.out.println(" 1. Listar módulos");
                                    System.out.println(" 2. Listar notas");
                                    System.out.print("\033[31m");
                                    System.out.println("\n 0. Salir");
                                    System.out.print("\033[0m");

                                    System.out.print("\n\033[32m Selecciona una opción: \033[0m");
                                    int opcionAlumno = sc.nextInt();
                                    sc.nextLine();

                                    switch (opcionAlumno) {

                                        case 1:
                                            // Llamar a método para listar módulos
                                            gestor.imprimirModulos(gestor.listarModulosPorAlumno(user));
                                            System.out.println();
                                            gestor.pausa();
                                            break;
                                        case 2:
                                            // Llamar a método para listar notas
                                            gestor.imprimirNotas(gestor.listarNotasPorAlumno(user));
                                            System.out.println();
                                            gestor.pausa();
                                            break;
                                        case 0:
                                            System.out.print("\033[H\033[2J");
                                            System.out.println("Saliendo...");
                                            historial.insertarHistorial("A", user, "Cierre de sesión " + fecha);
                                            menuAlumno = false;
                                            gestor.pausa();
                                            break;
                                        default:
                                            System.out.println("Opción no válida.");
                                            break;
                                    }

                                }
                            } else {
                                // Limpiar pantalla
                                System.out.print("\033[H\033[2J");
                                // Mensaje de error en rojo
                                System.out.print("\033[31m");
                                System.out.println("Usuario o contraseña incorrectos.");
                                System.out.println("\033[0m");
                                historial.insertarHistorial("A", user, "Contraseña incorrecta" + fecha);
                                gestor.pausa();
                            }

                            break;
                        case "3":
                            Boolean menuAdmin = true;

                            // Limpiar pantalla
                            System.out.print("\033[H\033[2J");
                            System.out.print("\033[32m");
                            System.out.print("Ingrese su usuario: ");
                            System.out.print("\033[0m");
                            user = sc.nextLine();
                            System.out.print("\033[32m");
                            char[] password3 = console.readPassword("Ingresa la contraseña: ");
                            pass = new String(password3);
                            System.out.print("\033[0m");

                            // Comprobar si el usuario existe
                            if (gestor.comprobarProfesor(user, pass)) {

                                historial.insertarHistorial("P", user, "Inicio de sesión " + fecha);
                                // administrador");

                                while (menuAdmin) {
                                    System.out.print("\033[H\033[2J");
                                    System.out.print("\033[33m");
                                    System.out.println("Menú de administrador:");
                                    System.out.println("\033[0m");
                                    System.out.println("1. Listar tabla historial");
                                    System.out.println("2. Insertar módulo");
                                    System.out.println("3. Listar TODOS los módulos");
                                    System.out.println("4. Eliminar módulo");
                                    System.out.println("5. Insertar alumno");
                                    System.out.println("6. Listar TODOS los alumnos");
                                    System.out.println("7. Listar alumnos por módulo");
                                    System.out.println("8. Eliminar alumno");
                                    System.out.println("9. Listar tabla profesores");
                                    System.out.println("10. Insertar profesor");
                                    System.out.println("11. Modificar profesor");
                                    System.out.println("12. Eliminar profesor");
                                    System.out.print("\033[31m");
                                    System.out.println("\n0. Salir");

                                    System.out.print("\n\033[32m Selecciona una opción: \033[0m");
                                    int opcionAdmin = sc.nextInt();

                                    switch (opcionAdmin) {
                                        case 1:
                                            // Llamar a método para listar historial
                                            System.out.print("\033[H\033[2J");
                                            historial.imprimirHistorial(historial.mostrarHistorial());
                                            gestor.pausa();
                                            break;
                                        case 2:
                                            // Llamar a método para insertar módulo
                                            System.out.print("\033[H\033[2J");
                                            gestor.insertarModulo();
                                            historial.insertarHistorial("P", user, "Insertar módulo " + fecha);
                                            break;
                                        case 3:
                                            // Limpiar pantalla
                                            System.out.print("\033[H\033[2J");
                                            // Llamar a método para listar todos los módulos
                                            gestor.imprimirModulos(gestor.listarModulos());
                                            System.out.println();
                                            gestor.pausa();
                                            break;
                                        case 4:
                                            // Llamar a método para eliminar módulo
                                            System.out.print("\033[H\033[2J");
                                            gestor.eliminarModulo();
                                            historial.insertarHistorial("P", user, "Eliminar módulo " + fecha);
                                            break;
                                        case 5:
                                            // Llamar a método para insertar alumno
                                            System.out.print("\033[H\033[2J");
                                            gestor.insertarAlumno();
                                            historial.insertarHistorial("P", user, "Insertar alumno " + fecha);
                                            break;
                                        case 6:
                                            // Llamar a método para listar todos los alumnos
                                            System.out.print("\033[H\033[2J");
                                            gestor.imprimirAlumnos(gestor.listarAlumnos());
                                            gestor.pausa();
                                            break;
                                        case 7:
                                            // Llamar a método para listar alumnos por módulo
                                            gestor.imprimirAlumnos(gestor.listarAlumnosPorModulo());
                                            gestor.pausa();
                                            break;
                                        case 8:
                                            // Llamar a método para eliminar alumno
                                            System.out.print("\033[H\033[2J");
                                            gestor.eliminarAlumno();
                                            historial.insertarHistorial("P", user, "Eliminar alumno " + fecha);
                                            break;
                                        case 9:
                                            // Llamar a método para listar tabla profesores
                                            System.out.print("\033[H\033[2J");
                                            gestor.imprimirProfesores(gestor.listarProfesores());
                                            System.out.println();
                                            gestor.pausa();
                                            break;
                                        case 10:
                                            // Llamar a metodo para insertar profesor
                                            gestor.insertarProfesor();
                                            historial.insertarHistorial("P", user, "Insertar profesor " + fecha);
                                            break;
                                        case 11:
                                            // Llamar a método para modificar profesor
                                            System.out.print("\033[H\033[2J");
                                            gestor.modificarProfesor();
                                            historial.insertarHistorial("P", user, "Modificar profesor " + fecha);
                                            break;
                                        case 12:
                                            // Llamar a método para eliminar profesor
                                            System.out.print("\033[H\033[2J");
                                            gestor.eliminarProfesor();
                                            historial.insertarHistorial("P", user, "Eliminar profesor " + fecha);
                                            break;
                                        case 0:
                                            System.out.print("\033[H\033[2J");
                                            System.out.println("Saliendo...");
                                            historial.insertarHistorial("P", user, "Cierre de sesión " + fecha);
                                            gestor.pausa();
                                            menuAdmin = false;
                                            break;
                                        default:
                                            System.out.print("\033[H\033[2J");
                                            System.out.println("Opción no válida.");
                                            gestor.pausa();
                                            break;
                                    }
                                }
                            } else {
                                // Limpiar pantalla
                                System.out.print("\033[H\033[2J");
                                // Mensaje de error en rojo
                                System.out.print("\033[31m");
                                System.out.println("Usuario o contraseña incorrectos.");
                                System.out.println("\033[0m");
                                historial.insertarHistorial("P", user,
                                        "Contraseña incorrecta administracion " + fecha);
                                gestor.pausa();
                            }
                            break;
                        case "0":
                            System.out.println("Hasta luego.");
                            sc.close();
                            gestor.cerrarConexion();
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Opción no válida.");
                            break;
                    }
                }
            }
        }
    }
}