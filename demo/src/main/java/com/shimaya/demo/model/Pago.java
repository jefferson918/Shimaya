package com.shimaya.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Data
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @Column(name = "id_pedido", unique = true, nullable = false)
    private Integer idPedido;

    @Column(name = "id_metodo", nullable = false)
    private Integer idMetodo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "num_operacion", length = 20, nullable = false)
    private String numOperacion;

    @Column(name = "telefono_yape", length = 20)
    private String telefonoYape;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora = LocalDateTime.now();

    @Column(name = "estado_pago", length = 30)
    private String estadoPago = "COMPLETADO";
}