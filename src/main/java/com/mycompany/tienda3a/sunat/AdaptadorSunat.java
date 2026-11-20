package com.mycompany.tienda3a.sunat;

import com.mycompany.tienda3a.modelo.Comprobante;
import com.mycompany.tienda3a.modelo.RespuestaSunat;

public interface AdaptadorSunat {
    RespuestaSunat enviar(Comprobante comprobante);

    RespuestaSunat comunicarAnulacion(Comprobante comprobante);
}
