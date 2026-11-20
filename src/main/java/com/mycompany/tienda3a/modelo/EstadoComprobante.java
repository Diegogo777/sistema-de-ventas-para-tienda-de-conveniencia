package com.mycompany.tienda3a.modelo;

public enum EstadoComprobante {
    PENDIENTE("Pendiente"),
    ACEPTADO("Aceptado"),
    RECHAZADO("Rechazado"),
    ANULADO("Anulado");

    private final String etiqueta;

    EstadoComprobante(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
