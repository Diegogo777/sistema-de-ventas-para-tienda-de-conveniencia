package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class PanelVentas extends PanelBase {
    private final Campo desde = new Campo("dd/mm/aaaa");
    private final Campo hasta = new Campo("dd/mm/aaaa");
    private final Combo<Caja> caja;
    private final Combo<Usuario> cajero;
    private final Campo numero = new Campo("B001-00000012");
    private final JTable tabla = Tablas.crear("Fecha y hora", "Comprobante", "Caja", "Cajero", "Cliente", "Pago", "Total", "Estado");
    private final List<Venta> resultado = new ArrayList<>();
    private final JLabel resumen = Textos.ayuda("");

    PanelVentas(Contexto ctx) {
        super(ctx, "Ventas", "Consulta las ventas por periodo, caja, cajero o comprobante.", Icono.Tipo.VENTAS, "CU-10");
        List<Caja> cajas = new ArrayList<>();
        cajas.add(null);
        cajas.addAll(ctx.caja.listarCajas());
        caja = new Combo<>(cajas, c -> c == null ? "Todas las cajas" : c.getCodigo());
        List<Usuario> cajeros = new ArrayList<>();
        cajeros.add(null);
        for (Usuario u : ctx.usuarios.listarUsuarios()) {
            if (u instanceof Cajero) {
                cajeros.add(u);
            }
        }
        cajero = new Combo<>(cajeros, u -> u == null ? "Todos los cajeros" : u.getNombre());

        Tarjeta t = new Tarjeta();
        JPanel filtros = Textos.transparente(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.SOUTH;
        g.insets = new Insets(0, 0, 0, 10);
        g.weightx = 0.8;
        filtros.add(Textos.conEtiqueta("Desde", desde), g);
        filtros.add(Textos.conEtiqueta("Hasta", hasta), g);
        g.weightx = 1;
        filtros.add(Textos.conEtiqueta("Caja", caja), g);
        filtros.add(Textos.conEtiqueta("Cajero", cajero), g);
        filtros.add(Textos.conEtiqueta("N° de comprobante", numero), g);
        g.weightx = 0;
        Boton buscar = new Boton("Buscar", Boton.Estilo.PRIMARIO, Icono.Tipo.BUSCAR);
        buscar.addActionListener(e -> cargar());
        filtros.add(buscar, g);
        g.insets = new Insets(0, 0, 0, 0);
        Boton limpiar = new Boton("Limpiar", Boton.Estilo.FANTASMA);
        limpiar.addActionListener(e -> {
            valoresIniciales();
            cargar();
        });
        filtros.add(limpiar, g);
        filtros.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        t.cuerpo().add(filtros, BorderLayout.NORTH);
        Tablas.ancho(tabla, 0, 132, true);
        Tablas.ancho(tabla, 1, 132, true);
        Tablas.ancho(tabla, 2, 80, true);
        Tablas.ancho(tabla, 5, 96, true);
        Tablas.ancho(tabla, 6, 96, true);
        Tablas.ancho(tabla, 7, 126, true);
        Tablas.cifra(tabla, 6);
        Tablas.insignia(tabla, 7, v -> "Anulada".equals(v) ? Insignia.Tipo.PELIGRO : Insignia.Tipo.EXITO);
        t.con(Tablas.envolver(tabla));
        resumen.setBorder(BorderFactory.createEmptyBorder(10, 2, 0, 0));
        t.cuerpo().add(resumen, BorderLayout.SOUTH);
        add(t, BorderLayout.CENTER);
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int f = tabla.getSelectedRow();
                if (e.getClickCount() == 2 && f >= 0) {
                    verDetalle(resultado.get(f));
                }
            }
        });
        numero.addActionListener(e -> cargar());
        valoresIniciales();
    }

    private void valoresIniciales() {
        desde.setText(Fechas.texto(LocalDate.now().minusDays(6)));
        hasta.setText(Fechas.texto(LocalDate.now()));
        caja.setSelectedIndex(0);
        cajero.setSelectedIndex(0);
        numero.setText("");
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
            resultado.clear();
            resultado.addAll(ctx.consulta.consultarVentas(d, h, caja.seleccionado(),
                    (Cajero) cajero.seleccionado(), numero.getText()));
            var m = Tablas.modelo(tabla);
            m.setRowCount(0);
            double total = 0;
            int conf = 0;
            for (Venta v : resultado) {
                m.addRow(new Object[]{v.getFechaHora().format(Tema.FECHA_HORA),
                        v.getComprobante() == null ? "—" : v.getComprobante().getNumero(), v.getCaja().getCodigo(),
                        v.getCajero().getNombre(), v.getCliente() == null ? "Sin datos" : v.getCliente().getNombreRazonSocial(),
                        v.getPago() == null ? "" : v.getPago().getMedio().getEtiqueta().replace(" digital", ""),
                        Tema.numero(v.getTotal()), v.getEstado().getEtiqueta()});
                if (v.getEstado() == EstadoVenta.CONFIRMADA) {
                    total += v.getTotal();
                    conf++;
                }
            }
            resumen.setText("<html>" + resultado.size() + " ventas · <b>" + conf + " confirmadas por " + Tema.soles(total)
                    + "</b> (el total excluye las anuladas) · Doble clic para ver el detalle</html>");
        });
    }

    private void verDetalle(Venta v) {
        if (v.getComprobante() == null) {
            return;
        }
        JPanel p = Textos.transparente(new BorderLayout(0, 10));
        JPanel ins = Textos.transparente(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ins.add(new Insignia(v.getEstado().getEtiqueta(), v.getEstado() == EstadoVenta.ANULADA ? Insignia.Tipo.PELIGRO : Insignia.Tipo.EXITO));
        ins.add(Box.createHorizontalStrut(8));
        ins.add(new Insignia("SUNAT: " + v.getComprobante().getEstado().getEtiqueta(), Insignia.Tipo.MARCA));
        p.add(ins, BorderLayout.NORTH);
        p.add(Dialogos.ticket(v.getComprobante().generarImpresion()), BorderLayout.CENTER);
        if (v.getEstado() == EstadoVenta.ANULADA) {
            p.add(Textos.texto("<html><div style='width:400px'>Anulada el " + v.getFechaAnulacion().format(Tema.FECHA_HORA)
                    + " por " + v.getCajeroAnulacion().getNombre() + ". Motivo: " + v.getMotivoAnulacion() + "</div></html>",
                    12, Tema.ROJO), BorderLayout.SOUTH);
        }
        Dialogos.mostrarContenido(this, "Venta · " + v.getComprobante().getNumero(), p);
    }
}
