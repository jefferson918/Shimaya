package com.shimaya.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "direccion_usuario")
@Data
public class DireccionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    private Integer idDireccion;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(nullable = false, length = 50)
    private String etiqueta;

    @Column(nullable = false, length = 100)
    private String distrito;

    @Column(name = "direccion_exacta", nullable = false, length = 255)
    private String direccionExacta;

    private String referencia;
}