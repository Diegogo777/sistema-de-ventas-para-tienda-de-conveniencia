package com.mycompany.tienda3a.repositorio.mysql;

import com.mycompany.tienda3a.modelo.Comprobante;
import com.mycompany.tienda3a.modelo.DetalleVenta;
import com.mycompany.tienda3a.modelo.MovimientoInventario;
import com.mycompany.tienda3a.modelo.Pago;
import com.mycompany.tienda3a.modelo.PagoEfectivo;
import com.mycompany.tienda3a.modelo.PagoElectronico;
import com.mycompany.tienda3a.modelo.PagoTarjeta;
import com.mycompany.tienda3a.modelo.PagoBilletera;
import com.mycompany.tienda3a.modelo.Venta;
import com.mycompany.tienda3a.repositorio.memoria.RepositorioVentasMemoria;
import java.sql.Connection;
import java.sql.SQLException;

public class RepositorioVentasMySQL extends RepositorioVentasMemoria {
    @Override
    public void guardarVentaConfirmada(Venta v) {
        ConexionMySQL.enTransaccion(c -> {
            v.setIdVenta(ConexionMySQL.insertar(c,
                    "INSERT INTO venta (caja_id, usuario_id, cliente_id, fecha_hora, estado, subtotal, igv, total, clave_operacion) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    v.getCaja().getIdCaja(), v.getCajero().getIdUsuario(),
                    v.getCliente() == null ? null : v.getCliente().getIdCliente(),
                    v.getFechaHora(), v.getEstado(), v.getSubtotal(), v.getIgv(), v.getTotal(), v.getClaveOperacion()));
            for (DetalleVenta d : v.getDetalles()) {
                d.setIdDetalleVenta(ConexionMySQL.insertar(c,
                        "INSERT INTO detalle_venta (venta_id, producto_id, promocion_id, cantidad, precio_lista, precio_unitario, importe) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        v.getIdVenta(), d.getProducto().getIdProducto(),
                        d.getPromocion() == null ? null : d.getPromocion().getIdPromocion(),
                        d.getCantidad(), d.getPrecioLista(), d.getPrecioUnitario(), d.getImporte()));
            }
            insertarPago(c, v.getPago());
            Comprobante k = v.getComprobante();
            k.setIdComprobante(ConexionMySQL.insertar(c,
                    "INSERT INTO comprobante (venta_id, tipo, serie, correlativo, fecha_emision, estado, intentos_envio) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    v.getIdVenta(), k.getTipo(), k.getSerie(), k.getCorrelativo(), k.getFechaEmision(), k.getEstado(),
                    k.getIntentosEnvio()));
            insertarMovimientos(c, v);
        });
        super.guardarVentaConfirmada(v);
    }

    @Override
    public void guardarAnulacion(Venta v) {
        ConexionMySQL.enTransaccion(c -> {
            ConexionMySQL.actualizar(c,
                    "UPDATE venta SET estado = ?, fecha_anulacion = ?, motivo_anulacion = ?, anulado_por = ? WHERE venta_id = ?",
                    v.getEstado(), v.getFechaAnulacion(), v.getMotivoAnulacion(),
                    v.getCajeroAnulacion() == null ? null : v.getCajeroAnulacion().getIdUsuario(), v.getIdVenta());
            Pago p = v.getPago();
            if (p != null) {
                ConexionMySQL.actualizar(c,
                        "UPDATE pago SET estado = ?, fecha_reversion = ?, revertido_por = ?, referencia_reversion = ? WHERE pago_id = ?",
                        p.getEstado(), p.getFechaReversion(),
                        p.getCajeroReversion() == null ? null : p.getCajeroReversion().getIdUsuario(),
                        p instanceof PagoTarjeta t ? t.getReferenciaReversion()
                                : p instanceof PagoBilletera b ? b.getReferenciaReversion() : null,
                        p.getIdPago());
            }
            if (v.getComprobante() != null) {
                actualizarComprobante(c, v.getComprobante());
            }
            insertarMovimientos(c, v);
        });
        super.guardarAnulacion(v);
    }

    @Override
    public void actualizarComprobante(Comprobante k) {
        ConexionMySQL.enTransaccion(c -> actualizarComprobante(c, k));
        super.actualizarComprobante(k);
    }

    private static void actualizarComprobante(Connection c, Comprobante k) throws SQLException {
        ConexionMySQL.actualizar(c,
                "UPDATE comprobante SET estado = ?, intentos_envio = ?, fecha_ultimo_envio = ?, fecha_respuesta = ?, "
                + "observacion_sunat = ?, observacion_revision = ? WHERE comprobante_id = ?",
                k.getEstado(), k.getIntentosEnvio(), k.getFechaUltimoEnvio(), k.getFechaRespuesta(),
                k.getObservacionSunat(), k.getObservacionRevision(), k.getIdComprobante());
    }

    private static void insertarPago(Connection c, Pago p) throws SQLException {
        Double entregado = null;
        Double vuelto = null;
        String referencia = null;
        if (p instanceof PagoEfectivo e) {
            entregado = e.getMontoEntregado();
            vuelto = e.getVuelto();
        } else if (p instanceof PagoElectronico pe) {
            referencia = pe.getReferenciaDigital();
        }
        p.setIdPago(ConexionMySQL.insertar(c,
                "INSERT INTO pago (venta_id, usuario_id, medio, monto, monto_entregado, vuelto, referencia_digital, estado, fecha_hora) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                p.getVenta().getIdVenta(), p.getCajero().getIdUsuario(), p.getMedio(), p.getMonto(), entregado, vuelto,
                referencia, p.getEstado(), p.getFechaHora()));
    }

    private static void insertarMovimientos(Connection c, Venta v) throws SQLException {
        for (MovimientoInventario m : v.getMovimientos()) {
            if (m.getIdMovimiento() != 0) {
                continue;
            }
            m.setIdMovimiento(ConexionMySQL.insertar(c,
                    "INSERT INTO movimiento_inventario (producto_id, detalle_venta_id, usuario_id, fecha_hora, tipo, cantidad, saldo_resultante) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    m.getProducto().getIdProducto(),
                    m.getDetalleVenta() == null ? null : ConexionMySQL.idONulo(m.getDetalleVenta().getIdDetalleVenta()),
                    m.getCajero().getIdUsuario(), m.getFechaHora(), m.getTipo(), m.getCantidad(), m.getSaldoResultante()));
            ConexionMySQL.actualizar(c, "UPDATE producto SET stock_actual = ? WHERE producto_id = ?",
                    m.getProducto().getStockActual(), m.getProducto().getIdProducto());
        }
    }
}
