package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.config.Configuracion;
import com.mycompany.tienda3a.modelo.Comprobante;
import com.mycompany.tienda3a.modelo.EstadoComprobante;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class PanelComprobantes extends PanelBase {
    private final JTable tabla = Tablas.crear("Comprobante", "Tipo", "Emitido", "Total", "Envíos", "Estado", "Mensaje del simulador");
    private final List<Comprobante> lista = new ArrayList<>();
    private final Indicador pendientes = new Indicador("Pendientes", Icono.Tipo.RELOJ, Tema.AMBAR);
    private final Indicador rechazados = new Indicador("Rechazados", Icono.Tipo.ALERTA, Tema.ROJO);
    private final Indicador regla = new Indicador("Reintento automático", Icono.Tipo.ENVIAR, Tema.PETROLEO);
    private final JTextArea observacion = new JTextArea(3, 20);
    private final JLabel seleccion = Textos.ayuda("Selecciona un comprobante de la lista.");

    PanelComprobantes(Contexto ctx) {
        super(ctx, "Comprobantes", "Comprobantes que el simulador de SUNAT no aceptó: revísalos y reenvíalos.",
                Icono.Tipo.COMPROBANTE, "CU-13");
        JPanel col = Textos.transparente(new BorderLayout(0, 14));
        JPanel kpis = Textos.transparente(new GridLayout(1, 3, 14, 0));
        kpis.add(pendientes);
        kpis.add(rechazados);
        kpis.add(regla);
        col.add(kpis, BorderLayout.NORTH);
        JPanel cuerpo = Textos.transparente(new BorderLayout(16, 0));
        Tarjeta t = new Tarjeta();
        Tablas.ancho(tabla, 0, 134, true);
        Tablas.ancho(tabla, 1, 104, true);
        Tablas.ancho(tabla, 2, 122, true);
        Tablas.ancho(tabla, 3, 70, true);
        Tablas.ancho(tabla, 4, 64, true);
        Tablas.ancho(tabla, 5, 116, true);
        Tablas.ancho(tabla, 6, 200, false);
        Tablas.cifra(tabla, 3, 4);
        Tablas.insignia(tabla, 5, v -> "Rechazado".equals(v) ? Insignia.Tipo.PELIGRO
                : "Pendiente".equals(v) ? Insignia.Tipo.ADVERTENCIA : Insignia.Tipo.EXITO);
        t.con(Tablas.envolver(tabla));
        cuerpo.add(t, BorderLayout.CENTER);
        cuerpo.add(acciones(), BorderLayout.EAST);
        col.add(cuerpo, BorderLayout.CENTER);
        add(col, BorderLayout.CENTER);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            Comprobante c = actual();
            seleccion.setText(c == null ? "Selecciona un comprobante de la lista." : c.getNumero() + " · "
                    + Tema.soles(c.getVenta().getTotal()) + " · " + c.getEstado().getEtiqueta());
            if (c != null) {
                observacion.setText(c.getObservacionRevision() == null ? "" : c.getObservacionRevision());
            }
        });
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && actual() != null) {
                    Dialogos.mostrarContenido(PanelComprobantes.this, actual().getNumero(), Dialogos.ticket(actual().generarImpresion()));
                }
            }
        });
    }

    private JComponent acciones() {
        Tarjeta t = new Tarjeta("Revisión", null);
        JPanel p = Textos.transparente(null);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(fila(seleccion));
        p.add(Box.createVerticalStrut(14));
        Boton reenviar = new Boton("Reenviar al simulador", Boton.Estilo.PRIMARIO, Icono.Tipo.ENVIAR);
        reenviar.addActionListener(e -> reenviar());
        p.add(fila(reenviar));
        p.add(Box.createVerticalStrut(8));
        Boton ver = new Boton("Ver comprobante", Boton.Estilo.SECUNDARIO, Icono.Tipo.OJO);
        ver.addActionListener(e -> {
            if (actual() != null) {
                Dialogos.mostrarContenido(this, actual().getNumero(), Dialogos.ticket(actual().generarImpresion()));
            }
        });
        p.add(fila(ver));
        p.add(Box.createVerticalStrut(18));
        observacion.setFont(Tema.texto(13));
        observacion.setLineWrap(true);
        observacion.setWrapStyleWord(true);
        observacion.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane so = Tablas.desplazable(observacion);
        so.setBorder(BorderFactory.createLineBorder(Tema.BORDE_FUERTE));
        p.add(fila(Textos.conEtiqueta("Observación de la revisión", so)));
        p.add(Box.createVerticalStrut(10));
        Boton guardar = new Boton("Registrar revisión", Boton.Estilo.SECUNDARIO, Icono.Tipo.CHECK);
        guardar.addActionListener(e -> revisar());
        p.add(fila(guardar));
        t.con(p);
        return Textos.ancho(t, 300);
    }

    private static JComponent fila(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        return c;
    }

    private Comprobante actual() {
        int f = tabla.getSelectedRow();
        return f < 0 || f >= lista.size() ? null : lista.get(f);
    }

    @Override
    public void alMostrar() {
        cargar();
    }

    private void cargar() {
        Comprobante antes = actual();
        lista.clear();
        lista.addAll(ctx.comprobante.listarPendientes());
        var m = Tablas.modelo(tabla);
        m.setRowCount(0);
        int p = 0;
        int r = 0;
        for (Comprobante c : lista) {
            if (c.getEstado() == EstadoComprobante.PENDIENTE) {
                p++;
            } else {
                r++;
            }
            m.addRow(new Object[]{c.getNumero(), c.getTipo().getEtiqueta(), c.getFechaEmision().format(Tema.FECHA_HORA),
                    Tema.numero(c.getVenta().getTotal()), c.getIntentosEnvio() + "/" + Configuracion.getMaxIntentosEnvio(),
                    c.getEstado().getEtiqueta(), c.getObservacionSunat() == null ? "" : c.getObservacionSunat()});
        }
        pendientes.setValor(String.valueOf(p), "sin respuesta del simulador");
        rechazados.setValor(String.valueOf(r), "revisar datos del cliente");
        regla.setValor("cada " + Configuracion.getMinutosReintentoEnvio() + " min", "hasta " + Configuracion.getMaxIntentosEnvio() + " intentos (RN-15)");
        if (lista.isEmpty()) {
            aviso.mostrar(Aviso.Tipo.EXITO, "No hay comprobantes pendientes ni rechazados.");
        }
        int i = antes == null ? -1 : lista.indexOf(antes);
        if (i >= 0) {
            tabla.setRowSelectionInterval(i, i);
        }
    }

    private void reenviar() {
        Comprobante c = actual();
        if (c == null) {
            aviso.mostrar(Aviso.Tipo.ADVERTENCIA, "Selecciona un comprobante de la lista.");
            return;
        }
        if (ejecutar(() -> ctx.comprobante.reenviar(c, ctx.getUsuario()))) {
            cargar();
            aviso.mostrar(c.getEstado() == EstadoComprobante.ACEPTADO ? Aviso.Tipo.EXITO : Aviso.Tipo.ADVERTENCIA,
                    c.getNumero() + ": " + c.getObservacionSunat());
        }
    }

    private void revisar() {
        Comprobante c = actual();
        if (c == null) {
            aviso.mostrar(Aviso.Tipo.ADVERTENCIA, "Selecciona un comprobante de la lista.");
            return;
        }
        if (ejecutar(() -> ctx.comprobante.registrarRevision(c, observacion.getText(), ctx.getUsuario()))) {
            exito("Revisión registrada para " + c.getNumero() + ".");
        }
    }
}
