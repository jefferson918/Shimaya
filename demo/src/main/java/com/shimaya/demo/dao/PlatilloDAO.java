package com.shimaya.demo.dao;

import com.shimaya.demo.model.Platillo;
import java.util.List;

public interface PlatilloDAO {
    List<Platillo> obtenerTodos();
    Platillo obtenerPorId(int id);
    Platillo guardar(Platillo platillo);
    void actualizar(Platillo platillo);
    void eliminar(int id);
}