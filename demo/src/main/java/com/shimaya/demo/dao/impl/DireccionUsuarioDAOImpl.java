package com.shimaya.demo.dao.impl;

import com.shimaya.demo.dao.DireccionUsuarioDAO;
import com.shimaya.demo.model.DireccionUsuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class DireccionUsuarioDAOImpl implements DireccionUsuarioDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DireccionUsuario> listarPorUsuario(int idUsuario) {
        return entityManager.createQuery(
                "FROM DireccionUsuario WHERE idUsuario = :idUsuario ORDER BY idDireccion ASC",
                DireccionUsuario.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }

    @Override
    public DireccionUsuario obtenerPorId(int idDireccion) {
        return entityManager.find(DireccionUsuario.class, idDireccion);
    }

    @Override
    public DireccionUsuario guardar(DireccionUsuario direccion) {
        entityManager.persist(direccion);
        return direccion;
    }

    @Override
    public void actualizar(DireccionUsuario direccion) {
        entityManager.merge(direccion);
    }

    @Override
    public void eliminar(int idDireccion) {
        DireccionUsuario dir = obtenerPorId(idDireccion);
        if (dir != null) {
            entityManager.remove(dir);
        }
    }
}