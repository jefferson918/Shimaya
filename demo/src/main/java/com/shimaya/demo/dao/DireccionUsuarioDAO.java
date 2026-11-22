package com.shimaya.demo.dao;

import com.shimaya.demo.model.DireccionUsuario;
import java.util.List;

public interface DireccionUsuarioDAO {
    List<DireccionUsuario> listarPorUsuario(int idUsuario);
    DireccionUsuario obtenerPorId(int idDireccion);
    DireccionUsuario guardar(DireccionUsuario direccion);
    void actualizar(DireccionUsuario direccion);
    void eliminar(int idDireccion);
}