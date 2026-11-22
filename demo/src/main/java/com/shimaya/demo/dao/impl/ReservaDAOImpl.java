package com.shimaya.demo.dao.impl;

import com.shimaya.demo.dao.ReservaDAO;
import com.shimaya.demo.model.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ReservaDAOImpl implements ReservaDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Reserva guardar(Reserva reserva) {
        entityManager.persist(reserva);
        return reserva;
    }
}