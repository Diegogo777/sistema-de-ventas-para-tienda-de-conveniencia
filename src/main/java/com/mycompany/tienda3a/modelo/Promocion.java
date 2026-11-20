package com.mycompany.tienda3a.modelo;

import java.time.LocalDate;

public class Promocion {
    private int idPromocion;
    private Producto producto;
    private Administrador creadoPor;
    private String nombre;
    private double precioPromocional;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activo;

    public Promocion(int idPromocion, Producto producto, Administrador creadoPor, String nombre,
                     double precioPromocional, LocalDate fechaInicio, LocalDate fechaFin) {
        if (producto == null) {
            throw new IllegalArgumentException("Elige el producto de la promoción.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la promoción es obligatorio.");
        }
        if (precioPromocional <= 0 || precioPromocional >= producto.getPrecioVenta()) {
            throw new IllegalArgumentException("El precio promocional debe ser mayor que cero y menor que el precio de venta.");
        }
        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial.");
        }
        this.idPromocion = idPromocion;
        this.producto = producto;
        this.creadoPor = creadoPor;
        this.nombre = nombre.trim();
        this.precioPromocional = Math.round(precioPromocional * 100.0) / 100.0;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = true;
    }

    private Promocion(int idPromocion, Producto producto, Administrador creadoPor, String nombre,
                      double precioPromocional, LocalDate fechaInicio, LocalDate fechaFin, boolean activo) {
        this.idPromocion = idPromocion;
        this.producto = producto;
        this.creadoPor = creadoPor;
        this.nombre = nombre;
        this.precioPromocional = precioPromocional;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = activo;
    }

    public static Promocion cargada(int idPromocion, Producto producto, Administrador creadoPor, String nombre,
                                    double precioPromocional, LocalDate fechaInicio, LocalDate fechaFin, boolean activo) {
        return new Promocion(idPromocion, producto, creadoPor, nombre, precioPromocional, fechaInicio, fechaFin, activo);
    }

    public boolean estaVigente(LocalDate fecha) {
        return activo && !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public boolean seSuperponeCon(Promocion otra) {
        return activo && otra.activo && otra != this
                && !otra.fechaFin.isBefore(fechaInicio) && !otra.fechaInicio.isAfter(fechaFin);
    }

    public void desactivar() {
        activo = false;
    }

    public int getIdPromocion() { return idPromocion; }
    public void setIdPromocion(int idPromocion) { this.idPromocion = idPromocion; }
    public Producto getProducto() { return producto; }
    public Administrador getCreadoPor() { return creadoPor; }
    public String getNombre() { return nombre; }
    public double getPrecioPromocional() { return precioPromocional; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
