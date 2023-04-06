package resources;

import java.util.ArrayList;
import java.util.List;

public class Alumno {
    private Integer idAlumno;
    private String nombre;
    private String nomUser;
    private String password;
    private List<Notas> notas; // nueva línea

    public Alumno() {
        this.notas = new ArrayList<>(); // nueva línea
    }

    public Alumno(Integer idAlumno, String nombre, String nomUser, String password) {
        this.idAlumno = idAlumno;
        this.nombre = nombre;
        this.nomUser = nomUser;
        this.password = password;
        this.notas = new ArrayList<>(); // nueva línea
    }

    public Integer getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(Integer idAlumno) {
        this.idAlumno = idAlumno + 1;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNomUser() {
        return nomUser;
    }

    public void setNomUser(String nomUser) {
        this.nomUser = nomUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Notas> getNotas() { // nueva línea
        return notas;
    }

    public void setNotas(List<Notas> notas) { // nueva línea
        this.notas = notas;
    }
}
