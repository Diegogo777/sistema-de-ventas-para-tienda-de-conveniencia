package com.mycompany.tienda3a.sunat;

import com.mycompany.tienda3a.modelo.Comprobante;
import com.mycompany.tienda3a.modelo.RespuestaSunat;
import java.util.Random;

public class SimuladorSunat implements AdaptadorSunat {
    private final Random azar;
    private final int porcentajeSinRespuesta;
    private final int porcentajeRechazo;

    public SimuladorSunat() {
        this(new Random(), 8, 4);
    }

    public SimuladorSunat(Random azar, int porcentajeSinRespuesta, int porcentajeRechazo) {
        this.azar = azar;
        this.porcentajeSinRespuesta = porcentajeSinRespuesta;
        this.porcentajeRechazo = porcentajeRechazo;
    }

    @Override
    public RespuestaSunat enviar(Comprobante comprobante) {
        int n = azar.nextInt(100);
        if (n < porcentajeRechazo) {
            return RespuestaSunat.RECHAZADO;
        }
        if (n < porcentajeRechazo + porcentajeSinRespuesta) {
            return RespuestaSunat.SIN_RESPUESTA;
        }
        return RespuestaSunat.ACEPTADO;
    }

    @Override
    public RespuestaSunat comunicarAnulacion(Comprobante comprobante) {
        return RespuestaSunat.ACEPTADO;
    }
}
