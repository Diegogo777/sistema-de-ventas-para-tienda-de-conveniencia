package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.modelo.Caja;
import com.mycompany.tienda3a.modelo.Cajero;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.function.Consumer;
import javax.swing.*;

class PanelSeleccionCaja extends JPanel {
    PanelSeleccionCaja(Contexto ctx, Consumer<Caja> alElegir, Runnable alSalir) {
        super(new GridBagLayout());
        setBackground(Tema.FONDO);
        Cajero cajero = (Cajero) ctx.getUsuario();
        JPanel col = Textos.transparente(null);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        Logo logo = new Logo(40, true, Tema.TEXTO);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        col.add(logo);
        col.add(Box.createVerticalStrut(30));
        JLabel t = Textos.titulo("Hola, " + cajero.getNombre().split(" ")[0] + ". ¿En qué caja trabajarás?");
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        col.add(t);
        JLabel a = Textos.ayuda("Tus ventas usarán las series de boleta y factura de la caja que elijas.");
        a.setAlignmentX(Component.CENTER_ALIGNMENT);
        col.add(a);
        col.add(Box.createVerticalStrut(28));
        JPanel fila = Textos.transparente(new GridLayout(1, 0, 18, 0));
        for (Caja caja : ctx.caja.listarCajas()) {
            boolean cerrada = ctx.caja.cierreDelDia(cajero, caja, LocalDate.now()) != null;
            fila.add(new TarjetaCaja(caja, cerrada, () -> alElegir.accept(caja)));
        }
        fila.setAlignmentX(Component.CENTER_ALIGNMENT);
        col.add(fila);
        col.add(Box.createVerticalStrut(26));
        Boton salir = new Boton("Cerrar sesión", Boton.Estilo.FANTASMA, Icono.Tipo.SALIR);
        salir.setAlignmentX(Component.CENTER_ALIGNMENT);
        salir.addActionListener(e -> alSalir.run());
        col.add(salir);
        add(col);
    }

    private static class TarjetaCaja extends JPanel {
        private boolean encima;

        TarjetaCaja(Caja caja, boolean cerrada, Runnable accion) {
            super(new BorderLayout(0, 14));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(24, 24, 22, 24));
            setPreferredSize(new Dimension(230, 200));
            JLabel ic = new JLabel(new Icono(Icono.Tipo.CIERRE, 34, cerrada ? Tema.TEXTO_TENUE : Tema.PETROLEO));
            ic.setHorizontalAlignment(SwingConstants.LEFT);
            add(ic, BorderLayout.NORTH);
            JPanel txt = Textos.transparente(null);
            txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
            JLabel cod = new JLabel(caja.getCodigo());
            cod.setFont(Tema.negrita(20));
            cod.setForeground(cerrada ? Tema.TEXTO_TENUE : Tema.TEXTO);
            txt.add(cod);
            JLabel ser = Textos.texto("Boletas " + caja.getSerieBoleta() + " · Facturas " + caja.getSerieFactura(), 12.5f, Tema.TEXTO_SUAVE);
            txt.add(ser);
            add(txt, BorderLayout.CENTER);
            Insignia est = new Insignia(cerrada ? "Ya la cerraste hoy" : "Disponible",
                    cerrada ? Insignia.Tipo.NEUTRO : Insignia.Tipo.EXITO);
            JPanel pie = Textos.transparente(new FlowLayout(FlowLayout.LEFT, 0, 0));
            pie.add(est);
            add(pie, BorderLayout.SOUTH);
            if (!cerrada) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        encima = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        encima = false;
                        repaint();
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        accion.run();
                    }
                });
            } else {
                setToolTipText("Ya registraste el cierre de esta caja hoy.");
            }
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D r = new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
            g.setColor(encima ? Tema.PETROLEO_SUAVE : Tema.SUPERFICIE);
            g.fill(r);
            g.setStroke(new BasicStroke(encima ? 2f : 1f));
            g.setColor(encima ? Tema.PETROLEO : Tema.BORDE);
            g.draw(r);
            g.dispose();
        }
    }
}
