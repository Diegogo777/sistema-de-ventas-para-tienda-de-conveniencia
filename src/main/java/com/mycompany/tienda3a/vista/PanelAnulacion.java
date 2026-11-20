package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class PanelAnulacion extends PanelBase {
    private final Campo numero = new Campo("Ej.: B001-00000012");
    private final JTable tabla = Tablas.crear("Comprobante", "Fecha y hora", "Cliente", "Pago", "Total", "Estado");
    private final List<Venta> ventas = new ArrayList<>();
    private Venta seleccionada;

    private final JLabel detTitulo = new JLabel("Elige una venta");
    private final JLabel detInfo = Textos.ayuda("Busca el comprobante o selecciónalo en la lista.");
    private final JTable detLineas = Tablas.crear("Producto", "Cant.", "Importe");
    private final JLabel detTotal = Textos.monto("", 20, Tema.TEXTO);
    private final JTextArea motivo = new JTextArea(3, 20);
    private final JCheckBox buenEstado = new JCheckBox("El cliente devolvió todos los productos en buen estado");
    private final Boton anular = new Boton("Anular venta", Boton.Estilo.PELIGRO, Icono.Tipo.ANULAR);

    PanelAnulacion(Contexto ctx) {
        super(ctx, "Anular venta", "Anula una venta confirmada: reingresa el stock, revierte el pago y anula el comprobante.",
                Icono.Tipo.ANULAR, "CU-08");
        JPanel centro = Textos.transparente(new BorderLayout(16, 0));

        Tarjeta lista = new Tarjeta("Ventas de tu caja", "Las más recientes primero. Doble clic para ver el comprobante.");
        JPanel busca = Textos.transparente(new BorderLayout(10, 0));
        numero.conIcono(Icono.Tipo.COMPROBANTE);
        busca.add(numero, BorderLayout.CENTER);
        Boton b = new Boton("Buscar", Boton.Estilo.SECUNDARIO, Icono.Tipo.BUSCAR);
        b.addActionListener(e -> buscar());
        numero.addActionListener(e -> buscar());
        busca.add(b, BorderLayout.EAST);
        busca.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        lista.cuerpo().add(busca, BorderLayout.NORTH);
        Tablas.ancho(tabla, 0, 124, true);
        Tablas.ancho(tabla, 1, 128, true);
        Tablas.ancho(tabla, 2, 150, false);
        Tablas.ancho(tabla, 3, 76, true);
        Tablas.ancho(tabla, 4, 72, true);
        Tablas.ancho(tabla, 5, 124, true);
        Tablas.cifra(tabla, 4);
        Tablas.insignia(tabla, 5, v -> "Anulada".equals(v) ? Insignia.Tipo.PELIGRO : Insignia.Tipo.EXITO);
        lista.con(Tablas.envolver(tabla));
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                mostrar(ventas.get(tabla.getSelectedRow()));
            }
        });
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && seleccionada != null && seleccionada.getComprobante() != null) {
                    Dialogos.mostrarContenido(PanelAnulacion.this, seleccionada.getComprobante().getNumero(),
                            Dialogos.ticket(seleccionada.getComprobante().generarImpresion()));
                }
            }
        });
        centro.add(lista, BorderLayout.CENTER);
        centro.add(panelDetalle(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);
    }

    private JComponent panelDetalle() {
        Tarjeta t = new Tarjeta();
        t.setPreferredSize(new Dimension(380, 0));
        JPanel p = Textos.transparente(new BorderLayout(0, 12));
        JPanel cab = Textos.transparente(new GridLayout(2, 1, 0, 2));
        detTitulo.setFont(Tema.negrita(17));
        cab.add(detTitulo);
        cab.add(detInfo);
        p.add(cab, BorderLayout.NORTH);
        detLineas.setRowHeight(32);
        Tablas.ancho(detLineas, 1, 92, true);
        Tablas.ancho(detLineas, 2, 86, true);
        Tablas.cifra(detLineas, 1, 2);
        JPanel medio = Textos.transparente(new BorderLayout(0, 8));
        medio.add(Tablas.envolver(detLineas), BorderLayout.CENTER);
        JPanel tot = Textos.transparente(new BorderLayout());
        tot.add(Textos.etiqueta("Total a devolver"), BorderLayout.WEST);
        tot.add(detTotal, BorderLayout.EAST);
        medio.add(tot, BorderLayout.SOUTH);
        p.add(medio, BorderLayout.CENTER);

        JPanel form = Textos.transparente(new BorderLayout(0, 10));
        motivo.setFont(Tema.texto(13));
        motivo.setLineWrap(true);
        motivo.setWrapStyleWord(true);
        motivo.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane sm = Tablas.desplazable(motivo);
        sm.setBorder(BorderFactory.createLineBorder(Tema.BORDE_FUERTE));
        form.add(Textos.conEtiqueta("Motivo de la anulación (obligatorio)", sm), BorderLayout.NORTH);
        buenEstado.setFont(Tema.texto(12.5f));
        buenEstado.setForeground(Tema.TEXTO);
        buenEstado.setOpaque(false);
        form.add(buenEstado, BorderLayout.CENTER);
        anular.addActionListener(e -> anular());
        form.add(anular, BorderLayout.SOUTH);
        p.add(form, BorderLayout.SOUTH);
        t.con(p);
        habilitar(false);
        return t;
    }

    @Override
    public void alMostrar() {
        cargar();
    }

    private void cargar() {
        ventas.clear();
        ventas.addAll(ctx.venta.ventasRecientes(ctx.getCaja(), 60));
        var m = Tablas.modelo(tabla);
        m.setRowCount(0);
        for (Venta v : ventas) {
            m.addRow(new Object[]{v.getComprobante() == null ? "—" : v.getComprobante().getNumero(),
                    v.getFechaHora().format(Tema.FECHA_HORA),
                    v.getCliente() == null ? "Sin datos" : v.getCliente().getNombreRazonSocial(),
                    v.getPago() == null ? "" : v.getPago().getMedio().getEtiqueta().replace(" digital", ""),
                    Tema.numero(v.getTotal()), v.getEstado().getEtiqueta()});
        }
    }

    private void buscar() {
        ejecutar(() -> {
            Venta v = ctx.venta.buscarVentaParaAnular(numero.getText());
            int i = ventas.indexOf(v);
            if (i >= 0) {
                tabla.setRowSelectionInterval(i, i);
                tabla.scrollRectToVisible(tabla.getCellRect(i, 0, true));
            } else {
                mostrar(v);
            }
        });
    }

    private void mostrar(Venta v) {
        seleccionada = v;
        aviso.limpiar();
        detTitulo.setText(v.getComprobante() == null ? "Venta " + v.getIdVenta() : v.getComprobante().getTipo().getEtiqueta()
                + " " + v.getComprobante().getNumero());
        detInfo.setText(v.getFechaHora().format(Tema.FECHA_HORA) + " · " + v.getCaja().getCodigo() + " · "
                + v.getCajero().getNombre() + (v.getPago() == null ? "" : " · " + v.getPago().getMedio().getEtiqueta()));
        var m = Tablas.modelo(detLineas);
        m.setRowCount(0);
        for (DetalleVenta d : v.getDetalles()) {
            m.addRow(new Object[]{d.getProducto().getNombre(), d.getProducto().formatearCantidad(d.getCantidad()),
                    Tema.numero(d.getImporte())});
        }
        detTotal.setText(Tema.soles(v.getTotal()));
        boolean anulable = v.getEstado() == EstadoVenta.CONFIRMADA;
        habilitar(anulable);
        if (!anulable) {
            aviso.mostrar(Aviso.Tipo.INFO, "Esta venta ya fue anulada el " + v.getFechaAnulacion().format(Tema.FECHA_HORA)
                    + ". Motivo: " + v.getMotivoAnulacion());
        }
    }

    private void habilitar(boolean b) {
        motivo.setEnabled(b);
        motivo.setBackground(b ? Tema.SUPERFICIE : Tema.FONDO);
        buenEstado.setEnabled(b);
        anular.setEnabled(b);
        if (!b) {
            motivo.setText("");
            buenEstado.setSelected(false);
        }
    }

    private void anular() {
        if (seleccionada == null) {
            return;
        }
        if (!buenEstado.isSelected()) {
            aviso.mostrar(Aviso.Tipo.ERROR, "Solo se anula si el cliente devolvió todos los productos en buen estado (RN-07). "
                    + "Revisa los productos y marca la casilla.");
            return;
        }
        String pagoTxt = seleccionada.getPago() instanceof PagoEfectivo
                ? "Devuelve " + Tema.soles(seleccionada.getTotal()) + " en efectivo al cliente."
                : "La devolución se registra en el mismo medio (" + seleccionada.getPago().getMedio().getEtiqueta().toLowerCase() + ").";
        String titulo = detTitulo.getText();
        if (!Dialogos.confirmar(this, "¿Anular la " + Character.toLowerCase(titulo.charAt(0)) + titulo.substring(1) + "?",
                "Se reingresará el stock de " + seleccionada.getDetalles().size() + " productos, se revertirá el pago y se "
                + "anulará el comprobante. " + pagoTxt + " Esta acción no se puede deshacer.", "Sí, anular", Boton.Estilo.PELIGRO)) {
            return;
        }
        Venta v = seleccionada;
        if (ejecutar(() -> ctx.venta.anularVenta(v, motivo.getText(), (Cajero) ctx.getUsuario()))) {
            cargar();
            int i = ventas.indexOf(v);
            if (i >= 0) {
                tabla.setRowSelectionInterval(i, i);
            }
            exito("Venta anulada. Stock reingresado y pago revertido. " + pagoTxt);
        }
    }
}
