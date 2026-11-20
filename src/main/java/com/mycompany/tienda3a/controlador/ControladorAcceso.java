package com.mycompany.tienda3a.controlador;

import com.mycompany.tienda3a.config.Configuracion;
import com.mycompany.tienda3a.excepcion.ExcepcionNegocio;
import com.mycompany.tienda3a.modelo.ResultadoOperacion;
import com.mycompany.tienda3a.modelo.Usuario;
import com.mycompany.tienda3a.repositorio.RepositorioUsuarios;
import java.time.LocalDateTime;

public class ControladorAcceso {
    public static final String MENSAJE_GENERICO = "Usuario o contraseña incorrectos. Revisa tus datos e inténtalo de nuevo.";

    private final RepositorioUsuarios usuarios;
    private final Bitacora bitacora;

    public ControladorAcceso(RepositorioUsuarios usuarios, Bitacora bitacora) {
        this.usuarios = usuarios;
        this.bitacora = bitacora;
    }

    public Usuario iniciarSesion(String nombreAcceso, String clave) {
        Usuario u = usuarios.buscarPorAcceso(nombreAcceso);
        if (u == null) {
            bitacora.registrar(null, "LOGIN", "usuario", null, ResultadoOperacion.FALLIDO,
                    "Nombre de acceso no registrado");
            throw new ExcepcionNegocio(MENSAJE_GENERICO);
        }
        if (!u.isActivo() || u.estaBloqueado()) {
            bitacora.registrar(u, "LOGIN", "usuario", u.getIdUsuario(), ResultadoOperacion.RECHAZADO,
                    u.isActivo() ? "Usuario bloqueado" : "Usuario inactivo");
            throw new ExcepcionNegocio(MENSAJE_GENERICO);
        }
        if (!u.autenticar(clave)) {
            u.registrarIntentoFallido();
            usuarios.guardar(u);
            bitacora.registrar(u, "LOGIN", "usuario", u.getIdUsuario(), ResultadoOperacion.FALLIDO,
                    "Intento fallido " + u.getIntentosFallidos());
            if (u.estaBloqueado()) {
                bitacora.registrar(u, "BLOQUEO", "usuario", u.getIdUsuario(), ResultadoOperacion.EXITO,
                        "Bloqueado " + Configuracion.getMinutosBloqueo() + " minutos (RN-14)");
            }
            throw new ExcepcionNegocio(MENSAJE_GENERICO);
        }
        u.reiniciarIntentos();
        usuarios.guardar(u);
        bitacora.registrar(u, "LOGIN", "usuario", u.getIdUsuario(), ResultadoOperacion.EXITO, u.getRol().name());
        return u;
    }

    public void cerrarSesion(Usuario usuario) {
        if (usuario != null) {
            bitacora.registrar(usuario, "LOGOUT", "usuario", usuario.getIdUsuario(), ResultadoOperacion.EXITO, null);
        }
    }

    public boolean verificarPermiso(Usuario usuario, String casoUso) {
        return usuario != null && usuario.isActivo() && usuario.puedeAcceder(casoUso);
    }

    public boolean sesionExpirada(LocalDateTime ultimaActividad) {
        return LocalDateTime.now().isAfter(ultimaActividad.plusMinutes(Configuracion.getMinutosInactividad()));
    }
}
