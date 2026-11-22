package com.shimaya.demo.dao;

import com.shimaya.demo.model.Usuario;
import java.util.List;

public interface UsuarioDAO {
    List<Usuario> obtenerTodos();
    Usuario obtenerPorId(int id);
    Usuario buscarPorEmail(String email);
    Usuario guardar(Usuario usuario);
    void actualizar(Usuario usuario);
    void eliminar(int id);
}