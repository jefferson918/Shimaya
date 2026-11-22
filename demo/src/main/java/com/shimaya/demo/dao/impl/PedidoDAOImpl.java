package com.shimaya.demo.dao.impl;

import com.shimaya.demo.dao.PedidoDAO;
import com.shimaya.demo.model.Pedido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PedidoDAOImpl implements PedidoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Pedido buscarPorId(int id) {
        return entityManager.find(Pedido.class, id);
    }

    @Override
    public void guardar(Pedido pedido) {
        entityManager.persist(pedido);
    }

    @Override
    public void actualizar(Pedido pedido) {
        entityManager.merge(pedido);
    }

    @Override
    public void eliminar(int id) {
        Pedido pedido = buscarPorId(id);
        if (pedido != null) {
            entityManager.remove(pedido);
        }
    }
}