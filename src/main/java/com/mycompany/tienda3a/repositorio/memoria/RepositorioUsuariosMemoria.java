package com.mycompany.tienda3a.repositorio.memoria;

import com.mycompany.tienda3a.modelo.Usuario;
import com.mycompany.tienda3a.repositorio.RepositorioUsuarios;
import java.util.ArrayList;
import java.util.List;

public class RepositorioUsuariosMemoria implements RepositorioUsuarios {
    protected final List<Usuario> usuarios = new ArrayList<>();
    private int ultimoId;

    @Override
    public List<Usuario> listar() {
        return new ArrayList<>(usuarios);
    }

    @Override
    public Usuario buscarPorAcceso(String nombreAcceso) {
        if (nombreAcceso == null) {
            return null;
        }
        for (Usuario u : usuarios) {
            if (u.getNombreAcceso().equalsIgnoreCase(nombreAcceso.trim())) {
                return u;
            }
        }
        return null;
    }

    @Override
    public Usuario buscarPorId(int idUsuario) {
        for (Usuario u : usuarios) {
            if (u.getIdUsuario() == idUsuario) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void guardar(Usuario usuario) {
        if (usuario.getIdUsuario() == 0) {
            usuario.setIdUsuario(++ultimoId);
        }
        ultimoId = Math.max(ultimoId, usuario.getIdUsuario());
        Usuario anterior = buscarPorId(usuario.getIdUsuario());
        if (anterior == null) {
            usuarios.add(usuario);
        } else if (anterior != usuario) {
            usuarios.set(usuarios.indexOf(anterior), usuario);
        }
    }

    public void guardarCargado(Usuario usuario) {
        usuarios.add(usuario);
        ultimoId = Math.max(ultimoId, usuario.getIdUsuario());
    }
}
