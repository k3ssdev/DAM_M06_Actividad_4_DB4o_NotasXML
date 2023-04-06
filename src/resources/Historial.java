package resources;

import java.util.List;

public class Historial {

    private Integer id;
    private String tipo;
    private int user;
    private String detalle;
    private String tiempoStamp;

    public String getTiempoStamp() {
        return tiempoStamp;
    }

    public void setTiempoStamp(String tiempoStamp) {
        this.tiempoStamp = tiempoStamp;
    }

    private List<Profesor> profesores;
    private List<Alumno> alumnos;

    public Historial() {
    }

    public Historial(Integer id, String tipo, int user, String detalle, List<Profesor> profesores,
            List<Alumno> alumnos) {
        this.id = id;
        this.tipo = tipo;
        this.user = user;
        this.detalle = detalle;
        this.profesores = profesores;
        this.alumnos = alumnos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public List<Profesor> getProfesores() {
        return profesores;
    }

    public void setProfesores(List<Profesor> profesores) {
        this.profesores = profesores;
    }

    public List<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(List<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    @Override
    public String toString() {
        return "Historial{" + "id=" + id + ", tipo=" + tipo + ", user=" + user + ", detalle=" + detalle
                + ", profesores=" + profesores + ", alumnos=" + alumnos + '}';
    }

}