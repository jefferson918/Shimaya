// === SedeDAOImpl.java ===
package com.shimaya.demo.dao.impl;

import com.shimaya.demo.dao.SedeDAO;
import com.shimaya.demo.model.Sede;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class SedeDAOImpl implements SedeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Sede> obtenerTodas() {
        return entityManager.createQuery("SELECT s FROM Sede s", Sede.class).getResultList();
    }

    @Override
    public Sede obtenerPorId(int id) {
        return entityManager.find(Sede.class, id);
    }

    @Override
    public Sede buscarPorNombre(String nombre) {
        List<Sede> lista = entityManager.createQuery(
                "SELECT s FROM Sede s WHERE LOWER(s.nombre) LIKE LOWER(:nombre)", Sede.class)
                .setParameter("nombre", "%" + nombre.trim() + "%")
                .getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }
}