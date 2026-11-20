package com.mycompany.tienda3a.controlador;

import com.mycompany.tienda3a.excepcion.ExcepcionNegocio;
import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.repositorio.RepositorioVentas;
import com.mycompany.tienda3a.sunat.AdaptadorSunat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ControladorComprobante {
    private final RepositorioVentas ventas;
    private final AdaptadorSunat adaptadorSunat;
    private final Bitacora bitacora;

    public ControladorComprobante(RepositorioVentas ventas, AdaptadorSunat adaptadorSunat, Bitacora bitacora) {
        this.ventas = ventas;
        this.adaptadorSunat = adaptadorSunat;
        this.bitacora = bitacora;
    }

    public Comprobante emitir(Venta venta, TipoComprobante tipo) {
        String serie = venta.getCaja().obtenerSerie(tipo);
        int correlativo = ventas.siguienteCorrelativo(serie);
        LocalDateTime ahora = LocalDateTime.now();
        Comprobante c = switch (tipo) {
            case VENTA_SIMPLE -> new VentaSimpleComprobante(0, venta, serie, correlativo, ahora);
            case BOLETA -> new Boleta(0, venta, serie, correlativo, ahora);
            case FACTURA -> new Factura(0, venta, serie, correlativo, ahora);
        };
        if (!c.validarDatos()) {
            throw new ExcepcionNegocio("Los datos del cliente no corresponden al tipo de comprobante elegido.");
        }
        venta.setComprobante(c);
        return c;
    }

    public void enviar(Comprobante comprobante) {
        comprobante.registrarEnvio(LocalDateTime.now());
        RespuestaSunat respuesta = adaptadorSunat.enviar(comprobante);
        String mensaje = switch (respuesta) {
            case ACEPTADO -> "Comprobante aceptado.";
            case RECHAZADO -> "El simulador rechazó el comprobante: revise los datos del cliente.";
            case SIN_RESPUESTA -> "Sin respuesta (intento " + comprobante.getIntentosEnvio() + ").";
        };
        comprobante.registrarRespuesta(respuesta, mensaje);
        ventas.actualizarComprobante(comprobante);
    }

    public int reintentarPendientes() {
        int enviados = 0;
        LocalDateTime ahora = LocalDateTime.now();
        for (Comprobante c : todos()) {
            if (c.puedeReintentar(ahora)) {
                enviar(c);
                enviados++;
            }
        }
        return enviados;
    }

    public List<Comprobante> listarPendientes() {
        List<Comprobante> lista = new ArrayList<>();
        for (Comprobante c : todos()) {
            if (c.getEstado() == EstadoComprobante.PENDIENTE || c.getEstado() == EstadoComprobante.RECHAZADO) {
                lista.add(c);
            }
        }
        return lista;
    }

    public List<Comprobante> todos() {
        List<Comprobante> lista = new ArrayList<>();
        for (Venta v : ventas.listar()) {
            if (v.getComprobante() != null) {
                lista.add(v.getComprobante());
            }
        }
        return lista;
    }

    public void reenviar(Comprobante comprobante, Usuario administrador) {
        if (comprobante.getEstado() != EstadoComprobante.PENDIENTE
                && comprobante.getEstado() != EstadoComprobante.RECHAZADO) {
            throw new ExcepcionNegocio("Solo se reenvían comprobantes pendientes o rechazados.");
        }
        enviar(comprobante);
        bitacora.registrar(administrador, "REENVIAR_COMPROBANTE", "comprobante", comprobante.getIdComprobante(),
                ResultadoOperacion.EXITO, comprobante.getNumero() + " → " + comprobante.getEstado());
    }

    public void registrarRevision(Comprobante comprobante, String observacion, Usuario administrador) {
        try {
            comprobante.registrarRevision(observacion);
        } catch (IllegalArgumentException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
        ventas.actualizarComprobante(comprobante);
        bitacora.registrar(administrador, "REVISAR_COMPROBANTE", "comprobante", comprobante.getIdComprobante(),
                ResultadoOperacion.EXITO, comprobante.getNumero());
    }

    public RespuestaSunat comunicarAnulacion(Comprobante comprobante) {
        return adaptadorSunat.comunicarAnulacion(comprobante);
    }

    public Comprobante buscarPorNumero(String numero) {
        if (numero == null) {
            return null;
        }
        String n = numero.trim().toUpperCase();
        for (Comprobante c : todos()) {
            if (c.getNumero().equals(n) || (c.getSerie() + "-" + c.getCorrelativo()).equals(n)) {
                return c;
            }
        }
        return null;
    }
}
