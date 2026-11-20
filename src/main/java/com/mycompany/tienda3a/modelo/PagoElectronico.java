package com.mycompany.tienda3a.modelo;

public interface PagoElectronico {
    String getReferenciaDigital();

    void registrarReferenciaReversion(String referencia);
}
