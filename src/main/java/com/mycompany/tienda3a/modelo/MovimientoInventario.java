package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

public class MovimientoInventario {
    private int idMovimiento;
    private Producto producto;
    private DetalleVenta detalleVenta;
    private Cajero cajero;
    private LocalDateTime fechaHora;
    private TipoMovimiento tipo;
    private double cantidad;
    private double saldoResultante;

    public MovimientoInventario(int idMovimiento, Producto producto, DetalleVenta detalleVenta, Cajero cajero,
                                TipoMovimiento tipo, double cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del movimiento debe ser mayor que cero.");
        }
        this.idMovimiento = idMovimiento;
        this.producto = producto;
        this.detalleVenta = detalleVenta;
        this.cajero = cajero;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fechaHora = LocalDateTime.now();
    }

    public void aplicar() {
        if (tipo == TipoMovimiento.SALIDA_VENTA) {
            producto.descontarStock(cantidad);
        } else {
            producto.reingresarStock(cantidad);
        }
        saldoResultante = producto.getStockActual();
    }

    public void cargarDatos(LocalDateTime fechaHora, double saldoResultante) {
        this.fechaHora = fechaHora;
        this.saldoResultante = saldoResultante;
    }

    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }
    public Producto getProducto() { return producto; }
    public DetalleVenta getDetalleVenta() { return detalleVenta; }
    public Cajero getCajero() { return cajero; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public TipoMovimiento getTipo() { return tipo; }
    public double getCantidad() { return cantidad; }
    public double getSaldoResultante() { return saldoResultante; }
}
