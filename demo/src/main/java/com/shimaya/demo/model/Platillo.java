// === Platillo.java ===
package com.shimaya.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "platillo")
@Data
public class Platillo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPlatillo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    private Boolean disponible = true;

    @Column(name = "url_imagen")
    private String urlImagen;

    @Column(name = "id_categoria")
    private Integer idCategoria;

    @OneToMany(mappedBy = "platillo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DetallePlatillo> presentaciones;
}