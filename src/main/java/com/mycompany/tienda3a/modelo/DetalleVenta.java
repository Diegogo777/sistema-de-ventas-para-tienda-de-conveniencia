package com.mycompany.tienda3a.modelo;

public class DetalleVenta {
    private int idDetalleVenta;
    private Venta venta;
    private Producto producto;
    private Promocion promocion;
    private double cantidad;
    private double precioLista;
    private double precioUnitario;
    private double importe;

    public DetalleVenta(int idDetalleVenta, Venta venta, Producto producto, Promocion promocion,
                        double cantidad, double precioLista, double precioUnitario) {
        if (precioUnitario > precioLista) {
            throw new IllegalArgumentException("El precio unitario no puede superar el precio de lista.");
        }
        this.idDetalleVenta = idDetalleVenta;
        this.venta = venta;
        this.producto = producto;
        this.promocion = promocion;
        this.precioLista = precioLista;
        this.precioUnitario = precioUnitario;
        cambiarCantidad(cantidad);
    }

    public double calcularImporte() {
        importe = Math.round(cantidad * precioUnitario * 100.0) / 100.0;
        return importe;
    }

    public void cambiarCantidad(double cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (producto.getUnidadMedida() == UnidadMedida.UNIDAD && cantidad != Math.floor(cantidad)) {
            throw new IllegalArgumentException(producto.getNombre() + " se vende por unidad: usa un número entero.");
        }
        this.cantidad = Producto.redondear3(cantidad);
        calcularImporte();
    }

    public boolean tienePromocion() {
        return promocion != null;
    }

    public double getAhorro() {
        return Math.round(cantidad * (precioLista - precioUnitario) * 100.0) / 100.0;
    }

    public int getIdDetalleVenta() { return idDetalleVenta; }
    public void setIdDetalleVenta(int idDetalleVenta) { this.idDetalleVenta = idDetalleVenta; }
    public Venta getVenta() { return venta; }
    public Producto getProducto() { return producto; }
    public Promocion getPromocion() { return promocion; }
    public double getCantidad() { return cantidad; }
    public double getPrecioLista() { return precioLista; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getImporte() { return importe; }
}
