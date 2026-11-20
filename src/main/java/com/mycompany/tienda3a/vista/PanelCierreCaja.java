package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.modelo.Cajero;
import com.mycompany.tienda3a.modelo.CierreCaja;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class PanelCierreCaja extends PanelBase {
    private CierreCaja cierre;
    private final Indicador ventas = new Indicador("Ventas del día", Icono.Tipo.VENTAS, Tema.PETROLEO);
    private final Indicador efectivo = new Indicador("Cobros en efectivo", Icono.Tipo.EFECTIVO, Tema.VERDE);
    private final Indicador tarjeta = new Indicador("Cobros con tarjeta", Icono.Tipo.TARJETA, Tema.PETROLEO);
    private final Indicador billetera = new Indicador("Cobros con billetera", Icono.Tipo.BILLETERA, Tema.AMBAR);

    private final JLabel fondo = Textos.monto("", 15, Tema.TEXTO);
    private final JLabel cobros = Textos.monto("", 15, Tema.TEXTO);
    private final JLabel devuelto = Textos.monto("", 15, Tema.ROJO);
    private final JLabel esperado = Textos.monto("", 30, Tema.PETROLEO);
    private final JLabel totalDia = Textos.ayuda("");

    private final Campo contado = new Campo("0.00");
    private final JLabel diferencia = Textos.monto("S/ 0.00", 22, Tema.TEXTO_TENUE);
    private final Insignia estadoDif = new Insignia("Escribe el efectivo contado", Insignia.Tipo.NEUTRO);
    private final JTextArea observacion = new JTextArea(3, 20);
    private final JLabel etiquetaObs = Textos.etiqueta("Observación");
    private final Boton registrar = new Boton("Registrar cierre de caja", Boton.Estilo.EXITO, Icono.Tipo.CHECK).alto(50);

    PanelCierreCaja(Contexto ctx) {
        super(ctx, "Cierre de caja", "Arqueo diario: fondo inicial + cobros en efectivo − devoluciones.", Icono.Tipo.CIERRE, "CU-09");
        JPanel centro = Textos.transparente(new BorderLayout(0, 16));
        JPanel fila = Textos.transparente(new GridLayout(1, 4, 14, 0));
        fila.add(ventas);
        fila.add(efectivo);
        fila.add(tarjeta);
        fila.add(billetera);
        centro.add(fila, BorderLayout.NORTH);

        JPanel abajo = Textos.transparente(new GridLayout(1, 2, 16, 0));
        abajo.add(tarjetaArqueo());
        abajo.add(tarjetaRegistro());
        JPanel envoltura = Textos.transparente(new BorderLayout());
        envoltura.add(abajo, BorderLayout.NORTH);
        centro.add(envoltura, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    private JComponent tarjetaArqueo() {
        Tarjeta t = new Tarjeta("Efectivo esperado en caja", "Así lo calcula el sistema (RN-10)");
        JPanel p = Textos.transparente(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        linea(p, g, "Fondo inicial", fondo, "+");
        linea(p, g, "Cobros en efectivo", cobros, "+");
        linea(p, g, "Efectivo devuelto por anulaciones", devuelto, "−");
        g.gridx = 0;
        g.gridwidth = 3;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 0, 10, 0);
        JSeparator s = new JSeparator();
        s.setForeground(Tema.BORDE);
        p.add(s, g);
        g.gridy++;
        g.gridwidth = 2;
        g.insets = new Insets(0, 0, 0, 0);
        JLabel l = new JLabel("Efectivo esperado");
        l.setFont(Tema.medio(15));
        p.add(l, g);
        g.gridx = 2;
        g.gridwidth = 1;
        g.anchor = GridBagConstraints.EAST;
        p.add(esperado, g);
        g.gridy++;
        g.gridx = 0;
        g.gridwidth = 3;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(10, 0, 0, 0);
        p.add(totalDia, g);
        t.con(p);
        return t;
    }

    private void linea(JPanel p, GridBagConstraints g, String texto, JLabel valor, String signo) {
        g.gridx = 0;
        g.weightx = 0;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(5, 0, 5, 10);
        JLabel s = Textos.monto(signo, 15, Tema.TEXTO_TENUE);
        p.add(s, g);
        g.gridx = 1;
        g.weightx = 1;
        p.add(Textos.texto(texto, 13.5f, Tema.TEXTO), g);
        g.gridx = 2;
        g.weightx = 0;
        g.anchor = GridBagConstraints.EAST;
        g.insets = new Insets(5, 0, 5, 0);
        p.add(valor, g);
        g.gridy++;
    }

    private JComponent tarjetaRegistro() {
        Tarjeta t = new Tarjeta("Arqueo", "Cuenta el efectivo de la gaveta, incluido el fondo inicial");
        JPanel p = Textos.transparente(new BorderLayout(0, 12));
        JPanel fila = Textos.transparente(new GridLayout(1, 2, 14, 0));
        contado.setFont(Tema.cifra(18));
        contado.setHorizontalAlignment(JTextField.RIGHT);
        contado.setPreferredSize(new Dimension(10, 46));
        fila.add(Textos.conEtiqueta("Efectivo contado (S/)", contado));
        JPanel dif = Textos.transparente(new BorderLayout(0, 6));
        dif.add(Textos.etiqueta("Diferencia"), BorderLayout.NORTH);
        diferencia.setHorizontalAlignment(SwingConstants.RIGHT);
        dif.add(diferencia, BorderLayout.CENTER);
        fila.add(dif);
        p.add(fila, BorderLayout.NORTH);
        JPanel medio = Textos.transparente(new BorderLayout(0, 10));
        JPanel est = Textos.transparente(new FlowLayout(FlowLayout.LEFT, 0, 0));
        est.add(estadoDif);
        medio.add(est, BorderLayout.NORTH);
        observacion.setFont(Tema.texto(13));
        observacion.setLineWrap(true);
        observacion.setWrapStyleWord(true);
        observacion.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane so = Tablas.desplazable(observacion);
        so.setBorder(BorderFactory.createLineBorder(Tema.BORDE_FUERTE));
        JPanel obs = Textos.transparente(new BorderLayout(0, 6));
        obs.add(etiquetaObs, BorderLayout.NORTH);
        obs.add(so, BorderLayout.CENTER);
        medio.add(obs, BorderLayout.CENTER);
        p.add(medio, BorderLayout.CENTER);
        registrar.addActionListener(e -> registrar());
        p.add(registrar, BorderLayout.SOUTH);
        contado.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarDiferencia(); }
            public void removeUpdate(DocumentEvent e) { actualizarDiferencia(); }
            public void changedUpdate(DocumentEvent e) { actualizarDiferencia(); }
        });
        t.con(p);
        return t;
    }

    @Override
    public void alMostrar() {
        Cajero cajero = (Cajero) ctx.getUsuario();
        CierreCaja hecho = ctx.caja.cierreDelDia(cajero, ctx.getCaja(), LocalDate.now());
        cierre = hecho != null ? hecho : ctx.caja.prepararCierre(cajero, ctx.getCaja(), LocalDate.now());
        ventas.setValor(String.valueOf(cierre.getCantidadVentas()),
                cierre.getCantidadAnuladas() + (cierre.getCantidadAnuladas() == 1 ? " anulada" : " anuladas"));
        efectivo.setValor(Tema.soles(cierre.getTotalEfectivo()), "");
        tarjeta.setValor(Tema.soles(cierre.getTotalTarjeta()), "");
        billetera.setValor(Tema.soles(cierre.getTotalBilletera()), "");
        fondo.setText(Tema.soles(cierre.getFondoInicial()));
        cobros.setText(Tema.soles(cierre.getTotalEfectivo()));
        devuelto.setText(Tema.soles(cierre.getDevueltoEfectivo()));
        esperado.setText(Tema.soles(cierre.getEfectivoEsperado()));
        totalDia.setText("Total cobrado hoy en todos los medios: " + Tema.soles(cierre.getTotalCobrado()));
        boolean cerrado = hecho != null;
        contado.setEnabled(!cerrado);
        observacion.setEnabled(!cerrado);
        registrar.setEnabled(!cerrado);
        if (cerrado) {
            contado.setText(Tema.numero(hecho.getEfectivoContado()).replace(",", ""));
            observacion.setText(hecho.getObservacion());
            aviso.mostrar(Aviso.Tipo.INFO, "Ya registraste el cierre de " + ctx.getCaja().getCodigo() + " hoy a las "
                    + hecho.getFechaHoraCierre().format(Tema.HORA) + ". Solo se permite un cierre por cajero, caja y día (RN-10).");
        } else if (ctx.venta.tieneVentaEnCurso(cajero)) {
            aviso.mostrar(Aviso.Tipo.ADVERTENCIA, "Tienes una venta en curso. Confírmala o cancélala en «Nueva venta» antes de cerrar la caja.");
        }
        actualizarDiferencia();
    }

    private void actualizarDiferencia() {
        String t = contado.getText().trim().replace(",", "");
        contado.setError(false);
        if (t.isEmpty()) {
            diferencia.setText("S/ 0.00");
            diferencia.setForeground(Tema.TEXTO_TENUE);
            estadoDif.setText("Escribe el efectivo contado");
            estadoDif.setTipo(Insignia.Tipo.NEUTRO);
            etiquetaObs.setText("Observación (opcional)");
            return;
        }
        try {
            double d = Math.round((Double.parseDouble(t) - cierre.getEfectivoEsperado()) * 100) / 100.0;
            diferencia.setText((d > 0 ? "+" : d < 0 ? "−" : "") + Tema.soles(Math.abs(d)));
            if (Math.abs(d) < 0.005) {
                diferencia.setForeground(Tema.VERDE);
                estadoDif.setText("La caja cuadra");
                estadoDif.setTipo(Insignia.Tipo.EXITO);
                etiquetaObs.setText("Observación (opcional)");
            } else {
                diferencia.setForeground(d < 0 ? Tema.ROJO : Tema.AMBAR);
                estadoDif.setText(d < 0 ? "Falta efectivo" : "Sobra efectivo");
                estadoDif.setTipo(d < 0 ? Insignia.Tipo.PELIGRO : Insignia.Tipo.ADVERTENCIA);
                etiquetaObs.setText("Observación (obligatoria: explica la diferencia)");
            }
        } catch (NumberFormatException e) {
            diferencia.setText("—");
            estadoDif.setText("Escribe solo números");
            estadoDif.setTipo(Insignia.Tipo.PELIGRO);
        }
    }

    private void registrar() {
        double c;
        try {
            c = Double.parseDouble(contado.getText().trim().replace(",", ""));
        } catch (NumberFormatException e) {
            contado.setError(true);
            aviso.mostrar(Aviso.Tipo.ERROR, "Escribe el efectivo contado con números, por ejemplo 523.40.");
            return;
        }
        if (!Dialogos.confirmar(this, "¿Registrar el cierre de " + ctx.getCaja().getCodigo() + "?",
                "Efectivo esperado " + Tema.soles(cierre.getEfectivoEsperado()) + " · contado " + Tema.soles(c)
                + ". Después del cierre no se registran más ventas en esta caja hoy.", "Registrar cierre", Boton.Estilo.EXITO)) {
            return;
        }
        if (ejecutar(() -> ctx.caja.registrarCierre(cierre, c, observacion.getText()))) {
            alMostrar();
            exito("Cierre registrado. Diferencia: " + Tema.soles(cierre.getDiferencia()) + ".");
        }
    }
}
