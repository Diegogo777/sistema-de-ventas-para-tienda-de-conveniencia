package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.controlador.ControladorConsulta.ResumenVentas;
import com.mycompany.tienda3a.modelo.MedioPago;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;

class PanelInicio extends PanelBase {
    private final Selector<Integer> periodo = new Selector<>(List.of(1, 7, 30), List.of("Hoy", "7 días", "30 días"), null);
    private final JLabel saludo = Textos.titulo("");
    private final JLabel rango = Textos.ayuda("");
    private final Indicador ventas = new Indicador("Ventas confirmadas", Icono.Tipo.VENTAS, Tema.PETROLEO);
    private final Indicador ticket = new Indicador("Ticket promedio", Icono.Tipo.CARRITO, Tema.VERDE);
    private final Indicador anuladas = new Indicador("Anulaciones", Icono.Tipo.ANULAR, Tema.ROJO);
    private final Indicador pendientes = new Indicador("Comprobantes por revisar", Icono.Tipo.COMPROBANTE, Tema.AMBAR);
    private final Grafico porDia = new Grafico(false, Tema.PETROLEO);
    private final Grafico porMedio = new Grafico(true, Tema.VERDE);
    private final Grafico porProducto = new Grafico(true, Tema.PETROLEO);
    private final Grafico porCajero = new Grafico(true, Tema.AMBAR);

    PanelInicio(Contexto ctx) {
        super(ctx, "Inicio", "Resumen de las ventas de la tienda.", Icono.Tipo.INICIO, "CU-10");
        JPanel col = Textos.transparente(null);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        JPanel cab = Textos.transparente(new BorderLayout());
        JPanel txt = Textos.transparente(new GridLayout(2, 1));
        saludo.setFont(Tema.negrita(19));
        txt.add(saludo);
        txt.add(rango);
        cab.add(txt, BorderLayout.WEST);
        periodo.setPreferredSize(new Dimension(300, 46));
        JPanel sel = Textos.transparente(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        sel.add(periodo);
        cab.add(sel, BorderLayout.EAST);
        col.add(ajustar(cab));
        col.add(Box.createVerticalStrut(14));

        JPanel kpis = Textos.transparente(new GridLayout(1, 4, 14, 0));
        kpis.add(ventas);
        kpis.add(ticket);
        kpis.add(anuladas);
        kpis.add(pendientes);
        col.add(ajustar(kpis));
        col.add(Box.createVerticalStrut(14));

        JPanel g1 = Textos.transparente(new GridLayout(1, 2, 14, 0));
        g1.add(conGrafico("Ventas por día", "Total confirmado, en soles", porDia, 230));
        g1.add(conGrafico("Por medio de pago", "Cada venta se paga con un solo medio (RN-09)", porMedio, 230));
        col.add(ajustar(g1));
        col.add(Box.createVerticalStrut(14));
        JPanel g2 = Textos.transparente(new GridLayout(1, 2, 14, 0));
        g2.add(conGrafico("Productos más vendidos", "Top 6 por importe", porProducto, 210));
        g2.add(conGrafico("Ventas por cajero", "Total confirmado", porCajero, 210));
        col.add(ajustar(g2));

        JScrollPane sp = Tablas.desplazable(col);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getViewport().setBackground(Tema.FONDO);
        add(sp, BorderLayout.CENTER);
        periodo.seleccionar(7);
        periodo.alCambiar(d -> cargar());
    }

    private static JComponent ajustar(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        return c;
    }

    private JComponent conGrafico(String titulo, String sub, Grafico g, int alto) {
        Tarjeta t = new Tarjeta(titulo, sub);
        g.setPreferredSize(new Dimension(300, alto));
        t.con(g);
        return t;
    }

    @Override
    public void alMostrar() {
        cargar();
    }

    private void cargar() {
        int dias = periodo.getSeleccionado();
        LocalDate hasta = LocalDate.now();
        LocalDate desde = hasta.minusDays(dias - 1);
        int h = LocalTime.now().getHour();
        saludo.setText((h < 12 ? "Buenos días" : h < 19 ? "Buenas tardes" : "Buenas noches") + ", "
                + ctx.getUsuario().getNombre().split(" ")[0]);
        rango.setText(dias == 1 ? "Ventas de hoy, " + hasta.format(Tema.FECHA)
                : "Del " + desde.format(Tema.FECHA) + " al " + hasta.format(Tema.FECHA));
        ResumenVentas r = ctx.consulta.resumen(desde, hasta);
        ventas.setValor(Tema.soles(r.total()), r.cantidadVentas() + " ventas");
        ticket.setValor(Tema.soles(r.ticketPromedio()), "por venta confirmada");
        anuladas.setValor(String.valueOf(r.anuladas()), Tema.soles(r.totalAnulado()) + " devueltos");
        int pend = ctx.comprobante.listarPendientes().size();
        pendientes.setValor(String.valueOf(pend), pend == 0 ? "Todo al día" : "Pendientes o rechazados");
        pendientes.setColorValor(pend > 0 ? Tema.AMBAR : Tema.TEXTO);

        Map<String, Double> dia = new LinkedHashMap<>();
        LocalDate desdeDias = dias == 1 ? hasta.minusDays(6) : desde;
        ResumenVentas rd = dias == 1 ? ctx.consulta.resumen(desdeDias, hasta) : r;
        Locale es = new Locale("es", "PE");
        String hoy = null;
        for (Map.Entry<LocalDate, Double> e : rd.porDia().entrySet()) {
            String k = dias > 7 ? e.getKey().format(java.time.format.DateTimeFormatter.ofPattern("d/M"))
                    : e.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT, es) + " " + e.getKey().getDayOfMonth();
            dia.put(k, e.getValue());
            if (e.getKey().equals(hasta)) {
                hoy = k;
            }
        }
        porDia.setDatos(dia, hoy);
        Map<String, Double> medio = new LinkedHashMap<>();
        for (MedioPago m : MedioPago.values()) {
            medio.put(m.getEtiqueta(), Math.round(r.porMedio().get(m) * 100) / 100.0);
        }
        porMedio.setDatos(medio, null);
        Map<String, Double> top = new LinkedHashMap<>();
        ctx.consulta.reportePorProducto(desde, hasta).entrySet().stream().limit(6).forEach(e -> top.put(e.getKey(), e.getValue()));
        porProducto.setDatos(top, null);
        porCajero.setDatos(ctx.consulta.reportePorCajero(desde, hasta), null);
        revalidate();
    }
}
