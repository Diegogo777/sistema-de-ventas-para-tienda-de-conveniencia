package com.mycompany.tienda3a.modelo;

public enum MedioPago {
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta"),
    BILLETERA("Billetera digital");

    private final String etiqueta;

    MedioPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
