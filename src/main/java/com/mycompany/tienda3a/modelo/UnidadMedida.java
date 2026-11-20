package com.mycompany.tienda3a.modelo;

public enum UnidadMedida {
    UNIDAD("Unidad"),
    KG("Kilogramo");

    private final String etiqueta;

    UnidadMedida(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
