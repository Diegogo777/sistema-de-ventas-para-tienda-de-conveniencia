package com.mycompany.tienda3a.controlador;

import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.repositorio.RepositorioAuditoria;
import com.mycompany.tienda3a.repositorio.RepositorioVentas;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ControladorConsulta {
    private final RepositorioVentas ventas;
    private final RepositorioAuditoria auditoria;

    public ControladorConsulta(RepositorioVentas ventas, RepositorioAuditoria auditoria) {
        this.ventas = ventas;
        this.auditoria = auditoria;
    }

    public List<Venta> consultarVentas(LocalDate desde, LocalDate hasta, Caja caja, Cajero cajero, String numero) {
        String num = numero == null ? "" : numero.trim().toUpperCase();
        List<Venta> lista = new ArrayList<>();
        for (Venta v : ventas.listar()) {
            if (v.getEstado() != EstadoVenta.CONFIRMADA && v.getEstado() != EstadoVenta.ANULADA) {
                continue;
            }
            LocalDate f = v.getFechaHora().toLocalDate();
            if ((desde != null && f.isBefore(desde)) || (hasta != null && f.isAfter(hasta))) {
                continue;
            }
            if ((caja != null && v.getCaja() != caja) || (cajero != null && v.getCajero().getIdUsuario() != cajero.getIdUsuario())) {
                continue;
            }
            if (!num.isEmpty() && (v.getComprobante() == null || !v.getComprobante().getNumero().contains(num))) {
                continue;
            }
            lista.add(v);
        }
        lista.sort(Comparator.comparing(Venta::getFechaHora).reversed());
        return lista;
    }

    public List<DetalleVenta> verDetalle(Venta venta) {
        return venta.getDetalles();
    }

    public Map<String, Double> reportePorProducto(LocalDate desde, LocalDate hasta) {
        Map<String, Double> m = new HashMap<>();
        for (Venta v : confirmadas(desde, hasta)) {
            for (DetalleVenta d : v.getDetalles()) {
                m.merge(d.getProducto().getNombre(), d.getImporte(), Double::sum);
            }
        }
        return ordenar(m);
    }

    public Map<String, Double> reportePorCaja(LocalDate desde, LocalDate hasta) {
        Map<String, Double> m = new HashMap<>();
        for (Venta v : confirmadas(desde, hasta)) {
            m.merge(v.getCaja().getCodigo(), v.getTotal(), Double::sum);
        }
        return ordenar(m);
    }

    public Map<String, Double> reportePorCajero(LocalDate desde, LocalDate hasta) {
        Map<String, Double> m = new HashMap<>();
        for (Venta v : confirmadas(desde, hasta)) {
            m.merge(v.getCajero().getNombre(), v.getTotal(), Double::sum);
        }
        return ordenar(m);
    }

    public Map<String, Double> cantidadesPorProducto(LocalDate desde, LocalDate hasta) {
        Map<String, Double> m = new HashMap<>();
        for (Venta v : confirmadas(desde, hasta)) {
            for (DetalleVenta d : v.getDetalles()) {
                m.merge(d.getProducto().getNombre(), d.getCantidad(), Double::sum);
            }
        }
        return m;
    }

    public ResumenVentas resumen(LocalDate desde, LocalDate hasta) {
        int cantidad = 0;
        int anuladas = 0;
        double total = 0;
        double totalAnulado = 0;
        Map<MedioPago, Double> porMedio = new EnumMap<>(MedioPago.class);
        for (MedioPago m : MedioPago.values()) {
            porMedio.put(m, 0.0);
        }
        Map<LocalDate, Double> porDia = new LinkedHashMap<>();
        for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) {
            porDia.put(d, 0.0);
        }
        for (Venta v : consultarVentas(desde, hasta, null, null, null)) {
            if (v.getEstado() == EstadoVenta.ANULADA) {
                anuladas++;
                totalAnulado += v.getTotal();
                continue;
            }
            cantidad++;
            total += v.getTotal();
            if (v.getPago() != null) {
                porMedio.merge(v.getPago().getMedio(), v.getTotal(), Double::sum);
            }
            porDia.merge(v.getFechaHora().toLocalDate(), v.getTotal(), Double::sum);
        }
        return new ResumenVentas(cantidad, total, anuladas, totalAnulado,
                cantidad == 0 ? 0 : total / cantidad, porMedio, porDia);
    }

    public List<Auditoria> consultarBitacora(LocalDate desde, LocalDate hasta) {
        List<Auditoria> lista = new ArrayList<>();
        for (Auditoria a : auditoria.listar()) {
            LocalDate f = a.getFechaHora().toLocalDate();
            if ((desde == null || !f.isBefore(desde)) && (hasta == null || !f.isAfter(hasta))) {
                lista.add(a);
            }
        }
        lista.sort(Comparator.comparing(Auditoria::getFechaHora).reversed());
        return lista;
    }

    private List<Venta> confirmadas(LocalDate desde, LocalDate hasta) {
        List<Venta> lista = new ArrayList<>();
        for (Venta v : consultarVentas(desde, hasta, null, null, null)) {
            if (v.getEstado() == EstadoVenta.CONFIRMADA) {
                lista.add(v);
            }
        }
        return lista;
    }

    private static Map<String, Double> ordenar(Map<String, Double> m) {
        Map<String, Double> r = new LinkedHashMap<>();
        m.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(e -> r.put(e.getKey(), Math.round(e.getValue() * 100.0) / 100.0));
        return r;
    }

    public record ResumenVentas(int cantidadVentas, double total, int anuladas, double totalAnulado,
                                double ticketPromedio, Map<MedioPago, Double> porMedio, Map<LocalDate, Double> porDia) {
    }
}
