package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

public class Factura extends Comprobante {
    public Factura(int idComprobante, Venta venta, String serie, int correlativo, LocalDateTime fechaEmision) {
        super(idComprobante, venta, serie, correlativo, fechaEmision);
    }

    @Override
    public TipoComprobante getTipo() {
        return TipoComprobante.FACTURA;
    }

    @Override
    public boolean validarDatos() {
        Cliente c = venta.getCliente();
        return c != null && c.getTipoDocumento() == TipoDocumento.RUC && !c.getNombreRazonSocial().isBlank();
    }

    @Override
    public String generarImpresion() {
        Cliente c = venta.getCliente();
        String dir = c.getDireccion() == null || c.getDireccion().isBlank() ? "" : "Dir.   : " + c.getDireccion() + "\n";
        return armarTicket("FACTURA ELECTRONICA",
                "Razón social: " + c.getNombreRazonSocial() + "\nRUC    : " + c.getNumeroDocumento() + "\n" + dir);
    }
}
