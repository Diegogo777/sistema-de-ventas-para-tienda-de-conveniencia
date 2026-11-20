package com.mycompany.tienda3a.controlador;

import com.mycompany.tienda3a.config.Configuracion;
import com.mycompany.tienda3a.excepcion.ExcepcionDatos;
import com.mycompany.tienda3a.excepcion.ExcepcionNegocio;
import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.repositorio.RepositorioCajas;
import com.mycompany.tienda3a.repositorio.RepositorioVentas;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControladorCaja {
    private final RepositorioCajas cajas;
    private final RepositorioVentas ventas;
    private final ControladorVenta controladorVenta;
    private final Bitacora bitacora;

    public ControladorCaja(RepositorioCajas cajas, RepositorioVentas ventas, ControladorVenta controladorVenta,
                           Bitacora bitacora) {
        this.cajas = cajas;
        this.ventas = ventas;
        this.controladorVenta = controladorVenta;
        this.bitacora = bitacora;
    }

    public List<Caja> listarCajas() {
        List<Caja> activas = new ArrayList<>();
        for (Caja c : cajas.listarCajas()) {
            if (c.isActivo()) {
                activas.add(c);
            }
        }
        return activas;
    }

    public CierreCaja cierreDelDia(Cajero cajero, Caja caja, LocalDate fecha) {
        for (CierreCaja c : cajas.listarCierres()) {
            if (c.getCajero() == cajero && c.getCaja() == caja && c.getFecha().equals(fecha)) {
                return c;
            }
        }
        return null;
    }

    public boolean cajaAbierta(Cajero cajero, Caja caja) {
        return cierreDelDia(cajero, caja, LocalDate.now()) == null;
    }

    public CierreCaja prepararCierre(Cajero cajero, Caja caja, LocalDate fecha) {
        CierreCaja cierre = new CierreCaja(0, cajero, caja, fecha, Configuracion.getFondoInicialCaja());
        cierre.calcularResumen(ventasDelDia(cajero, caja, fecha), devolucionesDelDia(cajero, fecha));
        return cierre;
    }

    public void registrarCierre(CierreCaja cierre, double efectivoContado, String observacion) {
        if (cierreDelDia(cierre.getCajero(), cierre.getCaja(), cierre.getFecha()) != null) {
            throw new ExcepcionNegocio("Ya registraste el cierre de esta caja para hoy.");
        }
        if (controladorVenta.tieneVentaEnCurso(cierre.getCajero())) {
            throw new ExcepcionNegocio("Tienes una venta en curso. Confírmala o cancélala antes de cerrar la caja.");
        }
        List<Venta> delDia = ventasDelDia(cierre.getCajero(), cierre.getCaja(), cierre.getFecha());
        List<Pago> devoluciones = devolucionesDelDia(cierre.getCajero(), cierre.getFecha());
        cierre.calcularResumen(delDia, devoluciones);
        try {
            cierre.registrarContado(efectivoContado);
            cierre.confirmar(observacion);
            cajas.guardarCierre(cierre, delDia, devoluciones);
        } catch (IllegalArgumentException e) {
            throw new ExcepcionNegocio(e.getMessage());
        } catch (ExcepcionDatos e) {
            throw new ExcepcionNegocio("No se pudo guardar el cierre en la base de datos. Inténtalo de nuevo.");
        }
        bitacora.registrar(cierre.getCajero(), "CERRAR_CAJA", "cierre_caja", cierre.getIdCierreCaja(), ResultadoOperacion.EXITO,
                String.format("%s · esperado S/ %.2f · contado S/ %.2f · diferencia S/ %.2f", cierre.getCaja().getCodigo(),
                        cierre.getEfectivoEsperado(), cierre.getEfectivoContado(), cierre.getDiferencia()));
    }

    private List<Venta> ventasDelDia(Cajero cajero, Caja caja, LocalDate fecha) {
        List<Venta> lista = new ArrayList<>();
        for (Venta v : ventas.listar()) {
            if (v.getCajero() == cajero && v.getCaja() == caja && v.getFechaHora().toLocalDate().equals(fecha)
                    && v.getCierreCaja() == null
                    && (v.getEstado() == EstadoVenta.CONFIRMADA || v.getEstado() == EstadoVenta.ANULADA)) {
                lista.add(v);
            }
        }
        return lista;
    }

    private List<Pago> devolucionesDelDia(Cajero cajero, LocalDate fecha) {
        List<Pago> lista = new ArrayList<>();
        for (Venta v : ventas.listar()) {
            Pago p = v.getPago();
            if (p != null && p.estaRevertido() && p.getCajeroReversion() == cajero && p.getCierreReversion() == null
                    && p.getFechaReversion() != null && p.getFechaReversion().toLocalDate().equals(fecha)) {
                lista.add(p);
            }
        }
        return lista;
    }
}
