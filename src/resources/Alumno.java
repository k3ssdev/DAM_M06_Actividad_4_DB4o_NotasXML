package resources;

import java.util.ArrayList;
import java.util.List;

public class Alumno {
    private String idAlumno;
    private String nombre;
    private String nomUser;
    private String password;
    private String idModulo;
    private Double nota;

    public Alumno() {

    }

    public Alumno(String idAlumno, String nombre, String nomUser, String password, String idModulo, Double nota) {
        this.idAlumno = idAlumno;
        this.nombre = nombre;
        this.nomUser = nomUser;
        this.password = password;
        this.idModulo = idModulo;
        this.nota = nota;
    }

    public Alumno(String idAlumno2, String nombre2, String nomUser2, String password2, double parseDouble,
            String idModulo2) {
    }

    public String getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(String idAlumno) {
        this.idAlumno = idAlumno;
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

    public String getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(String idModulo) {
        this.idModulo = idModulo;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    @Override
    public String toString() {
        return "Alumno{" +
                "idAlumno='" + idAlumno + '\'' +
                ", nombre='" + nombre + '\'' +
                ", usuario='" + nomUser + '\'' +
                ", password='" + password + '\'' +
                ", idModulo='" + idModulo + '\'' +
                ", nota=" + nota +
                '}';

    }

}
