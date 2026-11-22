package com.shimaya.demo.controller;

import com.shimaya.demo.dao.UsuarioDAO;
import com.shimaya.demo.model.ComprobantePago;
import com.shimaya.demo.model.DetallePedido;
import com.shimaya.demo.model.Pago;
import com.shimaya.demo.model.Pedido;
import com.shimaya.demo.model.Platillo;
import com.shimaya.demo.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @PostMapping
    @Transactional
    public ResponseEntity<?> registrarPedido(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        Usuario usuario = usuarioDAO.buscarPorEmail(email != null ? email.trim() : "");
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado en MySQL.");
        }

        // 1. Insertar en tabla pedido
        Pedido pedido = new Pedido();
        pedido.setIdUsuario(usuario.getIdUsuario());
        if (payload.get("idSede") != null && !payload.get("idSede").toString().isBlank()) {
            pedido.setIdSede(Integer.parseInt(payload.get("idSede").toString()));
        }
        pedido.setFechaHora(LocalDateTime.now());
        BigDecimal montoTotal = new BigDecimal(payload.get("montoTotal").toString());
        pedido.setMontoTotal(montoTotal);
        pedido.setEstadoPedido("EN PREPARACIÓN");
        pedido.setTipoEntrega(payload.get("tipoEntrega") != null ? payload.get("tipoEntrega").toString() : "DELIVERY");
        pedido.setDireccionEnvio(payload.get("direccionEnvio") != null ? payload.get("direccionEnvio").toString() : usuario.getDireccionHabitual());
        pedido.setObservaciones(payload.get("observaciones") != null ? payload.get("observaciones").toString() : "");
        entityManager.persist(pedido);
        entityManager.flush();

        // 2. Insertar ítems en tabla detalle_pedido
        Object itemsObj = payload.get("items");
        if (itemsObj instanceof List<?> rawItems) {
            for (Object obj : rawItems) {
                if (!(obj instanceof Map<?, ?> item)) {
                    continue;
                }
                DetallePedido detalle = new DetallePedido();
                detalle.setIdPedido(pedido.getIdPedido());

                Integer idPlatillo = null;
                if (item.get("idPlatillo") != null) {
                    idPlatillo = Integer.parseInt(item.get("idPlatillo").toString());
                } else if (item.get("nombre") != null) {
                    List<Platillo> encontrados = entityManager.createQuery(
                            "SELECT p FROM Platillo p WHERE LOWER(p.nombre) = LOWER(:nombre)", Platillo.class)
                            .setParameter("nombre", item.get("nombre").toString().trim())
                            .getResultList();
                    if (!encontrados.isEmpty()) {
                        idPlatillo = encontrados.get(0).getIdPlatillo();
                    }
                }
                detalle.setIdPlatillo(idPlatillo != null ? idPlatillo : 1);

                int cantidad = Integer.parseInt(item.get("cantidad").toString());
                BigDecimal precioUnit = new BigDecimal(item.get("precioUnitario").toString());
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(precioUnit);
                detalle.setSubtotal(precioUnit.multiply(BigDecimal.valueOf(cantidad)));
                entityManager.persist(detalle);
            }
        }

        // 3. Insertar en tabla pago
        String codigoYape = payload.get("numOperacion") != null
                ? payload.get("numOperacion").toString()
                : "OP-" + System.currentTimeMillis();

        Pago pago = new Pago();
        pago.setIdPedido(pedido.getIdPedido());
        pago.setIdMetodo(payload.get("idMetodo") != null ? Integer.parseInt(payload.get("idMetodo").toString()) : 1);
        pago.setMonto(montoTotal);
        pago.setNumOperacion(codigoYape);
        pago.setTelefonoYape(payload.get("telefonoYape") != null ? payload.get("telefonoYape").toString() : usuario.getTelefono());
        pago.setFechaHora(LocalDateTime.now());
        pago.setEstadoPago("COMPLETADO");
        entityManager.persist(pago);
        entityManager.flush();

        // 4. Insertar en tabla comprobante_pago
        ComprobantePago comprobante = new ComprobantePago();
        comprobante.setIdPago(pago.getIdPago());
        comprobante.setNumSerie("B001-" + String.format("%06d", pedido.getIdPedido()));
        comprobante.setFechaEmision(LocalDateTime.now());
        comprobante.setMontoTotal(montoTotal);
        comprobante.setTipoComprobante(payload.get("tipoComprobante") != null ? payload.get("tipoComprobante").toString() : "BOLETA");
        entityManager.persist(comprobante);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Pedido registrado en MySQL",
                "idPedido", pedido.getIdPedido(),
                "numSerie", comprobante.getNumSerie()
        ));
    }

    @GetMapping("/usuario/{email}")
    public ResponseEntity<?> listarPedidosPorUsuario(@PathVariable String email) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email.trim());
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado.");
        }

        List<Pedido> pedidos = entityManager.createQuery(
                "SELECT p FROM Pedido p WHERE p.idUsuario = :idUsuario ORDER BY p.fechaHora DESC", Pedido.class)
                .setParameter("idUsuario", usuario.getIdUsuario())
                .getResultList();

        var respuesta = new ArrayList<Object>();
        for (Pedido p : pedidos) {
            List<Pago> pagos = entityManager.createQuery(
                    "SELECT pg FROM Pago pg WHERE pg.idPedido = :idPedido", Pago.class)
                    .setParameter("idPedido", p.getIdPedido())
                    .getResultList();

            Map<String, Object> fila = new HashMap<>();
            fila.put("idPedido", p.getIdPedido());
            fila.put("fechaHora", p.getFechaHora());
            fila.put("montoTotal", p.getMontoTotal());
            fila.put("estadoPedido", p.getEstadoPedido());
            fila.put("tipoEntrega", p.getTipoEntrega());
            fila.put("direccionEnvio", p.getDireccionEnvio());
            fila.put("observaciones", p.getObservaciones());
            fila.put("codigoYape", pagos.isEmpty() ? "---" : pagos.get(0).getNumOperacion());
            respuesta.add(fila);
        }

        return ResponseEntity.ok(respuesta);
    }
}