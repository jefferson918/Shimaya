// === SedeDAO.java ===
package com.shimaya.demo.dao;

import com.shimaya.demo.model.Sede;
import java.util.List;

public interface SedeDAO {
    List<Sede> obtenerTodas();
    Sede obtenerPorId(int id);
    Sede buscarPorNombre(String nombre);
}