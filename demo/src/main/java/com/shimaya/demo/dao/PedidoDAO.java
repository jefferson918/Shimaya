package com.shimaya.demo.dao;

import com.shimaya.demo.model.Pedido;

public interface PedidoDAO {
    Pedido buscarPorId(int id);
    void guardar(Pedido pedido);
    void actualizar(Pedido pedido);
    void eliminar(int id);
}