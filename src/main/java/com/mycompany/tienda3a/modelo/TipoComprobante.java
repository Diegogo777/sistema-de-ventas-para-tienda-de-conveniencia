package com.mycompany.tienda3a.modelo;

public enum TipoComprobante {
    VENTA_SIMPLE("Venta simple"),
    BOLETA("Boleta"),
    FACTURA("Factura");

    private final String etiqueta;

    TipoComprobante(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
