package com.shimaya.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Data
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "id_sede")
    private Integer idSede;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora = LocalDateTime.now();

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "estado_pedido", length = 30)
    private String estadoPedido = "PENDIENTE";

    @Column(name = "tipo_entrega", length = 20, nullable = false)
    private String tipoEntrega;

    @Column(name = "direccion_envio", length = 255)
    private String direccionEnvio;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}