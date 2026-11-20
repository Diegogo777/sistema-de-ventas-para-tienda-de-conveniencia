package com.mycompany.tienda3a.vista.componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class Textos {
    private Textos() {
    }

    public static JLabel titulo(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Tema.negrita(22));
        l.setForeground(Tema.TEXTO);
        return l;
    }

    public static JLabel ayuda(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Tema.texto(13));
        l.setForeground(Tema.TEXTO_SUAVE);
        return l;
    }

    public static JLabel etiqueta(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Tema.medio(12));
        l.setForeground(Tema.TEXTO_SUAVE);
        return l;
    }

    public static JLabel texto(String t, float tam, Color color) {
        JLabel l = new JLabel(t);
        l.setFont(Tema.texto(tam));
        l.setForeground(color);
        return l;
    }

    public static JLabel monto(String t, float tam, Color color) {
        JLabel l = new JLabel(t);
        l.setFont(Tema.cifraNegrita(tam));
        l.setForeground(color);
        return l;
    }

    public static JPanel conEtiqueta(String etiqueta, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.add(etiqueta(etiqueta), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    public static JPanel cabecera(String titulo, String descripcion) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = titulo(titulo);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel d = ayuda(descripcion);
        d.setAlignmentX(Component.LEFT_ALIGNMENT);
        d.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        p.add(t);
        p.add(d);
        return p;
    }

    public static JPanel ancho(JComponent c, int ancho) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            public java.awt.Dimension getPreferredSize() {
                return new java.awt.Dimension(ancho, super.getPreferredSize().height);
            }
        };
        p.setOpaque(false);
        p.add(c, BorderLayout.NORTH);
        return p;
    }

    public static JPanel transparente(java.awt.LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }
}
