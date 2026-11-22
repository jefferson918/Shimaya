package com.shimaya.demo.dao;

import com.shimaya.demo.model.NotificacionYape;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface NotificacionYapeDAO {
    void guardar(NotificacionYape notificacion);
    void actualizar(NotificacionYape notificacion);
    NotificacionYape buscarPagoDisponible(BigDecimal monto, LocalDateTime desde);
}