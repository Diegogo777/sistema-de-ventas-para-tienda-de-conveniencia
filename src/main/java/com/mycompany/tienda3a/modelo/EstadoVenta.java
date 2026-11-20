package com.mycompany.tienda3a.modelo;

public enum EstadoVenta {
    EN_CURSO("En curso"),
    CONFIRMADA("Confirmada"),
    CANCELADA("Cancelada"),
    ANULADA("Anulada");

    private final String etiqueta;

    EstadoVenta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
