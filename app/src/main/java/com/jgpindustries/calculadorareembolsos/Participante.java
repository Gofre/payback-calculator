package com.jgpindustries.calculadorareembolsos;

import java.io.Serializable;

public class Participante implements Serializable {

    private int id;
    private String nombre;

    public Participante(String nombre) {
        this.nombre = nombre;
    }

    public Participante(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Participante () {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return /*id + " - " + */nombre;
    }

}
