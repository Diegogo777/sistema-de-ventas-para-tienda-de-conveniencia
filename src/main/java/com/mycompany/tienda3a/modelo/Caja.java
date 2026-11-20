package com.mycompany.tienda3a.modelo;

public class Caja {
    private int idCaja;
    private String codigo;
    private String serieBoleta;
    private String serieFactura;
    private boolean activo;

    public Caja(int idCaja, String codigo, String serieBoleta, String serieFactura) {
        this.idCaja = idCaja;
        this.codigo = codigo;
        this.serieBoleta = serieBoleta;
        this.serieFactura = serieFactura;
        this.activo = true;
    }

    public String obtenerSerie(TipoComprobante tipo) {
        return tipo == TipoComprobante.FACTURA ? serieFactura : serieBoleta;
    }

    public int getIdCaja() { return idCaja; }
    public void setIdCaja(int idCaja) { this.idCaja = idCaja; }
    public String getCodigo() { return codigo; }
    public String getSerieBoleta() { return serieBoleta; }
    public String getSerieFactura() { return serieFactura; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return codigo;
    }
}
