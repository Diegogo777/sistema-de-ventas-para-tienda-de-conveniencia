package com.mycompany.tienda3a.modelo;

import com.mycompany.tienda3a.config.Configuracion;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Venta {
    private int idVenta;
    private Caja caja;
    private Cajero cajero;
    private Cliente cliente;
    private CierreCaja cierreCaja;
    private LocalDateTime fechaHora;
    private EstadoVenta estado;
    private double subtotal;
    private double igv;
    private double total;
    private String claveOperacion;
    private LocalDateTime fechaAnulacion;
    private String motivoAnulacion;
    private Cajero cajeroAnulacion;
    private Pago pago;
    private Comprobante comprobante;
    private final List<DetalleVenta> detalles = new ArrayList<>();
    private final List<MovimientoInventario> movimientos = new ArrayList<>();

    public Venta(int idVenta, Caja caja, Cajero cajero, String claveOperacion, LocalDateTime fechaHora) {
        if (caja == null || cajero == null) {
            throw new IllegalArgumentException("La venta necesita una caja y un cajero.");
        }
        this.idVenta = idVenta;
        this.caja = caja;
        this.cajero = cajero;
        this.claveOperacion = claveOperacion;
        this.fechaHora = fechaHora == null ? LocalDateTime.now() : fechaHora;
        this.estado = EstadoVenta.EN_CURSO;
    }

    public DetalleVenta agregarProducto(Producto producto, double cantidad) {
        exigirEnCurso();
        if (producto == null || !producto.isActivo()) {
            throw new IllegalArgumentException("El producto no está disponible para la venta.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        DetalleVenta existente = buscarLinea(producto);
        double cantidadTotal = cantidad + (existente == null ? 0 : existente.getCantidad());
        if (!producto.hayStock(cantidadTotal)) {
            throw new IllegalStateException("Stock insuficiente de " + producto.getNombre()
                    + ". Disponible: " + producto.formatearCantidad(producto.getStockActual()) + ".");
        }
        if (existente != null) {
            existente.cambiarCantidad(cantidadTotal);
            calcularTotales();
            return existente;
        }
        Promocion promo = producto.promocionVigente(fechaHora.toLocalDate());
        double precioUnitario = promo == null ? producto.getPrecioVenta() : promo.getPrecioPromocional();
        DetalleVenta detalle = new DetalleVenta(0, this, producto, promo, cantidad,
                producto.getPrecioVenta(), precioUnitario);
        detalles.add(detalle);
        calcularTotales();
        return detalle;
    }

    public void modificarCantidad(DetalleVenta detalle, double cantidad) {
        exigirEnCurso();
        if (!detalles.contains(detalle)) {
            throw new IllegalArgumentException("La línea no pertenece a esta venta.");
        }
        if (!detalle.getProducto().hayStock(cantidad)) {
            throw new IllegalStateException("Stock insuficiente. Disponible: "
                    + detalle.getProducto().formatearCantidad(detalle.getProducto().getStockActual()) + ".");
        }
        detalle.cambiarCantidad(cantidad);
        calcularTotales();
    }

    public void retirarProducto(DetalleVenta detalle) {
        exigirEnCurso();
        detalles.remove(detalle);
        calcularTotales();
    }

    public void calcularTotales() {
        double suma = 0;
        for (DetalleVenta d : detalles) {
            suma += d.calcularImporte();
        }
        total = redondear2(suma);
        subtotal = redondear2(total / (1 + Configuracion.getTasaIgv()));
        igv = redondear2(total - subtotal);
    }

    public void asignarCliente(Cliente cliente) {
        exigirEnCurso();
        this.cliente = cliente;
    }

    public boolean revalidarStock() {
        for (DetalleVenta d : detalles) {
            if (!d.getProducto().hayStock(d.getCantidad())) {
                return false;
            }
        }
        return true;
    }

    public void confirmar(Pago pago) {
        exigirEnCurso();
        if (detalles.isEmpty()) {
            throw new IllegalStateException("La venta no tiene productos.");
        }
        if (!revalidarStock()) {
            throw new IllegalStateException("El stock cambió y ya no cubre alguna línea. Ajusta la venta.");
        }
        if (pago == null || !pago.validar()) {
            throw new IllegalArgumentException("El pago no es válido.");
        }
        movimientos.clear();
        for (DetalleVenta d : detalles) {
            MovimientoInventario m = new MovimientoInventario(0, d.getProducto(), d, cajero,
                    TipoMovimiento.SALIDA_VENTA, d.getCantidad());
            m.aplicar();
            movimientos.add(m);
        }
        this.pago = pago;
        this.estado = EstadoVenta.CONFIRMADA;
    }

    public void deshacerConfirmacion() {
        if (estado != EstadoVenta.CONFIRMADA) {
            return;
        }
        for (MovimientoInventario m : movimientos) {
            m.getProducto().reingresarStock(m.getCantidad());
        }
        movimientos.clear();
        pago = null;
        comprobante = null;
        estado = EstadoVenta.EN_CURSO;
    }

    public void cancelar() {
        exigirEnCurso();
        estado = EstadoVenta.CANCELADA;
    }

    public void anular(String motivo, Cajero cajero) {
        if (estado != EstadoVenta.CONFIRMADA) {
            throw new IllegalStateException("Solo se puede anular una venta confirmada.");
        }
        if (motivo == null || motivo.trim().length() < 5) {
            throw new IllegalArgumentException("Escribe el motivo de la anulación (mínimo 5 caracteres).");
        }
        movimientos.clear();
        for (DetalleVenta d : detalles) {
            MovimientoInventario m = new MovimientoInventario(0, d.getProducto(), d, cajero,
                    TipoMovimiento.REINGRESO_ANULACION, d.getCantidad());
            m.aplicar();
            movimientos.add(m);
        }
        if (pago != null) {
            pago.revertir(cajero);
        }
        if (comprobante != null) {
            comprobante.anular();
        }
        this.estado = EstadoVenta.ANULADA;
        this.motivoAnulacion = motivo.trim();
        this.fechaAnulacion = LocalDateTime.now();
        this.cajeroAnulacion = cajero;
    }

    public boolean estaEnCurso() {
        return estado == EstadoVenta.EN_CURSO;
    }

    private void exigirEnCurso() {
        if (estado != EstadoVenta.EN_CURSO) {
            throw new IllegalStateException("La venta ya no está en curso.");
        }
    }

    private DetalleVenta buscarLinea(Producto producto) {
        for (DetalleVenta d : detalles) {
            if (d.getProducto() == producto) {
                return d;
            }
        }
        return null;
    }

    public double getAhorroTotal() {
        double a = 0;
        for (DetalleVenta d : detalles) {
            a += d.getAhorro();
        }
        return redondear2(a);
    }

    static double redondear2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    public void cargarDatos(EstadoVenta estado, Cliente cliente, Pago pago, Comprobante comprobante,
                            LocalDateTime fechaAnulacion, String motivoAnulacion, Cajero cajeroAnulacion) {
        this.estado = estado;
        this.cliente = cliente;
        this.pago = pago;
        this.comprobante = comprobante;
        this.fechaAnulacion = fechaAnulacion;
        this.motivoAnulacion = motivoAnulacion;
        this.cajeroAnulacion = cajeroAnulacion;
    }

    public void cargarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
    }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }
    public Caja getCaja() { return caja; }
    public Cajero getCajero() { return cajero; }
    public Cliente getCliente() { return cliente; }
    public CierreCaja getCierreCaja() { return cierreCaja; }
    public void setCierreCaja(CierreCaja cierreCaja) { this.cierreCaja = cierreCaja; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public EstadoVenta getEstado() { return estado; }
    public double getSubtotal() { return subtotal; }
    public double getIgv() { return igv; }
    public double getTotal() { return total; }
    public String getClaveOperacion() { return claveOperacion; }
    public LocalDateTime getFechaAnulacion() { return fechaAnulacion; }
    public String getMotivoAnulacion() { return motivoAnulacion; }
    public Cajero getCajeroAnulacion() { return cajeroAnulacion; }
    public Pago getPago() { return pago; }
    public Comprobante getComprobante() { return comprobante; }
    public void setComprobante(Comprobante comprobante) { this.comprobante = comprobante; }
    public List<DetalleVenta> getDetalles() { return Collections.unmodifiableList(detalles); }
    public List<MovimientoInventario> getMovimientos() { return Collections.unmodifiableList(movimientos); }

    public int getCantidadArticulos() {
        int n = 0;
        for (DetalleVenta d : detalles) {
            n += d.getProducto().getUnidadMedida() == UnidadMedida.KG ? 1 : (int) d.getCantidad();
        }
        return n;
    }
}
