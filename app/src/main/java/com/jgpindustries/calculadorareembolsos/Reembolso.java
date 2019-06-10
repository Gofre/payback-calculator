package com.jgpindustries.calculadorareembolsos;

import java.text.NumberFormat;

public class Reembolso {

    private double cantidad; // Cantidad de dinero que el pagador debe abonar al beneficiario
    private Participante beneficiario; // El que recibe el dinero
    private Participante pagador; // El que debe dinero al beneficiario
    private NumberFormat nf = NumberFormat.getCurrencyInstance();

    public Reembolso() {}

    public Reembolso(double cantidad, Participante beneficiario, Participante pagador) {
        this.cantidad = cantidad;
        this.beneficiario = beneficiario;
        this.pagador = pagador;
    }

    public Participante getPagador() {
        return pagador;
    }

    public void setPagador(Participante pagador) {
        this.pagador = pagador;
    }

    public Participante getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(Participante beneficiario) {
        this.beneficiario = beneficiario;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return pagador.getNombre() + ": " + nf.format(cantidad) + "   ->   " + beneficiario.getNombre();
    }

    // Añadir nueva cantidad de dinero que el pagador debe al beneficiario
    public void addCantidad(double cantidad) {
        this.cantidad += cantidad;
    }
}
