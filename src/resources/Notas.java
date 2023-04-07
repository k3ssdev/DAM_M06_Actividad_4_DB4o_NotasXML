package resources;

import java.util.List;

public class Notas {
    private Integer idNotas;
    private Integer idAlumno;
    private Integer idModulo;
    private Double nota;
    private Alumno alumno;
    private Modulo modulo;

    public Notas() {
    }

    public Notas(Integer idNotas, Integer idAlumno, Integer idModulo, Double nota, Alumno alumno, Modulo modulo) {
        this.idNotas = idNotas;
        this.idAlumno = idAlumno;
        this.idModulo = idModulo;
        this.nota = nota;
        this.alumno = alumno;
        this.modulo = modulo;

        // Agregar la nota al alumno y al módulo correspondientes
        //alumno.getNotas().add(this);
        //modulo.getNotas().add(this);
    }

    public Notas(int codigoNota, int codigoAlumno, int codigoModulo, double nota2) {
        this.idNotas = codigoNota;
        this.idAlumno = codigoAlumno;
        this.idModulo = codigoModulo;
        this.nota = nota2;
        
    }

    public Integer getIdNotas() {
        return idNotas;
    }

    public void setIdNotas(Integer idNotas) {
        this.idNotas = idNotas;
    }

    public Integer getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(Integer idAlumno) {
        this.idAlumno = idAlumno;
    }

    public Integer getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(Integer idModulo) {
        this.idModulo = idModulo;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    @Override
    public String toString() {
        return "Notas{" + "idNotas=" + idNotas + ", idAlumno=" + idAlumno + ", idModulo=" + idModulo + ", nota=" + nota
                + ", idAlumno=" + alumno.getIdAlumno() + ", idModulo=" + modulo.getIdModulo() + '}';
    }
}