package resources;

import java.util.List;

public class Historial {

    private String id;
    private String tipo;
    private String user;
    private String detalle;

    public Historial() {

    }

    public Historial(String id, String tipo, String user, String detalle) {
        this.id = id;
        this.tipo = tipo;
        this.user = user;
        this.detalle = detalle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }


    @Override
    public String toString() {
        return "Historial{" +
                "id='" + id + '\'' +
                ", tipo='" + tipo + '\'' +
                ", user='" + user + '\'' +
                ", detalle='" + detalle + '\'' +
                '}';

    }

}
    
