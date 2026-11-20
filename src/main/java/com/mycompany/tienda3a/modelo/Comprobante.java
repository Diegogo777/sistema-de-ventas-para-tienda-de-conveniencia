package com.mycompany.tienda3a.modelo;

import com.mycompany.tienda3a.config.ConfiguracionVenta;

public abstract class Comprobante implements Imprimible {

    protected int id;
    protected Venta venta;
    protected String serie;
    protected int correlativo;
    protected EstadoComprobante estado;
    protected int intentosEnvio;
    protected String observacionRevision;

    public Comprobante(int id, Venta venta, String serie, int correlativo) {
        this.id = id;
        this.venta = venta;
        this.serie = serie;
        this.correlativo = correlativo;
        this.estado = EstadoComprobante.PENDIENTE;
        this.intentosEnvio = 0;
    }

    public double getSubtotal() { return venta.getSubtotal(); }
    public double getIgv() { return venta.getIgv(); }
    public double getTotal() { return venta.getTotal(); }

    public void registrarIntentoEnvio(boolean aceptado, String mensajeRechazo) {
        this.intentosEnvio++;
        if (aceptado) {
            this.estado = EstadoComprobante.ACEPTADO;
        } else if (intentosEnvio >= ConfiguracionVenta.REINTENTOS_SUNAT) {
            this.estado = EstadoComprobante.RECHAZADO;
            this.observacionRevision = mensajeRechazo;
        }
    }

    public void anular() { this.estado = EstadoComprobante.ANULADO; }

    public String getNumeroCompleto() { return serie + "-" + String.format("%06d", correlativo); }
    public EstadoComprobante getEstado() { return estado; }

    protected String encabezado(String tituloDocumento) {
        return "=== " + tituloDocumento + " " + getNumeroCompleto() + " ===\n"
             + String.format("Subtotal: S/ %.2f%n", getSubtotal())
             + String.format("IGV (%.0f%%): S/ %.2f%n", ConfiguracionVenta.TASA_IGV * 100, getIgv())
             + String.format("TOTAL: S/ %.2f%n", getTotal());
    }
}
