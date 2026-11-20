package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

public class Auditoria {
    private int idAuditoria;
    private Usuario usuario;
    private LocalDateTime fechaHora;
    private String accion;
    private String entidad;
    private String registroId;
    private ResultadoOperacion resultado;
    private String detalle;

    public Auditoria(int idAuditoria, Usuario usuario, LocalDateTime fechaHora, String accion, String entidad,
                     String registroId, ResultadoOperacion resultado, String detalle) {
        this.idAuditoria = idAuditoria;
        this.usuario = usuario;
        this.fechaHora = fechaHora == null ? LocalDateTime.now() : fechaHora;
        this.accion = accion;
        this.entidad = entidad;
        this.registroId = registroId;
        this.resultado = resultado;
        this.detalle = detalle;
    }

    public int getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(int idAuditoria) { this.idAuditoria = idAuditoria; }
    public Usuario getUsuario() { return usuario; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getAccion() { return accion; }
    public String getEntidad() { return entidad; }
    public String getRegistroId() { return registroId; }
    public ResultadoOperacion getResultado() { return resultado; }
    public String getDetalle() { return detalle; }
}
