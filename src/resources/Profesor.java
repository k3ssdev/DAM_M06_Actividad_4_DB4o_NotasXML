package resources;

public class Profesor {

    private Integer idProfesor;
    private String nombre;
    private String nomUser;
    private String password;


    public Profesor() {

    }

    public Profesor(Integer idProfesor, String nombre, String nomUser, String password) {
        this.idProfesor = idProfesor;
        this.nombre = nombre;
        this.nomUser = nomUser;
        this.password = password;
    }

    public Integer getId() {
        return idProfesor;
    }

    public void setIdProfesor(Integer idProfesor) {
        this.idProfesor = idProfesor;
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

    @Override
    public String toString() {
        return "Profesor{" +
                "idAlumno='" + idProfesor + '\'' +
                ", nombre='" + nombre + '\'' +
                ", usuario='" + nomUser + '\'' +
                ", password='" + password + '\'' +
                '}';

    }

}

