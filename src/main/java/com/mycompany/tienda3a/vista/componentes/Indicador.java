package com.mycompany.tienda3a.vista.componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Indicador extends Tarjeta {
    private final JLabel valor;
    private final JLabel nota;

    public Indicador(String etiqueta, Icono.Tipo icono, Color color) {
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        JPanel p = Textos.transparente(new BorderLayout(14, 0));
        JComponent circulo = new JComponent() {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Tema.tinte(color, 0.13));
                g.fill(new Ellipse2D.Double(0, 0, 42, 42));
                new Icono(icono, 21, color).paintIcon(this, g, 10, 10);
                g.dispose();
            }
        };
        circulo.setPreferredSize(new Dimension(42, 42));
        JPanel ic = Textos.transparente(new BorderLayout());
        ic.add(circulo, BorderLayout.NORTH);
        p.add(ic, BorderLayout.WEST);
        JPanel txt = Textos.transparente(new GridLayout(3, 1, 0, 1));
        JLabel e = Textos.etiqueta(etiqueta);
        valor = Textos.monto("—", 21, Tema.TEXTO);
        nota = Textos.texto(" ", 11.5f, Tema.TEXTO_TENUE);
        txt.add(e);
        txt.add(valor);
        txt.add(nota);
        p.add(txt, BorderLayout.CENTER);
        con(p);
    }

    public void setValor(String v, String n) {
        valor.setText(v);
        nota.setText(n == null || n.isEmpty() ? " " : n);
    }

    public void setColorValor(Color c) {
        valor.setForeground(c);
    }
}
