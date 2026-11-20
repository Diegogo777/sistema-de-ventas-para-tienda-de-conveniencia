package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

class PanelReportes extends PanelBase {
    private final Selector<String> agrupar = new Selector<>(List.of("producto", "caja", "cajero"),
            List.of("Por producto", "Por caja", "Por cajero"), List.of(Icono.Tipo.PRODUCTO, Icono.Tipo.CIERRE, Icono.Tipo.USUARIO));
    private final Campo desde = new Campo("dd/mm/aaaa");
    private final Campo hasta = new Campo("dd/mm/aaaa");
    private final JTable tabla = Tablas.crear("Concepto", "Cantidad", "Total", "% del total");
    private final Grafico grafico = new Grafico(true, Tema.PETROLEO);
    private final JLabel pie = Textos.ayuda("");

    PanelReportes(Contexto ctx) {
        super(ctx, "Reportes", "Ventas confirmadas agrupadas por producto, caja o cajero.", Icono.Tipo.REPORTE, "CU-10");
        JPanel col = Textos.transparente(new BorderLayout(0, 14));
        JPanel filtros = Textos.transparente(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.SOUTH;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 0, 10);
        agrupar.setPreferredSize(new Dimension(470, 46));
        filtros.add(agrupar, g);
        g.weightx = 1;
        filtros.add(Box.createHorizontalGlue(), g);
        g.weightx = 0;
        desde.setPreferredSize(new Dimension(130, 40));
        hasta.setPreferredSize(new Dimension(130, 40));
        filtros.add(Textos.conEtiqueta("Desde", desde), g);
        filtros.add(Textos.conEtiqueta("Hasta", hasta), g);
        g.insets = new Insets(0, 0, 0, 0);
        Boton generar = new Boton("Generar", Boton.Estilo.PRIMARIO, Icono.Tipo.REPORTE);
        generar.addActionListener(e -> cargar());
        filtros.add(generar, g);
        col.add(filtros, BorderLayout.NORTH);

        JPanel cuerpo = Textos.transparente(new GridLayout(1, 2, 16, 0));
        Tarjeta t = new Tarjeta();
        Tablas.ancho(tabla, 1, 100, true);
        Tablas.ancho(tabla, 2, 120, true);
        Tablas.ancho(tabla, 3, 100, true);
        Tablas.cifra(tabla, 1, 2, 3);
        t.con(Tablas.envolver(tabla));
        pie.setBorder(BorderFactory.createEmptyBorder(10, 2, 0, 0));
        t.cuerpo().add(pie, BorderLayout.SOUTH);
        cuerpo.add(t);
        Tarjeta tg = new Tarjeta("Los 8 primeros", null);
        tg.con(grafico);
        JPanel gEnv = Textos.transparente(new BorderLayout());
        gEnv.add(tg, BorderLayout.NORTH);
        cuerpo.add(gEnv);
        col.add(cuerpo, BorderLayout.CENTER);
        add(col, BorderLayout.CENTER);
        desde.setText(Fechas.texto(LocalDate.now().minusDays(6)));
        hasta.setText(Fechas.texto(LocalDate.now()));
        agrupar.alCambiar(a -> cargar());
    }

    @Override
    public void alMostrar() {
        cargar();
    }

    private void cargar() {
        aviso.limpiar();
        ejecutar(() -> {
            LocalDate d = Fechas.leer(desde, "inicial");
            LocalDate h = Fechas.leer(hasta, "final");
            String a = agrupar.getSeleccionado();
            Map<String, Double> datos = switch (a) {
                case "caja" -> ctx.consulta.reportePorCaja(d, h);
                case "cajero" -> ctx.consulta.reportePorCajero(d, h);
                default -> ctx.consulta.reportePorProducto(d, h);
            };
            Map<String, Double> cantidades = a.equals("producto") ? ctx.consulta.cantidadesPorProducto(d, h) : null;
            double total = 0;
            for (double v : datos.values()) {
                total += v;
            }
            var m = Tablas.modelo(tabla);
            m.setRowCount(0);
            Map<String, Double> top = new LinkedHashMap<>();
            for (Map.Entry<String, Double> e : datos.entrySet()) {
                String cant = cantidades == null ? "—" : formatear(cantidades.get(e.getKey()));
                m.addRow(new Object[]{e.getKey(), cant, Tema.numero(e.getValue()),
                        String.format("%.1f %%", total == 0 ? 0 : e.getValue() * 100 / total)});
                if (top.size() < 8) {
                    top.put(e.getKey(), e.getValue());
                }
            }
            grafico.setDatos(top, null);
            grafico.revalidate();
            pie.setText("<html><b>Total del periodo: " + Tema.soles(total) + "</b> · Es igual al total de ventas confirmadas (RF-17).</html>");
            revalidate();
        });
    }

    private static String formatear(Double c) {
        if (c == null) {
            return "";
        }
        return c == Math.floor(c) ? String.format("%.0f", c) : String.format("%.3f", c);
    }
}
