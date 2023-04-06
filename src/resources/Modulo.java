package resources;

import java.util.ArrayList;
import java.util.List;

public class Modulo {
    private Integer idModulo;
    private String nombre;
    private List<Notas> notas; // nueva línea

    public Modulo() {
        this.notas = new ArrayList<>(); // nueva línea
    }

    public Modulo(Integer idModulo, String nombre) {
        this.idModulo = idModulo;
        this.nombre = nombre;
        this.notas = new ArrayList<>(); // nueva línea
    }

    public Integer getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(Integer idModulo) {
        this.idModulo = idModulo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Notas> getNotas() { // nueva línea
        return notas;
    }

    public void setNotas(List<Notas> notas) { // nueva línea
        this.notas = notas;
    }
}