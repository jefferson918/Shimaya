package com.shimaya.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_carrito")
@Data
public class DetalleCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalleCarrito;

    @Column(name = "id_carrito", nullable = false)
    private Integer idCarrito;

    @Column(name = "id_platillo", nullable = false)
    private Integer idPlatillo;

    @Column(name = "id_detalle_platillo")
    private Integer idDetallePlatillo;

    @Column(nullable = false)
    private Integer cantidad = 1;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(length = 255)
    private String observaciones;
}