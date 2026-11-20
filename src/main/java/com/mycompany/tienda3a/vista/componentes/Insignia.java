package com.mycompany.tienda3a.vista.componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class Insignia extends JLabel {
    public enum Tipo { EXITO, ADVERTENCIA, PELIGRO, NEUTRO, MARCA, PROMO }

    private Color fondo;

    public Insignia(String texto, Tipo tipo) {
        super(texto);
        setFont(Tema.medio(11.5f));
        setBorder(BorderFactory.createEmptyBorder(3, 9, 3, 10));
        setIconTextGap(5);
        setTipo(tipo);
    }

    public final void setTipo(Tipo tipo) {
        Color texto;
        Icono.Tipo icono;
        switch (tipo) {
            case EXITO -> { texto = Tema.VERDE_OSCURO; fondo = Tema.VERDE_SUAVE; icono = Icono.Tipo.CHECK; }
            case ADVERTENCIA -> { texto = Tema.AMBAR_OSCURO; fondo = Tema.AMBAR_SUAVE; icono = Icono.Tipo.RELOJ; }
            case PELIGRO -> { texto = Tema.ROJO_OSCURO; fondo = Tema.ROJO_SUAVE; icono = Icono.Tipo.ALERTA; }
            case MARCA -> { texto = Tema.PETROLEO; fondo = Tema.PETROLEO_SUAVE; icono = null; }
            case PROMO -> { texto = Tema.AMARILLO_TEXTO; fondo = Tema.AMARILLO_SUAVE; icono = Icono.Tipo.PROMO; }
            default -> { texto = Tema.TEXTO_SUAVE; fondo = Tema.FONDO; icono = null; }
        }
        setForeground(texto);
        setIcon(icono == null ? null : new Icono(icono, 13, texto));
        repaint();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension d = getPreferredSize();
        int y = (getHeight() - d.height) / 2;
        g.setColor(fondo);
        g.fill(new RoundRectangle2D.Double(0, y, Math.min(getWidth(), d.width), d.height, d.height, d.height));
        g.dispose();
        super.paintComponent(g0);
    }
}
