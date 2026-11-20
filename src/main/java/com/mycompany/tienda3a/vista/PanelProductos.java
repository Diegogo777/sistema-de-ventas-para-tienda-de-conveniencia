package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class PanelProductos extends PanelBase {
    private final Campo buscar = new Campo("Buscar por código, nombre o categoría");
    private final JCheckBox inactivos = new JCheckBox("Mostrar desactivados");
    private final JTable tabla = Tablas.crear("Código", "Producto", "Categoría", "Unidad", "Precio", "Stock", "Estado");
    private final List<Producto> lista = new ArrayList<>();
    private Producto editando;

    private final JLabel tituloForm = new JLabel("Nuevo producto");
    private final Campo codigo = new Campo("Código de barras o interno");
    private final Campo nombre = new Campo("Nombre en caja y en el comprobante");
    private final Combo<Categoria> categoria;
    private final Combo<UnidadMedida> unidad = new Combo<>(List.of(UnidadMedida.UNIDAD, UnidadMedida.KG),
            u -> u == UnidadMedida.KG ? "Kilogramo (a granel)" : "Unidad");
    private final Campo precio = new Campo("0.00");
    private final Campo stock = new Campo("0");
    private final JLabel notaStock = Textos.texto(" ", 11.5f, Tema.TEXTO_TENUE);
    private final Boton guardar = new Boton("Registrar producto", Boton.Estilo.PRIMARIO, Icono.Tipo.CHECK);
    private final Boton estado = new Boton("Desactivar", Boton.Estilo.PELIGRO_SUAVE);

    PanelProductos(Contexto ctx) {
        super(ctx, "Productos", "Registra, modifica y desactiva productos. Los productos no se borran (RN-11).",
                Icono.Tipo.PRODUCTO, "CU-11");
        categoria = new Combo<>(ctx.producto.listarCategorias(), Categoria::getNombre);
        JPanel centro = Textos.transparente(new BorderLayout(16, 0));
        Tarjeta t = new Tarjeta();
        JPanel f = Textos.transparente(new BorderLayout(12, 0));
        buscar.conIcono(Icono.Tipo.BUSCAR);
        f.add(buscar, BorderLayout.CENTER);
        inactivos.setOpaque(false);
        inactivos.setFont(Tema.texto(12.5f));
        f.add(inactivos, BorderLayout.EAST);
        f.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        t.cuerpo().add(f, BorderLayout.NORTH);
        Tablas.ancho(tabla, 0, 128, true);
        Tablas.ancho(tabla, 1, 200, false);
        Tablas.ancho(tabla, 2, 120, false);
        Tablas.ancho(tabla, 3, 58, true);
        Tablas.ancho(tabla, 4, 72, true);
        Tablas.ancho(tabla, 5, 84, true);
        Tablas.ancho(tabla, 6, 122, true);
        Tablas.cifra(tabla, 4, 5);
        Tablas.insignia(tabla, 6, v -> switch (String.valueOf(v)) {
            case "Activo" -> Insignia.Tipo.EXITO;
            case "Stock bajo" -> Insignia.Tipo.ADVERTENCIA;
            default -> Insignia.Tipo.NEUTRO;
        });
        t.con(Tablas.envolver(tabla));
        centro.add(t, BorderLayout.CENTER);
        centro.add(formulario(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);
        buscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { cargar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { cargar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { cargar(); }
        });
        inactivos.addActionListener(e -> cargar());
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                editar(lista.get(tabla.getSelectedRow()));
            }
        });
    }

    private JComponent formulario() {
        Tarjeta t = new Tarjeta();
        JPanel p = Textos.transparente(null);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JPanel cab = Textos.transparente(new BorderLayout());
        tituloForm.setFont(Tema.negrita(17));
        cab.add(tituloForm, BorderLayout.WEST);
        Boton nuevo = new Boton("Nuevo", Boton.Estilo.FANTASMA, Icono.Tipo.MAS);
        nuevo.addActionListener(e -> limpiar());
        cab.add(nuevo, BorderLayout.EAST);
        p.add(fila(cab));
        p.add(Box.createVerticalStrut(14));
        p.add(fila(Textos.conEtiqueta("Código", codigo)));
        p.add(Box.createVerticalStrut(12));
        p.add(fila(Textos.conEtiqueta("Nombre", nombre)));
        p.add(Box.createVerticalStrut(12));
        p.add(fila(Textos.conEtiqueta("Categoría", categoria)));
        p.add(Box.createVerticalStrut(12));
        p.add(fila(Textos.conEtiqueta("Se vende por", unidad)));
        p.add(Box.createVerticalStrut(12));
        JPanel dos = Textos.transparente(new GridLayout(1, 2, 10, 0));
        precio.setFont(Tema.cifra(14));
        stock.setFont(Tema.cifra(14));
        dos.add(Textos.conEtiqueta("Precio con IGV (S/)", precio));
        dos.add(Textos.conEtiqueta("Stock inicial", stock));
        p.add(fila(dos));
        p.add(Box.createVerticalStrut(6));
        p.add(fila(notaStock));
        p.add(Box.createVerticalStrut(16));
        guardar.addActionListener(e -> guardar());
        p.add(fila(guardar));
        p.add(Box.createVerticalStrut(8));
        estado.addActionListener(e -> cambiarEstado());
        p.add(fila(estado));
        t.con(p);
        limpiar();
        return Textos.ancho(t, 330);
    }

    private static JComponent fila(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        return c;
    }

    @Override
    public void alMostrar() {
        cargar();
    }

    private void cargar() {
        lista.clear();
        lista.addAll(ctx.producto.buscar(buscar.getText(), inactivos.isSelected()));
        var m = Tablas.modelo(tabla);
        m.setRowCount(0);
        for (Producto p : lista) {
            m.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getCategoria().getNombre(),
                    p.getUnidadMedida() == UnidadMedida.KG ? "kg" : "und", Tema.numero(p.getPrecioVenta()),
                    p.formatearCantidad(p.getStockActual()),
                    !p.isActivo() ? "Desactivado" : p.getStockActual() < 10 ? "Stock bajo" : "Activo"});
        }
    }

    private void limpiar() {
        editando = null;
        tabla.clearSelection();
        tituloForm.setText("Nuevo producto");
        for (Campo c : new Campo[]{codigo, nombre, precio, stock}) {
            c.setText("");
            c.setError(false);
        }
        codigo.setEditable(true);
        stock.setEditable(true);
        notaStock.setText("El stock luego cambia solo con ventas y anulaciones (RN-02).");
        guardar.setText("Registrar producto");
        estado.setVisible(false);
    }

    private void editar(Producto p) {
        editando = p;
        aviso.limpiar();
        tituloForm.setText("Editar producto");
        codigo.setText(p.getCodigo());
        codigo.setEditable(false);
        nombre.setText(p.getNombre());
        categoria.setSelectedItem(p.getCategoria());
        unidad.setSelectedItem(p.getUnidadMedida());
        precio.setText(String.format(java.util.Locale.US, "%.2f", p.getPrecioVenta()));
        stock.setText(p.formatearCantidad(p.getStockActual()).replace(" kg", ""));
        stock.setEditable(false);
        notaStock.setText("Stock actual: se actualiza con cada venta o anulación.");
        guardar.setText("Guardar cambios");
        estado.setVisible(true);
        estado.setText(p.isActivo() ? "Desactivar producto" : "Reactivar producto");
        estado.setEstilo(p.isActivo() ? Boton.Estilo.PELIGRO_SUAVE : Boton.Estilo.SECUNDARIO);
    }

    private void guardar() {
        aviso.limpiar();
        double pr;
        try {
            pr = Double.parseDouble(precio.getText().trim().replace(',', '.'));
            precio.setError(false);
        } catch (NumberFormatException e) {
            precio.setError(true);
            aviso.mostrar(Aviso.Tipo.ERROR, "Escribe el precio con números, por ejemplo 4.90.");
            return;
        }
        if (editando == null) {
            double st;
            try {
                st = stock.getText().isBlank() ? 0 : Double.parseDouble(stock.getText().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                stock.setError(true);
                aviso.mostrar(Aviso.Tipo.ERROR, "Escribe el stock inicial con números.");
                return;
            }
            Producto[] nuevo = new Producto[1];
            if (ejecutar(() -> nuevo[0] = ctx.producto.registrarProducto(categoria.seleccionado(), codigo.getText(),
                    nombre.getText(), unidad.seleccionado(), pr, st, ctx.getUsuario()))) {
                cargar();
                seleccionar(nuevo[0]);
                exito("Producto «" + nuevo[0].getNombre() + "» registrado.");
            }
        } else {
            Producto p = editando;
            boolean cambioPrecio = Math.abs(p.getPrecioVenta() - pr) > 0.001;
            if (ejecutar(() -> {
                ctx.producto.modificarProducto(p, categoria.seleccionado(), nombre.getText(), unidad.seleccionado(), ctx.getUsuario());
                if (cambioPrecio) {
                    ctx.producto.cambiarPrecio(p, pr, ctx.getUsuario());
                }
            })) {
                cargar();
                seleccionar(p);
                exito("Cambios guardados." + (cambioPrecio ? " El nuevo precio rige para las ventas que se agreguen desde ahora (RN-03)." : ""));
            }
        }
    }

    private void cambiarEstado() {
        Producto p = editando;
        if (p == null) {
            return;
        }
        if (p.isActivo()) {
            if (Dialogos.confirmar(this, "¿Desactivar «" + p.getNombre() + "»?", "No se podrá vender ni aparecerá en la búsqueda de "
                    + "caja. Su historial de ventas se conserva y puedes reactivarlo cuando quieras.", "Desactivar", Boton.Estilo.PELIGRO)
                    && ejecutar(() -> ctx.producto.desactivarProducto(p, ctx.getUsuario()))) {
                inactivos.setSelected(true);
                cargar();
                seleccionar(p);
                exito("Producto desactivado.");
            }
        } else if (ejecutar(() -> ctx.producto.reactivarProducto(p, ctx.getUsuario()))) {
            cargar();
            seleccionar(p);
            exito("Producto reactivado.");
        }
    }

    private void seleccionar(Producto p) {
        int i = lista.indexOf(p);
        if (i >= 0) {
            tabla.setRowSelectionInterval(i, i);
            tabla.scrollRectToVisible(tabla.getCellRect(i, 0, true));
        }
    }
}
