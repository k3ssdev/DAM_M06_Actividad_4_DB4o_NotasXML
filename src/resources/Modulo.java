package resources;

import java.util.ArrayList;
import java.util.List;

public class Modulo {
    private String idModulo;
    private String nombre;
    private List<Modulo> modulos = new ArrayList<>();



    public Modulo(String idModulo, String nombre) {
        this.idModulo = idModulo;
        this.nombre = nombre;
    }

    public Modulo() {
    }

    public String getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(String string) {
        this.idModulo = string;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        // tabla de modulos
        return "Modulo{" +
                "idModulo='" + idModulo + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}