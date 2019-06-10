package com.jgpindustries.calculadorareembolsos;

import java.io.Serializable;

public class Evento implements Serializable {

    private int id;
    private String nombre;

    public Evento(String nombre) {
        this.nombre = nombre;
    }

    public Evento(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
        return nombre;
    }
}
