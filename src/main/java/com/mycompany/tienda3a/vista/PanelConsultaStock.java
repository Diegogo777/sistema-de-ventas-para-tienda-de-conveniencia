package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.modelo.Categoria;
import com.mycompany.tienda3a.modelo.Producto;
import com.mycompany.tienda3a.modelo.Promocion;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class PanelConsultaStock extends PanelBase {
    private final Campo buscar = new Campo("Código, nombre o categoría");
    private final Combo<Categoria> categoria;
    private final JTable tabla = Tablas.crear("Código", "Producto", "Categoría", "Precio", "Promoción vigente", "Stock", "Estado");
    private final JLabel conteo = Textos.ayuda("");

    PanelConsultaStock(Contexto ctx) {
        super(ctx, "Consultar productos", "Precio, promoción vigente y stock sin iniciar una venta.", Icono.Tipo.BUSCAR, "CU-07");
        List<Categoria> cats = new ArrayList<>();
        cats.add(null);
        cats.addAll(ctx.producto.listarCategorias());
        categoria = new Combo<>(cats, c -> c == null ? "Todas las categorías" : c.getNombre());
        Tarjeta t = new Tarjeta();
        JPanel filtros = Textos.transparente(new BorderLayout(12, 0));
        buscar.conIcono(Icono.Tipo.BUSCAR);
        filtros.add(buscar, BorderLayout.CENTER);
        categoria.setPreferredSize(new Dimension(230, 40));
        filtros.add(categoria, BorderLayout.EAST);
        filtros.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        t.cuerpo().add(filtros, BorderLayout.NORTH);
        Tablas.ancho(tabla, 0, 130, true);
        Tablas.ancho(tabla, 1, 260, false);
        Tablas.ancho(tabla, 2, 150, false);
        Tablas.ancho(tabla, 3, 80, true);
        Tablas.ancho(tabla, 4, 220, true);
        Tablas.ancho(tabla, 5, 100, true);
        Tablas.ancho(tabla, 6, 140, true);
        Tablas.cifra(tabla, 3, 5);
        Tablas.insignia(tabla, 4, v -> v == null || v.toString().isEmpty() ? Insignia.Tipo.NEUTRO : Insignia.Tipo.PROMO);
        Tablas.insignia(tabla, 6, v -> switch (String.valueOf(v)) {
            case "Disponible" -> Insignia.Tipo.EXITO;
            case "Stock bajo" -> Insignia.Tipo.ADVERTENCIA;
            default -> Insignia.Tipo.PELIGRO;
        });
        t.con(Tablas.envolver(tabla));
        conteo.setBorder(BorderFactory.createEmptyBorder(10, 2, 0, 0));
        t.cuerpo().add(conteo, BorderLayout.SOUTH);
        add(t, BorderLayout.CENTER);
        buscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { cargar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { cargar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { cargar(); }
        });
        categoria.addActionListener(e -> cargar());
    }

    @Override
    public void alMostrar() {
        cargar();
        SwingUtilities.invokeLater(buscar::requestFocusInWindow);
    }

    private void cargar() {
        var m = Tablas.modelo(tabla);
        m.setRowCount(0);
        Categoria cat = categoria.seleccionado();
        int n = 0;
        int bajos = 0;
        for (Producto p : ctx.producto.buscarProductos(buscar.getText())) {
            if (cat != null && p.getCategoria() != cat) {
                continue;
            }
            Promocion promo = p.promocionVigente(LocalDate.now());
            String estado = p.getStockActual() <= 0 ? "Sin stock" : p.getStockActual() < 10 ? "Stock bajo" : "Disponible";
            if (!"Disponible".equals(estado)) {
                bajos++;
            }
            m.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getCategoria().getNombre(), Tema.numero(p.getPrecioVenta()),
                    promo == null ? "" : "S/ " + Tema.numero(promo.getPrecioPromocional()) + " hasta " + promo.getFechaFin().format(Tema.FECHA),
                    p.formatearCantidad(p.getStockActual()), estado});
            n++;
        }
        conteo.setText(n + " productos" + (bajos > 0 ? " · " + bajos + " con stock bajo o sin stock" : ""));
    }
}
