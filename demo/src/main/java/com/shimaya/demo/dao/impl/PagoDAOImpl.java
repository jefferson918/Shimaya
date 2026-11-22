package com.shimaya.demo.dao.impl;

import com.shimaya.demo.dao.PagoDAO;
import com.shimaya.demo.model.Pago;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PagoDAOImpl implements PagoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Pago buscarPorId(int id) {
        return entityManager.find(Pago.class, id);
    }

    @Override
    public void guardar(Pago pago) {
        entityManager.persist(pago);
    }

    @Override
    public void actualizar(Pago pago) {
        entityManager.merge(pago);
    }

    @Override
    public void eliminar(int id) {
        Pago pago = buscarPorId(id);
        if (pago != null) {
            entityManager.remove(pago);
        }
    }
}