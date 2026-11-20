package com.mycompany.tienda3a.modelo;

public enum TipoDocumento {
    DNI("DNI"),
    RUC("RUC");

    private final String etiqueta;

    TipoDocumento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
