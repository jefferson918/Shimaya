package com.shimaya.demo.dao.impl;

import com.shimaya.demo.dao.NotificacionYapeDAO;
import com.shimaya.demo.model.NotificacionYape;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class NotificacionYapeDAOImpl implements NotificacionYapeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void guardar(NotificacionYape notificacion) {
        entityManager.persist(notificacion);
    }

    @Override
    public void actualizar(NotificacionYape notificacion) {
        entityManager.merge(notificacion);
    }

    @Override
    public NotificacionYape buscarPagoDisponible(BigDecimal monto, LocalDateTime desde) {
        TypedQuery<NotificacionYape> query = entityManager.createQuery(
                "SELECT n FROM NotificacionYape n " +
                "WHERE n.monto = :monto " +
                "AND n.usado = false " +
                "AND n.fechaRecepcion >= :desde " +
                "ORDER BY n.idNotificacion DESC",
                NotificacionYape.class
        );
        query.setParameter("monto", monto);
        query.setParameter("desde", desde);
        query.setMaxResults(1);

        List<NotificacionYape> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }
}