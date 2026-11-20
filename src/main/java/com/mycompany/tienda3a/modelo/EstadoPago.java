package com.mycompany.tienda3a.modelo;

public enum EstadoPago {
    REGISTRADO("Registrado"),
    REVERTIDO("Revertido");

    private final String etiqueta;

    EstadoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
