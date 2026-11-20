package com.mycompany.tienda3a.modelo;

public enum ResultadoOperacion {
    EXITO("Éxito"),
    FALLIDO("Fallido"),
    RECHAZADO("Rechazado");

    private final String etiqueta;

    ResultadoOperacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
