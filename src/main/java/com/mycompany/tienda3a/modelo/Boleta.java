package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

public class Boleta extends Comprobante {
    public Boleta(int idComprobante, Venta venta, String serie, int correlativo, LocalDateTime fechaEmision) {
        super(idComprobante, venta, serie, correlativo, fechaEmision);
    }

    @Override
    public TipoComprobante getTipo() {
        return TipoComprobante.BOLETA;
    }

    @Override
    public boolean validarDatos() {
        Cliente c = venta.getCliente();
        return c != null && c.getTipoDocumento() == TipoDocumento.DNI;
    }

    @Override
    public String generarImpresion() {
        Cliente c = venta.getCliente();
        return armarTicket("BOLETA DE VENTA ELECTRONICA",
                "Cliente: " + c.getNombreRazonSocial() + "\nDNI    : " + c.getNumeroDocumento() + "\n");
    }
}
