package com.mycompany.tienda3a.vista.componentes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

public class Grafico extends JPanel {
    private final boolean horizontal;
    private Map<String, Double> datos = new LinkedHashMap<>();
    private final Color color;
    private String resaltado;

    public Grafico(boolean horizontal, Color color) {
        this.horizontal = horizontal;
        this.color = color;
        setOpaque(false);
        setPreferredSize(new Dimension(300, 220));
    }

    public void setDatos(Map<String, Double> datos, String resaltado) {
        this.datos = datos;
        this.resaltado = resaltado;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (datos.isEmpty()) {
            g.setFont(Tema.texto(13));
            g.setColor(Tema.TEXTO_TENUE);
            g.drawString("Sin ventas en el periodo", 8, 24);
            g.dispose();
            return;
        }
        double max = 0;
        for (double v : datos.values()) {
            max = Math.max(max, v);
        }
        max = max <= 0 ? 1 : max;
        List<Map.Entry<String, Double>> filas = new ArrayList<>(datos.entrySet());
        if (horizontal) {
            dibujarHorizontal(g, filas, max);
        } else {
            dibujarVertical(g, filas, max);
        }
        g.dispose();
    }

    private void dibujarVertical(Graphics2D g, List<Map.Entry<String, Double>> filas, double max) {
        int w = getWidth();
        int h = getHeight();
        int abajo = 26;
        int arriba = 22;
        int alto = h - abajo - arriba;
        g.setColor(Tema.BORDE);
        for (int i = 0; i <= 3; i++) {
            int y = arriba + alto - alto * i / 3;
            g.draw(new Line2D.Double(0, y, w, y));
        }
        int n = filas.size();
        double paso = (double) w / n;
        double ancho = Math.min(46, paso * 0.56);
        g.setFont(Tema.texto(11.5f));
        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i < n; i++) {
            Map.Entry<String, Double> e = filas.get(i);
            double bh = alto * e.getValue() / max;
            double x = paso * i + (paso - ancho) / 2;
            boolean res = e.getKey().equals(resaltado);
            g.setColor(res ? Tema.AMARILLO : color);
            g.fill(new RoundRectangle2D.Double(x, arriba + alto - bh, ancho, Math.max(bh, 2), 6, 6));
            g.setColor(Tema.TEXTO_SUAVE);
            String et = e.getKey();
            g.drawString(et, (int) (paso * i + (paso - fm.stringWidth(et)) / 2), h - 6);
            if (e.getValue() > 0) {
                g.setFont(Tema.cifra(11));
                String v = Tema.numero(e.getValue()).replace(".00", "");
                FontMetrics fv = g.getFontMetrics();
                g.setColor(Tema.TEXTO);
                g.drawString(v, (int) (x + (ancho - fv.stringWidth(v)) / 2), (int) (arriba + alto - bh - 6));
                g.setFont(Tema.texto(11.5f));
            }
        }
    }

    private void dibujarHorizontal(Graphics2D g, List<Map.Entry<String, Double>> filas, double max) {
        int w = getWidth();
        int alturaFila = 34;
        g.setFont(Tema.texto(12.5f));
        FontMetrics fm = g.getFontMetrics();
        int etiqueta = 0;
        for (Map.Entry<String, Double> e : filas) {
            etiqueta = Math.max(etiqueta, fm.stringWidth(e.getKey()));
        }
        etiqueta = Math.min(etiqueta + 14, w / 2);
        int valor = 92;
        int barra = Math.max(40, w - etiqueta - valor);
        for (int i = 0; i < filas.size(); i++) {
            Map.Entry<String, Double> e = filas.get(i);
            int y = i * alturaFila;
            g.setColor(Tema.TEXTO);
            g.setFont(Tema.texto(12.5f));
            String et = e.getKey();
            while (fm.stringWidth(et) > etiqueta - 12 && et.length() > 4) {
                et = et.substring(0, et.length() - 2) + "…";
            }
            g.drawString(et, 0, y + 21);
            g.setColor(Tema.FONDO);
            g.fill(new RoundRectangle2D.Double(etiqueta, y + 9, barra, 16, 8, 8));
            g.setColor(color);
            g.fill(new RoundRectangle2D.Double(etiqueta, y + 9, Math.max(6, barra * e.getValue() / max), 16, 8, 8));
            g.setFont(Tema.cifra(12));
            String v = Tema.soles(e.getValue());
            g.setColor(Tema.TEXTO);
            g.drawString(v, w - g.getFontMetrics().stringWidth(v), y + 21);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (horizontal) {
            return new Dimension(300, Math.max(60, datos.size() * 34));
        }
        return super.getPreferredSize();
    }
}
