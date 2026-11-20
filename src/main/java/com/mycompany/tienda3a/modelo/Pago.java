package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

public abstract class Pago {
    protected int idPago;
    protected Venta venta;
    protected Cajero cajero;
    protected LocalDateTime fechaHora;
    protected double monto;
    protected EstadoPago estado;
    protected LocalDateTime fechaReversion;
    protected Cajero cajeroReversion;
    protected CierreCaja cierreReversion;

    public Pago(int idPago, Venta venta, Cajero cajero, double monto, LocalDateTime fechaHora) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor que cero.");
        }
        this.idPago = idPago;
        this.venta = venta;
        this.cajero = cajero;
        this.monto = Venta.redondear2(monto);
        this.fechaHora = fechaHora == null ? LocalDateTime.now() : fechaHora;
        this.estado = EstadoPago.REGISTRADO;
    }

    public abstract boolean validar();

    public abstract MedioPago getMedio();

    public void revertir(Cajero cajero) {
        if (estado == EstadoPago.REVERTIDO) {
            throw new IllegalStateException("El pago ya fue revertido.");
        }
        estado = EstadoPago.REVERTIDO;
        fechaReversion = LocalDateTime.now();
        cajeroReversion = cajero;
    }

    public void asignarCierreReversion(CierreCaja cierre) {
        this.cierreReversion = cierre;
    }

    public boolean estaRevertido() {
        return estado == EstadoPago.REVERTIDO;
    }

    public void cargarReversion(EstadoPago estado, LocalDateTime fechaReversion, Cajero cajeroReversion) {
        this.estado = estado;
        this.fechaReversion = fechaReversion;
        this.cajeroReversion = cajeroReversion;
    }

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }
    public Venta getVenta() { return venta; }
    public Cajero getCajero() { return cajero; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public double getMonto() { return monto; }
    public EstadoPago getEstado() { return estado; }
    public LocalDateTime getFechaReversion() { return fechaReversion; }
    public Cajero getCajeroReversion() { return cajeroReversion; }
    public CierreCaja getCierreReversion() { return cierreReversion; }
}
