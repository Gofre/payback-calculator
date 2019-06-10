package com.jgpindustries.calculadorareembolsos;

import java.io.Serializable;
import java.util.ArrayList;

public class Gasto implements Serializable {

    private int id;
    private String nombre;
    private double coste;
    private Participante comprador;
    private ArrayList<Participante> consumidores;

    public Gasto(String nombre, double coste, Participante comprador, ArrayList<Participante> consumidores) {
        this.nombre = nombre;
        this.coste = coste;
        this.comprador = comprador;
        this.consumidores = consumidores;
    }

    public Gasto(int id, String nombre, double coste, Participante comprador, ArrayList<Participante> consumidores) {
        this.id = id;
        this.nombre = nombre;
        this.coste = coste;
        this.comprador = comprador;
        this.consumidores = consumidores;
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

    public double getCoste() {
        return coste;
    }

    public Participante getComprador() {
        return comprador;
    }

    public ArrayList<Participante> getConsumidores() {
        return consumidores;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    public void setComprador(Participante comprador) {
        this.comprador = comprador;
    }

    public void setConsumidores(ArrayList<Participante> consumidores) {
        this.consumidores = consumidores;
    }

    @Override
    public String toString() {
        return nombre;
    }

}
