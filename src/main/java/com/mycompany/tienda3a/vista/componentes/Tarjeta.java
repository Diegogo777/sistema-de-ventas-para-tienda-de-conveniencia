package com.mycompany.tienda3a.vista.componentes;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Tarjeta extends JPanel {
    private final JPanel cuerpo;

    public Tarjeta() {
        this(null, null);
    }

    public Tarjeta(String titulo, String subtitulo) {
        super(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);
        if (titulo != null) {
            JPanel cabecera = new JPanel();
            cabecera.setOpaque(false);
            cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
            JLabel t = new JLabel(titulo);
            t.setFont(Tema.medio(15));
            t.setForeground(Tema.TEXTO);
            cabecera.add(t);
            if (subtitulo != null) {
                JLabel s = new JLabel(subtitulo);
                s.setFont(Tema.texto(12));
                s.setForeground(Tema.TEXTO_SUAVE);
                cabecera.add(s);
            }
            cabecera.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            add(cabecera, BorderLayout.NORTH);
        }
        add(cuerpo, BorderLayout.CENTER);
    }

    public JPanel cuerpo() {
        return cuerpo;
    }

    public Tarjeta con(JComponent c) {
        cuerpo.add(c, BorderLayout.CENTER);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D r = new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1.5, getHeight() - 1.5, Tema.RADIO * 1.5, Tema.RADIO * 1.5);
        g.setColor(new java.awt.Color(16, 40, 48, 10));
        g.fill(new RoundRectangle2D.Double(0.5, 1.5, getWidth() - 1.5, getHeight() - 1.5, Tema.RADIO * 1.5, Tema.RADIO * 1.5));
        g.setColor(Tema.SUPERFICIE);
        g.fill(r);
        g.setColor(Tema.BORDE);
        g.draw(r);
        g.dispose();
    }
}
