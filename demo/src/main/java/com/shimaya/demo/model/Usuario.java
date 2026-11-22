package com.shimaya.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellidos", length = 100)
    private String apellidos;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "direccion_habitual")
    private String direccionHabitual;

    @Column(name = "puntos_fidelidad")
    private Integer puntosFidelidad = 0;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "proveedor_auth")
    private String proveedorAuth = "LOCAL";

    @Column(name = "rol")
    private String rol = "CLIENTE";

    @Column(name = "codigo_verificacion", length = 6)
    private String codigoVerificacion;

    @Column(name = "estado_cuenta")
    private String estadoCuenta = "PENDIENTE";

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "estado_sesion")
    private String estadoSesion = "INACTIVO";
}