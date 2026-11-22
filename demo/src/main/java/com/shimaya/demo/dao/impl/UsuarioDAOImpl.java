package com.shimaya.demo.dao.impl;

import com.shimaya.demo.dao.UsuarioDAO;
import com.shimaya.demo.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class UsuarioDAOImpl implements UsuarioDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Usuario> obtenerTodos() {
        TypedQuery<Usuario> query = entityManager.createQuery("FROM Usuario", Usuario.class);
        return query.getResultList();
    }

    @Override
    public Usuario obtenerPorId(int id) {
        return entityManager.find(Usuario.class, id);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        try {
            TypedQuery<Usuario> query = entityManager.createQuery("FROM Usuario WHERE email = :email", Usuario.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        entityManager.persist(usuario);
        return usuario;
    }

    @Override
    public void actualizar(Usuario usuario) {
        entityManager.merge(usuario);
    }

    @Override
    public void eliminar(int id) {
        Usuario usuario = obtenerPorId(id);
        if (usuario != null) {
            entityManager.remove(usuario);
        }
    }
}