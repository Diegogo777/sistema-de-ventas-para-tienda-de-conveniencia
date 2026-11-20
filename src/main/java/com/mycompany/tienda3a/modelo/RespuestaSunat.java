package com.mycompany.tienda3a.modelo;

public enum RespuestaSunat {
    ACEPTADO("Aceptado"),
    RECHAZADO("Rechazado"),
    SIN_RESPUESTA("Sin respuesta");

    private final String etiqueta;

    RespuestaSunat(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
