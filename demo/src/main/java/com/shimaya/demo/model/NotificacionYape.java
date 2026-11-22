package com.shimaya.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion_yape")
@Data
public class NotificacionYape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer idNotificacion;

    @Column(length = 150)
    private String remitente;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "mensaje_original", columnDefinition = "TEXT")
    private String mensajeOriginal;

    @Column(nullable = false)
    private Boolean usado = false;

    @Column(name = "fecha_recepcion", nullable = false)
    private LocalDateTime fechaRecepcion = LocalDateTime.now();
}