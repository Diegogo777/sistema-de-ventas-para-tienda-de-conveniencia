package com.mycompany.tienda3a.modelo;

import com.mycompany.tienda3a.config.Configuracion;
import java.time.LocalDateTime;

public class VentaSimpleComprobante extends Comprobante {
    public VentaSimpleComprobante(int idComprobante, Venta venta, String serie, int correlativo,
                                  LocalDateTime fechaEmision) {
        super(idComprobante, venta, serie, correlativo, fechaEmision);
    }

    @Override
    public TipoComprobante getTipo() {
        return TipoComprobante.VENTA_SIMPLE;
    }

    @Override
    public boolean validarDatos() {
        return venta.getTotal() <= Configuracion.getMontoMaximoVentaSimple();
    }

    @Override
    public String generarImpresion() {
        return armarTicket("NOTA DE VENTA SIMPLE", "Cliente: (sin datos)\n");
    }
}
