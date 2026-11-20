package com.mycompany.tienda3a.datos;

import com.mycompany.tienda3a.excepcion.ExcepcionDatos;
import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.repositorio.Repositorios;
import com.mycompany.tienda3a.repositorio.memoria.*;
import com.mycompany.tienda3a.repositorio.mysql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public final class CargaMySQL {
    private CargaMySQL() {
    }

    public static Repositorios cargar() {
        RepositorioUsuariosMySQL usuarios = new RepositorioUsuariosMySQL();
        RepositorioCatalogoMySQL catalogo = new RepositorioCatalogoMySQL();
        RepositorioClientesMySQL clientes = new RepositorioClientesMySQL();
        RepositorioCajasMySQL cajas = new RepositorioCajasMySQL();
        RepositorioVentasMySQL ventas = new RepositorioVentasMySQL();
        RepositorioAuditoriaMySQL auditoria = new RepositorioAuditoriaMySQL();

        Map<Integer, Usuario> mUsuarios = new HashMap<>();
        Map<Integer, Caja> mCajas = new HashMap<>();
        Map<Integer, Categoria> mCategorias = new HashMap<>();
        Map<Integer, Producto> mProductos = new HashMap<>();
        Map<Integer, Promocion> mPromociones = new HashMap<>();
        Map<Integer, Cliente> mClientes = new HashMap<>();
        Map<Integer, CierreCaja> mCierres = new HashMap<>();
        Map<Integer, Venta> mVentas = new HashMap<>();
        Map<Integer, DetalleVenta> mDetalles = new HashMap<>();

        try (Connection c = ConexionMySQL.obtener(); Statement s = c.createStatement()) {
            try (ResultSet r = s.executeQuery("SELECT * FROM caja ORDER BY caja_id")) {
                while (r.next()) {
                    Caja k = new Caja(r.getInt("caja_id"), r.getString("codigo"), r.getString("serie_boleta"), r.getString("serie_factura"));
                    k.setActivo(r.getBoolean("activo"));
                    mCajas.put(k.getIdCaja(), k);
                    ((RepositorioCajasMemoria) cajas).agregarCaja(k);
                }
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM usuario ORDER BY usuario_id")) {
                while (r.next()) {
                    int id = r.getInt("usuario_id");
                    Usuario u = "ADMINISTRADOR".equals(r.getString("rol"))
                            ? new Administrador(id, r.getString("nombre"), r.getString("nombre_acceso"), r.getString("clave_hash"),
                                    r.getBoolean("activo"), fecha(r.getTimestamp("creado_en")))
                            : new Cajero(id, r.getString("nombre"), r.getString("nombre_acceso"), r.getString("clave_hash"),
                                    r.getBoolean("activo"), fecha(r.getTimestamp("creado_en")));
                    u.setIntentosFallidos(r.getInt("intentos_fallidos"));
                    u.setBloqueadoHasta(fecha(r.getTimestamp("bloqueado_hasta")));
                    mUsuarios.put(id, u);
                    ((RepositorioUsuariosMemoria) usuarios).guardarCargado(u);
                }
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM categoria ORDER BY categoria_id")) {
                while (r.next()) {
                    Categoria k = new Categoria(r.getInt("categoria_id"), r.getString("nombre"), r.getString("descripcion"));
                    k.setActivo(r.getBoolean("activo"));
                    mCategorias.put(k.getIdCategoria(), k);
                    catalogo.cargarCategoria(k);
                }
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM producto ORDER BY producto_id")) {
                while (r.next()) {
                    Producto p = new Producto(r.getInt("producto_id"), mCategorias.get(r.getInt("categoria_id")),
                            r.getString("codigo"), r.getString("nombre"), UnidadMedida.valueOf(r.getString("unidad_medida")),
                            r.getDouble("precio_venta"), r.getDouble("stock_actual"));
                    p.setActivo(r.getBoolean("activo"));
                    mProductos.put(p.getIdProducto(), p);
                    catalogo.cargarProducto(p);
                }
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM promocion ORDER BY promocion_id")) {
                while (r.next()) {
                    Producto p = mProductos.get(r.getInt("producto_id"));
                    Usuario autor = mUsuarios.get(r.getInt("creado_por"));
                    Promocion k = Promocion.cargada(r.getInt("promocion_id"), p,
                            autor instanceof Administrador a ? a : null, r.getString("nombre"),
                            r.getDouble("precio_promocional"), r.getDate("fecha_inicio").toLocalDate(),
                            r.getDate("fecha_fin").toLocalDate(), r.getBoolean("activo"));
                    p.cargarPromocion(k);
                    mPromociones.put(k.getIdPromocion(), k);
                    catalogo.cargarPromocion(k);
                }
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM cliente ORDER BY cliente_id")) {
                while (r.next()) {
                    Cliente k = new Cliente(r.getInt("cliente_id"), TipoDocumento.valueOf(r.getString("tipo_documento")),
                            r.getString("numero_documento"), r.getString("nombre_razon_social"), r.getString("direccion"));
                    k.setActivo(r.getBoolean("activo"));
                    mClientes.put(k.getIdCliente(), k);
                    clientes.cargar(k);
                }
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM cierre_caja ORDER BY cierre_caja_id")) {
                while (r.next()) {
                    CierreCaja k = new CierreCaja(r.getInt("cierre_caja_id"), (Cajero) mUsuarios.get(r.getInt("usuario_id")),
                            mCajas.get(r.getInt("caja_id")), r.getDate("fecha").toLocalDate(), r.getDouble("fondo_inicial"));
                    k.cargarDatos(fecha(r.getTimestamp("fecha_hora_cierre")), r.getInt("cantidad_ventas"),
                            r.getInt("cantidad_anuladas"), r.getDouble("total_efectivo"), r.getDouble("total_tarjeta"),
                            r.getDouble("total_billetera"), r.getDouble("devuelto_efectivo"), r.getDouble("efectivo_esperado"),
                            r.getDouble("efectivo_contado"), r.getDouble("diferencia"), r.getString("observacion"));
                    mCierres.put(k.getIdCierreCaja(), k);
                    cajas.cargarCierre(k);
                }
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM venta ORDER BY venta_id")) {
                while (r.next()) {
                    Venta v = new Venta(r.getInt("venta_id"), mCajas.get(r.getInt("caja_id")),
                            (Cajero) mUsuarios.get(r.getInt("usuario_id")), r.getString("clave_operacion"),
                            fecha(r.getTimestamp("fecha_hora")));
                    int cierre = r.getInt("cierre_caja_id");
                    if (!r.wasNull()) {
                        v.setCierreCaja(mCierres.get(cierre));
                    }
                    Usuario anulo = mUsuarios.get(r.getInt("anulado_por"));
                    v.cargarDatos(EstadoVenta.valueOf(r.getString("estado")), mClientes.get(r.getInt("cliente_id")), null, null,
                            fecha(r.getTimestamp("fecha_anulacion")), r.getString("motivo_anulacion"),
                            anulo instanceof Cajero cj ? cj : null);
                    mVentas.put(v.getIdVenta(), v);
                }
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM detalle_venta ORDER BY detalle_venta_id")) {
                while (r.next()) {
                    Venta v = mVentas.get(r.getInt("venta_id"));
                    DetalleVenta d = new DetalleVenta(r.getInt("detalle_venta_id"), v, mProductos.get(r.getInt("producto_id")),
                            mPromociones.get(r.getInt("promocion_id")), r.getDouble("cantidad"), r.getDouble("precio_lista"),
                            r.getDouble("precio_unitario"));
                    v.cargarDetalle(d);
                    mDetalles.put(d.getIdDetalleVenta(), d);
                }
            }
            for (Venta v : mVentas.values()) {
                v.calcularTotales();
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM pago ORDER BY pago_id")) {
                while (r.next()) {
                    Venta v = mVentas.get(r.getInt("venta_id"));
                    Cajero cj = (Cajero) mUsuarios.get(r.getInt("usuario_id"));
                    LocalDateTime f = fecha(r.getTimestamp("fecha_hora"));
                    Pago p = switch (MedioPago.valueOf(r.getString("medio"))) {
                        case EFECTIVO -> new PagoEfectivo(r.getInt("pago_id"), v, cj, r.getDouble("monto"), f, r.getDouble("monto_entregado"));
                        case TARJETA -> new PagoTarjeta(r.getInt("pago_id"), v, cj, r.getDouble("monto"), f, r.getString("referencia_digital"));
                        case BILLETERA -> new PagoBilletera(r.getInt("pago_id"), v, cj, r.getDouble("monto"), f, r.getString("referencia_digital"));
                    };
                    Usuario rev = mUsuarios.get(r.getInt("revertido_por"));
                    p.cargarReversion(EstadoPago.valueOf(r.getString("estado")), fecha(r.getTimestamp("fecha_reversion")),
                            rev instanceof Cajero x ? x : null);
                    if (p instanceof PagoElectronico pe) {
                        pe.registrarReferenciaReversion(r.getString("referencia_reversion"));
                    }
                    int cierre = r.getInt("cierre_reversion_id");
                    if (!r.wasNull()) {
                        p.asignarCierreReversion(mCierres.get(cierre));
                    }
                    v.cargarDatos(v.getEstado(), v.getCliente(), p, v.getComprobante(), v.getFechaAnulacion(),
                            v.getMotivoAnulacion(), v.getCajeroAnulacion());
                }
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM comprobante ORDER BY comprobante_id")) {
                while (r.next()) {
                    Venta v = mVentas.get(r.getInt("venta_id"));
                    int id = r.getInt("comprobante_id");
                    String serie = r.getString("serie");
                    int corr = r.getInt("correlativo");
                    LocalDateTime f = fecha(r.getTimestamp("fecha_emision"));
                    Comprobante k = switch (TipoComprobante.valueOf(r.getString("tipo"))) {
                        case VENTA_SIMPLE -> new VentaSimpleComprobante(id, v, serie, corr, f);
                        case BOLETA -> new Boleta(id, v, serie, corr, f);
                        case FACTURA -> new Factura(id, v, serie, corr, f);
                    };
                    k.cargarEstado(EstadoComprobante.valueOf(r.getString("estado")), r.getInt("intentos_envio"),
                            fecha(r.getTimestamp("fecha_ultimo_envio")), fecha(r.getTimestamp("fecha_respuesta")),
                            r.getString("observacion_sunat"), r.getString("observacion_revision"));
                    v.setComprobante(k);
                }
            }
            for (Venta v : mVentas.values()) {
                ventas.cargarVenta(v);
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM movimiento_inventario ORDER BY movimiento_id")) {
                while (r.next()) {
                    MovimientoInventario m = new MovimientoInventario(r.getInt("movimiento_id"),
                            mProductos.get(r.getInt("producto_id")), mDetalles.get(r.getInt("detalle_venta_id")),
                            (Cajero) mUsuarios.get(r.getInt("usuario_id")), TipoMovimiento.valueOf(r.getString("tipo")),
                            r.getDouble("cantidad"));
                    m.cargarDatos(fecha(r.getTimestamp("fecha_hora")), r.getDouble("saldo_resultante"));
                    ventas.cargarMovimiento(m);
                }
            }
            try (ResultSet r = s.executeQuery("SELECT * FROM auditoria ORDER BY auditoria_id")) {
                while (r.next()) {
                    auditoria.cargar(new Auditoria(r.getInt("auditoria_id"), mUsuarios.get(r.getInt("usuario_id")),
                            fecha(r.getTimestamp("fecha_hora")), r.getString("accion"), r.getString("entidad"),
                            r.getString("registro_id"), ResultadoOperacion.valueOf(r.getString("resultado")),
                            r.getString("detalle")));
                }
            }
        } catch (SQLException | RuntimeException e) {
            throw new ExcepcionDatos("No se pudieron leer los datos de MySQL: " + e.getMessage(), e);
        }
        return new Repositorios(usuarios, catalogo, clientes, cajas, ventas, auditoria, "MySQL");
    }

    private static LocalDateTime fecha(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }
}
