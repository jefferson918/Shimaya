package com.shimaya.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "metodo_pago")
@Data
public class MetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMetodo;

    @Column(name = "nombre_metodo", nullable = false, length = 50)
    private String nombreMetodo;

    @Column(length = 255)
    private String descripcion;
}