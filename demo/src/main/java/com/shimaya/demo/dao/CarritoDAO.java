package com.shimaya.demo.dao;

import com.shimaya.demo.model.Carrito;

public interface CarritoDAO {
    Carrito buscarPorId(int id);
    void guardar(Carrito carrito);
    void actualizar(Carrito carrito);
    void eliminar(int id);
}