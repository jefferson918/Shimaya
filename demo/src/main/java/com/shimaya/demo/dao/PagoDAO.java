package com.shimaya.demo.dao;

import com.shimaya.demo.model.Pago;

public interface PagoDAO {
    Pago buscarPorId(int id);
    void guardar(Pago pago);
    void actualizar(Pago pago);
    void eliminar(int id);
}