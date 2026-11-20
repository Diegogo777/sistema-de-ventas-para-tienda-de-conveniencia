package com.mycompany.tienda3a.vista.componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Aviso extends JPanel {
    public enum Tipo { EXITO, ERROR, ADVERTENCIA, INFO }

    private final JLabel texto = new JLabel();
    private Color fondo = Tema.FONDO;
    private Color borde = Tema.BORDE;
    private Timer ocultar;

    public Aviso() {
        super(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
        texto.setFont(Tema.medio(13));
        texto.setIconTextGap(10);
        add(texto);
        setVisible(false);
    }

    public void mostrar(Tipo tipo, String mensaje) {
        Color c;
        Icono.Tipo i;
        switch (tipo) {
            case EXITO -> { c = Tema.VERDE; fondo = Tema.VERDE_SUAVE; i = Icono.Tipo.CHECK; }
            case ERROR -> { c = Tema.ROJO; fondo = Tema.ROJO_SUAVE; i = Icono.Tipo.ALERTA; }
            case ADVERTENCIA -> { c = Tema.AMBAR; fondo = Tema.AMBAR_SUAVE; i = Icono.Tipo.ALERTA; }
            default -> { c = Tema.PETROLEO; fondo = Tema.PETROLEO_SUAVE; i = Icono.Tipo.INFO; }
        }
        borde = Tema.tinte(c, 0.35);
        texto.setForeground(tipo == Tipo.INFO ? Tema.PETROLEO_OSCURO : c.darker());
        texto.setIcon(new Icono(i, 18, c));
        texto.setText("<html>" + mensaje.replace("&", "&amp;").replace("<", "&lt;") + "</html>");
        setVisible(true);
        revalidate();
        repaint();
        if (ocultar != null) {
            ocultar.stop();
        }
        if (tipo == Tipo.EXITO || tipo == Tipo.INFO) {
            ocultar = new Timer(7000, e -> setVisible(false));
            ocultar.setRepeats(false);
            ocultar.start();
        }
    }

    public void limpiar() {
        setVisible(false);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(fondo);
        g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
        g.setColor(borde);
        g.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 2, getHeight() - 2, 10, 10));
        g.dispose();
    }
}
