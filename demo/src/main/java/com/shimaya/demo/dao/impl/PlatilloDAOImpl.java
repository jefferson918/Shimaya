package com.shimaya.demo.dao.impl;

import com.shimaya.demo.dao.PlatilloDAO;
import com.shimaya.demo.model.Platillo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class PlatilloDAOImpl implements PlatilloDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Platillo> obtenerTodos() {
        return entityManager.createQuery("SELECT p FROM Platillo p", Platillo.class).getResultList();
    }

    @Override
    public Platillo obtenerPorId(int id) {
        return entityManager.find(Platillo.class, id);
    }

    @Override
    public Platillo guardar(Platillo platillo) {
        entityManager.persist(platillo);
        return platillo;
    }

    @Override
    public void actualizar(Platillo platillo) {
        entityManager.merge(platillo);
    }

    @Override
    public void eliminar(int id) {
        Platillo platillo = obtenerPorId(id);
        if (platillo != null) {
            entityManager.remove(platillo);
        }
    }
}