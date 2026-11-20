package com.mycompany.tienda3a.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Producto {
    private int idProducto;
    private Categoria categoria;
    private String codigo;
    private String nombre;
    private UnidadMedida unidadMedida;
    private double precioVenta;
    private double stockActual;
    private boolean activo;
    private final List<Promocion> promociones = new ArrayList<>();

    public Producto(int idProducto, Categoria categoria, String codigo, String nombre,
                    UnidadMedida unidadMedida, double precioVenta, double stockActual) {
        this.idProducto = idProducto;
        setCategoria(categoria);
        setCodigo(codigo);
        setNombre(nombre);
        setUnidadMedida(unidadMedida);
        setPrecioVenta(precioVenta);
        setStockActual(stockActual);
        this.activo = true;
    }

    public boolean esVendible() {
        return activo && stockActual > 0;
    }

    public boolean hayStock(double cantidad) {
        return activo && stockActual + 1e-9 >= cantidad;
    }

    void descontarStock(double cantidad) {
        if (!hayStock(cantidad)) {
            throw new IllegalStateException("Stock insuficiente de " + nombre + ".");
        }
        stockActual = redondear3(stockActual - cantidad);
    }

    void reingresarStock(double cantidad) {
        stockActual = redondear3(stockActual + cantidad);
    }

    public Promocion promocionVigente(LocalDate fecha) {
        for (Promocion p : promociones) {
            if (p.estaVigente(fecha)) {
                return p;
            }
        }
        return null;
    }

    public double precioAplicable(LocalDate fecha) {
        Promocion p = promocionVigente(fecha);
        return p == null ? precioVenta : p.getPrecioPromocional();
    }

    public void cambiarPrecio(double nuevoPrecio) {
        setPrecioVenta(nuevoPrecio);
    }

    public void desactivar() {
        activo = false;
    }

    public void agregarPromocion(Promocion promocion) {
        for (Promocion p : promociones) {
            if (p.seSuperponeCon(promocion)) {
                throw new IllegalArgumentException("Ya hay una promoción activa en esas fechas: " + p.getNombre() + ".");
            }
        }
        promociones.add(promocion);
    }

    public void cargarPromocion(Promocion promocion) {
        promociones.add(promocion);
    }

    public String formatearCantidad(double cantidad) {
        return unidadMedida == UnidadMedida.KG ? String.format("%.3f kg", cantidad) : String.format("%.0f", cantidad);
    }

    static double redondear3(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public Categoria getCategoria() { return categoria; }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Elige una categoría.");
        }
        this.categoria = categoria;
    }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) {
        if (codigo == null || !codigo.trim().matches("[0-9A-Za-z-]{3,20}")) {
            throw new IllegalArgumentException("El código debe tener de 3 a 20 letras o números.");
        }
        this.codigo = codigo.trim();
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public UnidadMedida getUnidadMedida() { return unidadMedida; }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        if (unidadMedida == null) {
            throw new IllegalArgumentException("Elige la unidad de medida.");
        }
        this.unidadMedida = unidadMedida;
    }

    public double getPrecioVenta() { return precioVenta; }

    public void setPrecioVenta(double precioVenta) {
        if (precioVenta <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor que cero.");
        }
        this.precioVenta = Math.round(precioVenta * 100.0) / 100.0;
    }

    public double getStockActual() { return stockActual; }

    public void setStockActual(double stockActual) {
        if (stockActual < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        this.stockActual = redondear3(stockActual);
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public List<Promocion> getPromociones() { return Collections.unmodifiableList(promociones); }

    @Override
    public String toString() {
        return nombre;
    }
}
