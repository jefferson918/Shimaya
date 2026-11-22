// === DetallePlatillo.java ===
package com.shimaya.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_platillo")
@Data
public class DetallePlatillo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetallePlatillo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_platillo", nullable = false)
    private Platillo platillo;

    @Column(nullable = false, length = 100)
    private String presentacion;

    @Column(name = "precio_adicional", precision = 10, scale = 2)
    private BigDecimal precioAdicional;
}