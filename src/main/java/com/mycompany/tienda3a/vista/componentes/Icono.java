package com.mycompany.tienda3a.vista.componentes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;

public class Icono implements Icon {
    public enum Tipo {
        CARRITO, BUSCAR, ANULAR, CIERRE, INICIO, VENTAS, PRODUCTO, PROMO, USUARIOS, COMPROBANTE, BITACORA,
        SALIR, MAS, MENOS, BASURA, CHECK, ALERTA, INFO, CANDADO, USUARIO, EFECTIVO, TARJETA, BILLETERA,
        EDITAR, ENVIAR, TIENDA, CERRAR, RELOJ, OJO, BLOQUEO, REPORTE, CALENDARIO
    }

    private final Tipo tipo;
    private final int tam;
    private Color color;

    public Icono(Tipo tipo, int tam, Color color) {
        this.tipo = tipo;
        this.tam = tam;
        this.color = color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return tam;
    }

    @Override
    public int getIconHeight() {
        return tam;
    }

    @Override
    public void paintIcon(Component c, Graphics g0, int x, int y) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.translate(x, y);
        double e = tam / 24.0;
        g.scale(e, e);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        dibujar(g);
        g.dispose();
    }

    private void dibujar(Graphics2D g) {
        switch (tipo) {
            case CARRITO -> {
                Path2D p = new Path2D.Double();
                p.moveTo(2.5, 3.5); p.lineTo(5.2, 3.5); p.lineTo(7.6, 15); p.lineTo(18.5, 15); p.lineTo(20.8, 7); p.lineTo(6.2, 7);
                g.draw(p);
                g.fill(new Ellipse2D.Double(7.6, 17.6, 3, 3));
                g.fill(new Ellipse2D.Double(15.6, 17.6, 3, 3));
            }
            case BUSCAR -> {
                g.draw(new Ellipse2D.Double(4, 4, 12, 12));
                g.draw(new Line2D.Double(14.6, 14.6, 20, 20));
            }
            case ANULAR -> {
                g.draw(new Arc2D.Double(4.5, 5, 15, 14, 100, 260, Arc2D.OPEN));
                Path2D p = new Path2D.Double();
                p.moveTo(3.5, 3.6); p.lineTo(9.6, 5.2); p.lineTo(5.6, 10);
                g.draw(p);
            }
            case CIERRE -> {
                g.draw(new RoundRectangle2D.Double(3, 10, 18, 10, 2, 2));
                g.draw(new RoundRectangle2D.Double(6, 4, 12, 6, 1.5, 1.5));
                g.draw(new Line2D.Double(7, 14, 9, 14));
                g.draw(new Line2D.Double(11, 14, 13, 14));
                g.draw(new Line2D.Double(15, 14, 17, 14));
                g.draw(new Line2D.Double(7, 17, 17, 17));
            }
            case INICIO -> {
                g.draw(new RoundRectangle2D.Double(3.5, 3.5, 7, 7, 2, 2));
                g.draw(new RoundRectangle2D.Double(13.5, 3.5, 7, 7, 2, 2));
                g.draw(new RoundRectangle2D.Double(3.5, 13.5, 7, 7, 2, 2));
                g.draw(new RoundRectangle2D.Double(13.5, 13.5, 7, 7, 2, 2));
            }
            case VENTAS, REPORTE -> {
                if (tipo == Tipo.VENTAS) {
                    Path2D p = new Path2D.Double();
                    p.moveTo(5, 3); p.lineTo(19, 3); p.lineTo(19, 21); p.lineTo(16.7, 19.5); p.lineTo(14.3, 21);
                    p.lineTo(12, 19.5); p.lineTo(9.7, 21); p.lineTo(7.3, 19.5); p.lineTo(5, 21); p.closePath();
                    g.draw(p);
                    g.draw(new Line2D.Double(8.5, 8, 15.5, 8));
                    g.draw(new Line2D.Double(8.5, 11.5, 15.5, 11.5));
                    g.draw(new Line2D.Double(8.5, 15, 12.5, 15));
                } else {
                    g.draw(new Line2D.Double(4, 20, 20, 20));
                    g.draw(new RoundRectangle2D.Double(5.5, 12, 3, 8, 1, 1));
                    g.draw(new RoundRectangle2D.Double(10.5, 7, 3, 13, 1, 1));
                    g.draw(new RoundRectangle2D.Double(15.5, 4, 3, 16, 1, 1));
                }
            }
            case PRODUCTO -> {
                Path2D p = new Path2D.Double();
                p.moveTo(12, 3); p.lineTo(20, 7); p.lineTo(20, 17); p.lineTo(12, 21); p.lineTo(4, 17); p.lineTo(4, 7); p.closePath();
                g.draw(p);
                Path2D q = new Path2D.Double();
                q.moveTo(4, 7); q.lineTo(12, 11); q.lineTo(20, 7); q.moveTo(12, 11); q.lineTo(12, 21);
                g.draw(q);
            }
            case PROMO -> {
                Path2D p = new Path2D.Double();
                p.moveTo(3.5, 12.5); p.lineTo(12.5, 3.5); p.lineTo(20.5, 3.5); p.lineTo(20.5, 11.5); p.lineTo(11.5, 20.5); p.closePath();
                g.draw(p);
                g.fill(new Ellipse2D.Double(15, 6.5, 2.6, 2.6));
            }
            case USUARIOS -> {
                g.draw(new Ellipse2D.Double(5, 5, 7, 7));
                g.draw(new Arc2D.Double(2.5, 14, 12, 10, 0, 180, Arc2D.OPEN));
                g.draw(new Arc2D.Double(13, 5, 6, 7, -60, 210, Arc2D.OPEN));
                g.draw(new Arc2D.Double(15, 14, 7.5, 10, 0, 120, Arc2D.OPEN));
            }
            case USUARIO -> {
                g.draw(new Ellipse2D.Double(8, 4, 8, 8));
                g.draw(new Arc2D.Double(4.5, 14, 15, 12, 0, 180, Arc2D.OPEN));
            }
            case COMPROBANTE -> {
                Path2D p = new Path2D.Double();
                p.moveTo(14, 3); p.lineTo(6, 3); p.lineTo(6, 21); p.lineTo(18, 21); p.lineTo(18, 7); p.closePath();
                p.moveTo(14, 3); p.lineTo(14, 7); p.lineTo(18, 7);
                g.draw(p);
                Path2D q = new Path2D.Double();
                q.moveTo(9, 14); q.lineTo(11.2, 16.2); q.lineTo(15, 12);
                g.draw(q);
            }
            case BITACORA -> {
                g.draw(new Ellipse2D.Double(3.5, 3.5, 17, 17));
                Path2D p = new Path2D.Double();
                p.moveTo(12, 7.5); p.lineTo(12, 12); p.lineTo(15, 14);
                g.draw(p);
            }
            case RELOJ -> {
                g.draw(new Ellipse2D.Double(4, 4, 16, 16));
                Path2D p = new Path2D.Double();
                p.moveTo(12, 8); p.lineTo(12, 12); p.lineTo(14.5, 13.5);
                g.draw(p);
            }
            case SALIR -> {
                Path2D p = new Path2D.Double();
                p.moveTo(10, 4); p.lineTo(5, 4); p.lineTo(5, 20); p.lineTo(10, 20);
                p.moveTo(10, 12); p.lineTo(20, 12); p.moveTo(16, 8); p.lineTo(20, 12); p.lineTo(16, 16);
                g.draw(p);
            }
            case MAS -> {
                g.draw(new Line2D.Double(12, 5, 12, 19));
                g.draw(new Line2D.Double(5, 12, 19, 12));
            }
            case MENOS -> g.draw(new Line2D.Double(5, 12, 19, 12));
            case CERRAR -> {
                g.draw(new Line2D.Double(6, 6, 18, 18));
                g.draw(new Line2D.Double(18, 6, 6, 18));
            }
            case BASURA -> {
                g.draw(new Line2D.Double(4, 6.5, 20, 6.5));
                Path2D p = new Path2D.Double();
                p.moveTo(9, 6.5); p.lineTo(9, 3.8); p.lineTo(15, 3.8); p.lineTo(15, 6.5);
                p.moveTo(6, 6.5); p.lineTo(7, 20.5); p.lineTo(17, 20.5); p.lineTo(18, 6.5);
                p.moveTo(10, 10.5); p.lineTo(10, 16.5); p.moveTo(14, 10.5); p.lineTo(14, 16.5);
                g.draw(p);
            }
            case CHECK -> {
                Path2D p = new Path2D.Double();
                p.moveTo(4.5, 12.5); p.lineTo(9.5, 17.5); p.lineTo(19.5, 6.5);
                g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(p);
            }
            case ALERTA -> {
                Path2D p = new Path2D.Double();
                p.moveTo(12, 3.5); p.lineTo(21.5, 20); p.lineTo(2.5, 20); p.closePath();
                g.draw(p);
                g.draw(new Line2D.Double(12, 9.5, 12, 14));
                g.fill(new Ellipse2D.Double(10.8, 16, 2.4, 2.4));
            }
            case INFO -> {
                g.draw(new Ellipse2D.Double(3.5, 3.5, 17, 17));
                g.draw(new Line2D.Double(12, 11, 12, 16.5));
                g.fill(new Ellipse2D.Double(10.8, 6.8, 2.4, 2.4));
            }
            case CANDADO, BLOQUEO -> {
                g.draw(new RoundRectangle2D.Double(5, 10.5, 14, 10, 3, 3));
                g.draw(new Arc2D.Double(8, 3.5, 8, 12, 0, 180, Arc2D.OPEN));
                g.draw(new Line2D.Double(8, 9.5, 8, 10.5));
                g.draw(new Line2D.Double(16, 9.5, 16, 10.5));
                g.fill(new Ellipse2D.Double(10.8, 14.2, 2.4, 2.4));
            }
            case EFECTIVO -> {
                g.draw(new RoundRectangle2D.Double(2.5, 6, 19, 12, 2.5, 2.5));
                g.draw(new Ellipse2D.Double(9.2, 9.2, 5.6, 5.6));
                g.draw(new Line2D.Double(5.5, 9, 5.5, 9.01));
                g.draw(new Line2D.Double(18.5, 15, 18.5, 15.01));
            }
            case TARJETA -> {
                g.draw(new RoundRectangle2D.Double(2.5, 5.5, 19, 13, 2.5, 2.5));
                g.draw(new Line2D.Double(2.5, 9.5, 21.5, 9.5));
                g.draw(new Line2D.Double(6, 14.5, 10, 14.5));
            }
            case BILLETERA -> {
                g.draw(new RoundRectangle2D.Double(6.5, 2.5, 11, 19, 3, 3));
                g.draw(new Line2D.Double(10.5, 18.2, 13.5, 18.2));
                g.draw(new Line2D.Double(10, 5.5, 14, 5.5));
            }
            case EDITAR -> {
                Path2D p = new Path2D.Double();
                p.moveTo(15.5, 4.5); p.lineTo(19.5, 8.5); p.lineTo(9, 19); p.lineTo(4.5, 19.5); p.lineTo(5, 15); p.closePath();
                g.draw(p);
            }
            case ENVIAR -> {
                Path2D p = new Path2D.Double();
                p.moveTo(3.5, 11); p.lineTo(20.5, 3.5); p.lineTo(14, 20.5); p.lineTo(11, 13); p.closePath();
                p.moveTo(11, 13); p.lineTo(20.5, 3.5);
                g.draw(p);
            }
            case TIENDA -> {
                Path2D p = new Path2D.Double();
                p.moveTo(3.5, 9); p.lineTo(5.5, 4); p.lineTo(18.5, 4); p.lineTo(20.5, 9);
                p.moveTo(5, 10.5); p.lineTo(5, 20); p.lineTo(19, 20); p.lineTo(19, 10.5);
                p.moveTo(10, 20); p.lineTo(10, 14.5); p.lineTo(14, 14.5); p.lineTo(14, 20);
                g.draw(p);
                g.draw(new Arc2D.Double(3.5, 6, 5.5, 6, 180, 180, Arc2D.OPEN));
                g.draw(new Arc2D.Double(9.2, 6, 5.5, 6, 180, 180, Arc2D.OPEN));
                g.draw(new Arc2D.Double(15, 6, 5.5, 6, 180, 180, Arc2D.OPEN));
            }
            case OJO -> {
                Path2D p = new Path2D.Double();
                p.moveTo(2.5, 12); p.quadTo(12, 2.5, 21.5, 12); p.quadTo(12, 21.5, 2.5, 12);
                g.draw(p);
                g.draw(new Ellipse2D.Double(9, 9, 6, 6));
            }
            case CALENDARIO -> {
                g.draw(new RoundRectangle2D.Double(3.5, 5, 17, 15.5, 3, 3));
                g.draw(new Line2D.Double(3.5, 9.5, 20.5, 9.5));
                g.draw(new Line2D.Double(8, 3, 8, 6.5));
                g.draw(new Line2D.Double(16, 3, 16, 6.5));
            }
        }
    }
}
