package com.shimaya.demo.dao.impl;

import com.shimaya.demo.dao.CarritoDAO;
import com.shimaya.demo.model.Carrito;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CarritoDAOImpl implements CarritoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Carrito buscarPorId(int id) {
        return entityManager.find(Carrito.class, id);
    }

    @Override
    public void guardar(Carrito carrito) {
        entityManager.persist(carrito);
    }

    @Override
    public void actualizar(Carrito carrito) {
        entityManager.merge(carrito);
    }

    @Override
    public void eliminar(int id) {
        Carrito carrito = buscarPorId(id);
        if (carrito != null) {
            entityManager.remove(carrito);
        }
    }
}