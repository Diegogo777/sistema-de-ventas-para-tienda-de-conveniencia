package com.mycompany.tienda3a.modelo;

public enum TipoMovimiento {
    SALIDA_VENTA("Salida por venta"),
    REINGRESO_ANULACION("Reingreso por anulación");

    private final String etiqueta;

    TipoMovimiento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
