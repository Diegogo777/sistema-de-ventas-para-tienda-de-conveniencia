package com.mycompany.tienda3a.vista.componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

public class Logo extends JComponent {
    private final int tam;
    private final boolean conTexto;
    private final Color colorTexto;

    public Logo(int tam, boolean conTexto, Color colorTexto) {
        this.tam = tam;
        this.conTexto = conTexto;
        this.colorTexto = colorTexto;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(conTexto ? tam + (int) (tam * 4.2) : tam, tam);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Tema.AMARILLO);
        g.fill(new RoundRectangle2D.Double(0, 0, tam, tam, tam * 0.3, tam * 0.3));
        g.setFont(Tema.negrita(tam * 0.46f));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Tema.PETROLEO_OSCURO);
        String s = "3A";
        g.drawString(s, (tam - fm.stringWidth(s)) / 2f, (tam + fm.getAscent() - fm.getDescent()) / 2f);
        if (conTexto) {
            g.setColor(colorTexto);
            g.setFont(Tema.negrita(tam * 0.42f));
            FontMetrics f2 = g.getFontMetrics();
            g.drawString("Tiendas 3A", tam + tam * 0.3f, tam * 0.52f);
            g.setFont(Tema.texto(tam * 0.27f));
            g.setColor(new Color(colorTexto.getRed(), colorTexto.getGreen(), colorTexto.getBlue(), 170));
            g.drawString("Ventas en caja", tam + tam * 0.3f, tam * 0.52f + f2.getDescent() + tam * 0.3f);
        }
        g.dispose();
    }
}
