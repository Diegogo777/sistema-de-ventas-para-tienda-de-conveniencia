package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.modelo.Administrador;
import com.mycompany.tienda3a.modelo.Producto;
import com.mycompany.tienda3a.modelo.Promocion;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class PanelPromociones extends PanelBase {
    private final JTable tabla = Tablas.crear("Promoción", "Producto", "Normal", "Promo", "Desde", "Hasta", "Estado");
    private final List<Promocion> lista = new ArrayList<>();
    private final Combo<Producto> producto = new Combo<>(List.of(), Producto::getNombre);
    private final Campo nombre = new Campo("Ej.: Semana de galletas");
    private final Campo precio = new Campo("0.00");
    private final Campo desde = new Campo("dd/mm/aaaa");
    private final Campo hasta = new Campo("dd/mm/aaaa");
    private final JLabel referencia = Textos.texto(" ", 12, Tema.TEXTO_SUAVE);
    private final Boton desactivar = new Boton("Desactivar promoción seleccionada", Boton.Estilo.PELIGRO_SUAVE);

    PanelPromociones(Contexto ctx) {
        super(ctx, "Promociones", "Precio promocional por producto y periodo; se aplica solo al vender (RF-21).",
                Icono.Tipo.PROMO, "CU-11");
        JPanel centro = Textos.transparente(new BorderLayout(16, 0));
        Tarjeta t = new Tarjeta("Promociones registradas", "Un producto no puede tener dos promociones activas en las mismas fechas (RN-13).");
        Tablas.ancho(tabla, 0, 180, false);
        Tablas.ancho(tabla, 1, 150, false);
        Tablas.ancho(tabla, 2, 70, true);
        Tablas.ancho(tabla, 3, 70, true);
        Tablas.ancho(tabla, 4, 92, true);
        Tablas.ancho(tabla, 5, 92, true);
        Tablas.ancho(tabla, 6, 124, true);
        Tablas.cifra(tabla, 2, 3);
        Tablas.insignia(tabla, 6, v -> switch (String.valueOf(v)) {
            case "Vigente" -> Insignia.Tipo.PROMO;
            case "Programada" -> Insignia.Tipo.MARCA;
            default -> Insignia.Tipo.NEUTRO;
        });
        t.con(Tablas.envolver(tabla));
        desactivar.addActionListener(e -> desactivar());
        producto.addActionListener(e -> mostrarReferencia());
        JPanel pie = Textos.transparente(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pie.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        pie.add(desactivar);
        t.cuerpo().add(pie, BorderLayout.SOUTH);
        centro.add(t, BorderLayout.CENTER);
        centro.add(formulario(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);
    }

    private JComponent formulario() {
        Tarjeta t = new Tarjeta("Nueva promoción", null);
        JPanel p = Textos.transparente(null);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(fila(Textos.conEtiqueta("Producto", producto)));
        p.add(Box.createVerticalStrut(4));
        p.add(fila(referencia));
        p.add(Box.createVerticalStrut(10));
        p.add(fila(Textos.conEtiqueta("Nombre visible en caja", nombre)));
        p.add(Box.createVerticalStrut(12));
        precio.setFont(Tema.cifra(14));
        p.add(fila(Textos.conEtiqueta("Precio promocional con IGV (S/)", precio)));
        p.add(Box.createVerticalStrut(12));
        JPanel fechas = Textos.transparente(new GridLayout(1, 2, 10, 0));
        fechas.add(Textos.conEtiqueta("Desde", desde));
        fechas.add(Textos.conEtiqueta("Hasta", hasta));
        p.add(fila(fechas));
        p.add(Box.createVerticalStrut(18));
        Boton registrar = new Boton("Registrar promoción", Boton.Estilo.PRIMARIO, Icono.Tipo.PROMO);
        registrar.addActionListener(e -> registrar());
        p.add(fila(registrar));
        t.con(p);
        return Textos.ancho(t, 330);
    }

    private static JComponent fila(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        return c;
    }

    @Override
    public void alMostrar() {
        Producto antes = producto.seleccionado();
        producto.removeAllItems();
        for (Producto p : ctx.producto.buscarProductos("")) {
            producto.addItem(p);
        }
        if (antes != null) {
            producto.setSelectedItem(antes);
        }
        mostrarReferencia();
        if (desde.getText().isBlank()) {
            desde.setText(Fechas.texto(LocalDate.now()));
            hasta.setText(Fechas.texto(LocalDate.now().plusDays(7)));
        }
        cargar();
    }

    private void mostrarReferencia() {
        Producto p = producto.seleccionado();
        referencia.setText(p == null ? " " : "Precio normal: " + Tema.soles(p.getPrecioVenta()) + " · el promocional debe ser menor");
    }

    private void cargar() {
        lista.clear();
        lista.addAll(ctx.producto.listarPromociones());
        var m = Tablas.modelo(tabla);
        m.setRowCount(0);
        LocalDate hoy = LocalDate.now();
        for (Promocion p : lista) {
            String est = !p.isActivo() ? "Desactivada" : p.estaVigente(hoy) ? "Vigente"
                    : p.getFechaInicio().isAfter(hoy) ? "Programada" : "Vencida";
            m.addRow(new Object[]{p.getNombre(), p.getProducto().getNombre(), Tema.numero(p.getProducto().getPrecioVenta()),
                    Tema.numero(p.getPrecioPromocional()), p.getFechaInicio().format(Tema.FECHA),
                    p.getFechaFin().format(Tema.FECHA), est});
        }
    }

    private void registrar() {
        aviso.limpiar();
        double pr;
        try {
            pr = Double.parseDouble(precio.getText().trim().replace(',', '.'));
            precio.setError(false);
        } catch (NumberFormatException e) {
            precio.setError(true);
            aviso.mostrar(Aviso.Tipo.ERROR, "Escribe el precio promocional con números, por ejemplo 3.90.");
            return;
        }
        Promocion[] r = new Promocion[1];
        if (ejecutar(() -> r[0] = ctx.producto.registrarPromocion(producto.seleccionado(), nombre.getText(), pr,
                Fechas.leer(desde, "inicial"), Fechas.leer(hasta, "final"), (Administrador) ctx.getUsuario()))) {
            nombre.setText("");
            precio.setText("");
            cargar();
            exito("Promoción «" + r[0].getNombre() + "» registrada. Se aplicará sola en caja durante su vigencia.");
        }
    }

    private void desactivar() {
        int f = tabla.getSelectedRow();
        if (f < 0) {
            aviso.mostrar(Aviso.Tipo.ADVERTENCIA, "Selecciona una promoción de la lista.");
            return;
        }
        Promocion p = lista.get(f);
        if (!p.isActivo()) {
            aviso.mostrar(Aviso.Tipo.INFO, "Esa promoción ya está desactivada.");
            return;
        }
        if (Dialogos.confirmar(this, "¿Desactivar «" + p.getNombre() + "»?", "Las ventas ya registradas conservan su precio "
                + "(RN-03); las nuevas usarán el precio normal.", "Desactivar", Boton.Estilo.PELIGRO)
                && ejecutar(() -> ctx.producto.desactivarPromocion(p, ctx.getUsuario()))) {
            cargar();
            exito("Promoción desactivada.");
        }
    }
}
