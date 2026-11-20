package com.mycompany.tienda3a.repositorio;

import com.mycompany.tienda3a.modelo.Usuario;
import java.util.List;

public interface RepositorioUsuarios {
    List<Usuario> listar();

    Usuario buscarPorAcceso(String nombreAcceso);

    Usuario buscarPorId(int idUsuario);

    void guardar(Usuario usuario);
}
