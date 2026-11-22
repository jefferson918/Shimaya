package com.shimaya.demo.controller;

import com.shimaya.demo.dao.DireccionUsuarioDAO;
import com.shimaya.demo.dao.UsuarioDAO;
import com.shimaya.demo.model.DireccionUsuario;
import com.shimaya.demo.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
@CrossOrigin(origins = "*")
public class PerfilController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired(required = false)
    private DireccionUsuarioDAO direccionUsuarioDAO;

    @GetMapping("/por-email")
    public ResponseEntity<?> obtenerPorEmail(@RequestParam("email") String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("El correo es obligatorio.");
        }
        Usuario usuario = usuarioDAO.buscarPorEmail(email.trim());
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable("id") int id) {
        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/actualizar-por-email")
    public ResponseEntity<?> actualizarPorEmail(@RequestBody Map<String, String> datos) {
        String email = datos.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("El correo es obligatorio.");
        }
        Usuario usuario = usuarioDAO.buscarPorEmail(email.trim());
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        if (datos.get("nombre") != null && !datos.get("nombre").isBlank()) {
            usuario.setNombre(datos.get("nombre").trim());
        }
        if (datos.get("apellidos") != null) {
            usuario.setApellidos(datos.get("apellidos").trim());
        }
        if (datos.get("telefono") != null) {
            usuario.setTelefono(datos.get("telefono").trim());
        }
        if (datos.get("direccionHabitual") != null) {
            usuario.setDireccionHabitual(datos.get("direccionHabitual").trim());
        }
        if (datos.get("password") != null && !datos.get("password").isBlank()) {
            usuario.setPassword(datos.get("password").trim());
        }

        usuarioDAO.actualizar(usuario);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/direcciones")
    public ResponseEntity<?> listarDirecciones(@RequestParam("email") String email) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email != null ? email.trim() : "");
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        if (direccionUsuarioDAO == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<DireccionUsuario> direcciones = direccionUsuarioDAO.listarPorUsuario(usuario.getIdUsuario());
        if (direcciones.isEmpty() && usuario.getDireccionHabitual() != null && !usuario.getDireccionHabitual().isBlank()) {
            DireccionUsuario inicial = new DireccionUsuario();
            inicial.setIdUsuario(usuario.getIdUsuario());
            inicial.setEtiqueta("CASA");
            inicial.setDistrito("Lima");
            inicial.setDireccionExacta(usuario.getDireccionHabitual().trim());
            inicial.setReferencia("");
            direccionUsuarioDAO.guardar(inicial);
            direcciones = direccionUsuarioDAO.listarPorUsuario(usuario.getIdUsuario());
        }
        return ResponseEntity.ok(direcciones);
    }

    @PostMapping("/direcciones")
    public ResponseEntity<?> agregarDireccion(@RequestBody Map<String, Object> payload) {
        String email = payload.get("email") != null ? payload.get("email").toString().trim() : "";
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario == null || direccionUsuarioDAO == null) {
            return ResponseEntity.notFound().build();
        }

        DireccionUsuario nueva = new DireccionUsuario();
        nueva.setIdUsuario(usuario.getIdUsuario());
        nueva.setEtiqueta(payload.get("etiqueta") != null ? payload.get("etiqueta").toString().toUpperCase().trim() : "CASA");
        nueva.setDistrito(payload.get("distrito") != null ? payload.get("distrito").toString().trim() : "Lima");
        nueva.setDireccionExacta(payload.get("direccionExacta") != null ? payload.get("direccionExacta").toString().trim() : "");
        nueva.setReferencia(payload.get("referencia") != null ? payload.get("referencia").toString().trim() : "");

        direccionUsuarioDAO.guardar(nueva);

        if (Boolean.TRUE.equals(payload.get("principal")) || usuario.getDireccionHabitual() == null || usuario.getDireccionHabitual().isBlank()) {
            usuario.setDireccionHabitual(nueva.getDireccionExacta() + " (" + nueva.getDistrito() + ")");
            usuarioDAO.actualizar(usuario);
        }
        return ResponseEntity.ok(direccionUsuarioDAO.listarPorUsuario(usuario.getIdUsuario()));
    }

    @GetMapping("/pedidos")
    public ResponseEntity<?> listarPedidos(@RequestParam("email") String email) {
        try {
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body("El correo es obligatorio.");
            }
            Usuario usuario = usuarioDAO.buscarPorEmail(email.trim());
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }

            String sqlPedidos = "SELECT id_pedido, fecha_hora, monto_total, estado_pedido, " +
                                "tipo_entrega, direccion_envio, observaciones " +
                                "FROM pedido WHERE id_usuario = :idUsuario " +
                                "ORDER BY id_pedido DESC";

            @SuppressWarnings("unchecked")
            List<Object[]> filasPedidos = entityManager.createNativeQuery(sqlPedidos)
                    .setParameter("idUsuario", usuario.getIdUsuario())
                    .getResultList();

            List<Object> resultado = new ArrayList<>();
            for (Object[] p : filasPedidos) {
                int idPedido = ((Number) p[0]).intValue();
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("idPedido", idPedido);
                item.put("fechaHora", p[1] != null ? String.valueOf(p[1]) : "");
                item.put("montoTotal", p[2] != null ? p[2] : 0);
                item.put("estadoPedido", p[3] != null ? String.valueOf(p[3]) : "EN PREPARACIÓN");
                item.put("tipoEntrega", p[4] != null ? String.valueOf(p[4]) : "DELIVERY");
                item.put("direccionEnvio", p[5] != null ? String.valueOf(p[5]) : "");
                item.put("observaciones", p[6] != null ? String.valueOf(p[6]) : "");

                // Obtener productos asociados desde detalle_pedido + platillo
                List<Object> detalleItems = new ArrayList<>();
                List<String> nombresProductos = new ArrayList<>();
                int cantidadTotal = 0;

                try {
                    String sqlDetalle = "SELECT COALESCE(pl.nombre, 'Platillo Shimaya'), dp.cantidad, dp.precio_unitario, dp.subtotal " +
                                        "FROM detalle_pedido dp " +
                                        "LEFT JOIN platillo pl ON dp.id_platillo = pl.id_platillo " +
                                        "WHERE dp.id_pedido = :idPedido";
                    @SuppressWarnings("unchecked")
                    List<Object[]> filasDetalle = entityManager.createNativeQuery(sqlDetalle)
                            .setParameter("idPedido", idPedido)
                            .getResultList();

                    for (Object[] d : filasDetalle) {
                        String nombreProd = d[0] != null ? String.valueOf(d[0]) : "Platillo Shimaya";
                        int cant = d[1] != null ? ((Number) d[1]).intValue() : 1;
                        cantidadTotal += cant;
                        nombresProductos.add(nombreProd);

                        Map<String, Object> prodMap = new LinkedHashMap<>();
                        prodMap.put("nombre", nombreProd);
                        prodMap.put("cantidad", cant);
                        prodMap.put("precioUnitario", d[2] != null ? d[2] : 0);
                        prodMap.put("subtotal", d[3] != null ? d[3] : 0);
                        detalleItems.add(prodMap);
                    }
                } catch (Exception ignored) {
                }

                // Obtener código de pago y serie de comprobante si existen
                String codigoOperacion = "---";
                String numSerie = "B001-" + String.format("%06d", idPedido);
                try {
                    @SuppressWarnings("unchecked")
                    List<Object[]> filasPago = entityManager.createNativeQuery(
                            "SELECT pg.num_operacion, cp.num_serie " +
                            "FROM pago pg LEFT JOIN comprobante_pago cp ON cp.id_pago = pg.id_pago " +
                            "WHERE pg.id_pedido = :idPedido")
                            .setParameter("idPedido", idPedido)
                            .getResultList();
                    if (!filasPago.isEmpty()) {
                        Object[] fp = filasPago.get(0);
                        if (fp[0] != null) codigoOperacion = String.valueOf(fp[0]);
                        if (fp[1] != null) numSerie = String.valueOf(fp[1]);
                    }
                } catch (Exception ignored) {
                }

                item.put("productos", nombresProductos);
                item.put("items", detalleItems);
                item.put("cantidadTotal", cantidadTotal > 0 ? cantidadTotal : 1);
                item.put("codigoOperacion", codigoOperacion);
                item.put("numSerie", numSerie);
                resultado.add(item);
            }

            return ResponseEntity.ok(resultado);
        } catch (Throwable e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Detalle SQL pedidos: " + e.getMessage());
        }
    }

    @GetMapping("/reservas")
    public ResponseEntity<?> listarReservas(@RequestParam("email") String email) {
        try {
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body("El correo es obligatorio.");
            }
            Usuario usuario = usuarioDAO.buscarPorEmail(email.trim());
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }

            String sql = "SELECT id_reserva, id_sede, fecha_reserva, hora_reserva, " +
                         "num_personas, ocasion_especial, comentarios, estado " +
                         "FROM reserva WHERE id_usuario = :idUsuario " +
                         "ORDER BY id_reserva DESC";

            @SuppressWarnings("unchecked")
            List<Object[]> filas = entityManager.createNativeQuery(sql)
                    .setParameter("idUsuario", usuario.getIdUsuario())
                    .getResultList();

            List<Object> resultado = new ArrayList<>();
            for (Object[] f : filas) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("idReserva", f[0]);
                item.put("idSede", f[1]);
                item.put("fechaReserva", f[2] != null ? String.valueOf(f[2]) : "");
                item.put("horaReserva", f[3] != null ? String.valueOf(f[3]) : "");
                item.put("numPersonas", f[4] != null ? f[4] : 1);
                item.put("ocasionEspecial", f[5] != null ? String.valueOf(f[5]) : "Ninguna");
                item.put("comentarios", f[6] != null ? String.valueOf(f[6]) : "");
                item.put("estado", f[7] != null ? String.valueOf(f[7]) : "CONFIRMADA");
                item.put("nombreSede", obtenerNombreSedeSeguro(f[1]));
                item.put("distritoSede", "");
                resultado.add(item);
            }

            return ResponseEntity.ok(resultado);
        } catch (Throwable e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Detalle SQL: " + e.getMessage());
        }
    }

    private String obtenerNombreSedeSeguro(Object idSedeObj) {
        if (idSedeObj == null) {
            return "Sede Shimaya";
        }
        try {
            Object nombre = entityManager.createNativeQuery("SELECT nombre FROM sede WHERE id_sede = :id")
                    .setParameter("id", idSedeObj)
                    .getSingleResult();
            if (nombre != null) {
                return String.valueOf(nombre);
            }
        } catch (Exception ignored) {
        }
        return "Sede #" + idSedeObj;
    }

    @PutMapping("/reservas/{idReserva}/cancelar")
    @Transactional
    public ResponseEntity<?> cancelarReserva(@PathVariable("idReserva") int idReserva) {
        int filas = entityManager.createNativeQuery("UPDATE reserva SET estado = 'CANCELADA' WHERE id_reserva = :id")
                .setParameter("id", idReserva)
                .executeUpdate();
        if (filas == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("idReserva", idReserva, "estado", "CANCELADA"));
    }
}