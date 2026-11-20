package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.controlador.ControladorProducto;
import com.mycompany.tienda3a.modelo.Auditoria;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;

class PanelBitacora extends PanelBase {
    private final Campo desde = new Campo("dd/mm/aaaa");
    private final Campo hasta = new Campo("dd/mm/aaaa");
    private final Campo texto = new Campo("Usuario, acción o detalle");
    private final JTable tabla = Tablas.crear("Fecha y hora", "Usuario", "Acción", "Tabla", "Registro", "Resultado", "Detalle");
    private final JLabel conteo = Textos.ayuda("");

    PanelBitacora(Contexto ctx) {
        super(ctx, "Bitácora", "Accesos, ventas, anulaciones, cierres y cambios de la administración.", Icono.Tipo.BITACORA, "CU-10");
        Tarjeta t = new Tarjeta();
        JPanel filtros = Textos.transparente(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.SOUTH;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 0, 10);
        desde.setPreferredSize(new Dimension(130, 40));
        hasta.setPreferredSize(new Dimension(130, 40));
        filtros.add(Textos.conEtiqueta("Desde", desde), g);
        filtros.add(Textos.conEtiqueta("Hasta", hasta), g);
        g.weightx = 1;
        texto.conIcono(Icono.Tipo.BUSCAR);
        filtros.add(Textos.conEtiqueta("Buscar", texto), g);
        g.weightx = 0;
        g.insets = new Insets(0, 0, 0, 0);
        Boton b = new Boton("Filtrar", Boton.Estilo.PRIMARIO);
        b.addActionListener(e -> cargar());
        filtros.add(b, g);
        filtros.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        t.cuerpo().add(filtros, BorderLayout.NORTH);
        Tablas.ancho(tabla, 0, 132, true);
        Tablas.ancho(tabla, 1, 130, true);
        Tablas.ancho(tabla, 2, 170, true);
        Tablas.ancho(tabla, 3, 110, true);
        Tablas.ancho(tabla, 4, 76, true);
        Tablas.ancho(tabla, 5, 120, true);
        Tablas.insignia(tabla, 5, v -> switch (String.valueOf(v)) {
            case "Éxito" -> Insignia.Tipo.EXITO;
            case "Rechazado" -> Insignia.Tipo.ADVERTENCIA;
            default -> Insignia.Tipo.PELIGRO;
        });
        t.con(Tablas.envolver(tabla));
        conteo.setBorder(BorderFactory.createEmptyBorder(10, 2, 0, 0));
        t.cuerpo().add(conteo, BorderLayout.SOUTH);
        add(t, BorderLayout.CENTER);
        desde.setText(Fechas.texto(LocalDate.now().minusDays(6)));
        hasta.setText(Fechas.texto(LocalDate.now()));
        texto.addActionListener(e -> cargar());
    }

    @Override
    public void alMostrar() {
        cargar();
    }

    private void cargar() {
        aviso.limpiar();
        ejecutar(() -> {
            String f = ControladorProducto.normalizar(texto.getText());
            var m = Tablas.modelo(tabla);
            m.setRowCount(0);
            int n = 0;
            for (Auditoria a : ctx.consulta.consultarBitacora(Fechas.leer(desde, "inicial"), Fechas.leer(hasta, "final"))) {
                String usuario = a.getUsuario() == null ? "(sin usuario)" : a.getUsuario().getNombreAcceso();
                String linea = ControladorProducto.normalizar(usuario + " " + a.getAccion() + " " + a.getDetalle());
                if (!f.isEmpty() && !linea.contains(f)) {
                    continue;
                }
                m.addRow(new Object[]{a.getFechaHora().format(Tema.FECHA_HORA), usuario, a.getAccion(), a.getEntidad(),
                        a.getRegistroId() == null ? "" : a.getRegistroId(), a.getResultado().getEtiqueta(),
                        a.getDetalle() == null ? "" : a.getDetalle()});
                n++;
            }
            conteo.setText(n + " registros. Nunca se guardan contraseñas (RNF-08).");
        });
    }
}
