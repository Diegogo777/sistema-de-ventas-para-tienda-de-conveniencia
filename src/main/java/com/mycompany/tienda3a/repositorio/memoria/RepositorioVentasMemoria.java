package com.mycompany.tienda3a.repositorio.memoria;

import com.mycompany.tienda3a.modelo.Comprobante;
import com.mycompany.tienda3a.modelo.DetalleVenta;
import com.mycompany.tienda3a.modelo.MovimientoInventario;
import com.mycompany.tienda3a.modelo.Venta;
import com.mycompany.tienda3a.repositorio.RepositorioVentas;
import java.util.ArrayList;
import java.util.List;

public class RepositorioVentasMemoria implements RepositorioVentas {
    protected final List<Venta> ventas = new ArrayList<>();
    protected final List<MovimientoInventario> movimientos = new ArrayList<>();
    private int ultimaVenta;
    private int ultimoDetalle;
    private int ultimoPago;
    private int ultimoComprobante;
    private int ultimoMovimiento;

    @Override
    public List<Venta> listar() {
        return new ArrayList<>(ventas);
    }

    @Override
    public List<MovimientoInventario> listarMovimientos() {
        return new ArrayList<>(movimientos);
    }

    @Override
    public int siguienteCorrelativo(String serie) {
        int max = 0;
        for (Venta v : ventas) {
            Comprobante c = v.getComprobante();
            if (c != null && c.getSerie().equals(serie)) {
                max = Math.max(max, c.getCorrelativo());
            }
        }
        return max + 1;
    }

    @Override
    public void guardarVentaConfirmada(Venta venta) {
        if (venta.getIdVenta() == 0) {
            venta.setIdVenta(++ultimaVenta);
        }
        ultimaVenta = Math.max(ultimaVenta, venta.getIdVenta());
        for (DetalleVenta d : venta.getDetalles()) {
            if (d.getIdDetalleVenta() == 0) {
                d.setIdDetalleVenta(++ultimoDetalle);
            }
            ultimoDetalle = Math.max(ultimoDetalle, d.getIdDetalleVenta());
        }
        if (venta.getPago() != null) {
            if (venta.getPago().getIdPago() == 0) {
                venta.getPago().setIdPago(++ultimoPago);
            }
            ultimoPago = Math.max(ultimoPago, venta.getPago().getIdPago());
        }
        if (venta.getComprobante() != null) {
            if (venta.getComprobante().getIdComprobante() == 0) {
                venta.getComprobante().setIdComprobante(++ultimoComprobante);
            }
            ultimoComprobante = Math.max(ultimoComprobante, venta.getComprobante().getIdComprobante());
        }
        agregarMovimientos(venta);
        if (!ventas.contains(venta)) {
            ventas.add(venta);
        }
    }

    @Override
    public void guardarAnulacion(Venta venta) {
        agregarMovimientos(venta);
    }

    @Override
    public void actualizarComprobante(Comprobante comprobante) {
    }

    public void cargarVenta(Venta venta) {
        ventas.add(venta);
        ultimaVenta = Math.max(ultimaVenta, venta.getIdVenta());
        for (DetalleVenta d : venta.getDetalles()) {
            ultimoDetalle = Math.max(ultimoDetalle, d.getIdDetalleVenta());
        }
        if (venta.getPago() != null) {
            ultimoPago = Math.max(ultimoPago, venta.getPago().getIdPago());
        }
        if (venta.getComprobante() != null) {
            ultimoComprobante = Math.max(ultimoComprobante, venta.getComprobante().getIdComprobante());
        }
    }

    public void cargarMovimiento(MovimientoInventario m) {
        movimientos.add(m);
        ultimoMovimiento = Math.max(ultimoMovimiento, m.getIdMovimiento());
    }

    private void agregarMovimientos(Venta venta) {
        for (MovimientoInventario m : venta.getMovimientos()) {
            if (m.getIdMovimiento() == 0) {
                m.setIdMovimiento(++ultimoMovimiento);
            }
            ultimoMovimiento = Math.max(ultimoMovimiento, m.getIdMovimiento());
            if (!movimientos.contains(m)) {
                movimientos.add(m);
            }
        }
    }
}
