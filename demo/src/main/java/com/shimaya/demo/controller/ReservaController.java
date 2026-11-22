// === ReservaController.java ===
package com.shimaya.demo.controller;

import com.shimaya.demo.dao.ReservaDAO;
import com.shimaya.demo.dao.SedeDAO;
import com.shimaya.demo.dao.UsuarioDAO;
import com.shimaya.demo.model.Reserva;
import com.shimaya.demo.model.Sede;
import com.shimaya.demo.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ReservaDAO reservaDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private SedeDAO sedeDAO;

    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody Map<String, Object> payload) {
        String email = payload.get("email") != null ? payload.get("email").toString().trim() : "";
        Usuario usuario = !email.isEmpty()
                ? usuarioDAO.buscarPorEmail(email)
                : (payload.get("idUsuario") != null
                    ? usuarioDAO.obtenerPorId(Integer.parseInt(payload.get("idUsuario").toString()))
                    : null);

        Sede sede = null;
        if (payload.get("idSede") != null && !payload.get("idSede").toString().isBlank()) {
            sede = sedeDAO.obtenerPorId(Integer.parseInt(payload.get("idSede").toString()));
        }
        if (sede == null && payload.get("nombreSede") != null) {
            sede = sedeDAO.buscarPorNombre(payload.get("nombreSede").toString());
        }

        if (usuario == null || sede == null) {
            return ResponseEntity.badRequest().body("Usuario o sede no encontrados en MySQL.");
        }

        String horaStr = payload.get("horaReserva").toString();
        if (horaStr.length() == 5) {
            horaStr += ":00";
        }

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setSede(sede);
        reserva.setFechaReserva(LocalDate.parse(payload.get("fechaReserva").toString()));
        reserva.setHoraReserva(LocalTime.parse(horaStr));
        reserva.setNumPersonas(Integer.parseInt(payload.get("numPersonas").toString()));
        reserva.setOcasionEspecial(payload.get("ocasionEspecial") != null ? payload.get("ocasionEspecial").toString() : "Ninguna");
        reserva.setComentarios(payload.get("comentarios") != null ? payload.get("comentarios").toString() : "");
        reserva.setEstado("CONFIRMADA");
        reserva.setFechaCreacion(LocalDateTime.now());

        Reserva guardada = reservaDAO.guardar(reserva);
        return ResponseEntity.ok(guardada);
    }

    @GetMapping("/usuario/{email}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable String email) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email.trim());
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado.");
        }
        List<Reserva> reservas = entityManager.createQuery(
                "SELECT r FROM Reserva r WHERE r.usuario.idUsuario = :idUsuario ORDER BY r.fechaCreacion DESC",
                Reserva.class)
                .setParameter("idUsuario", usuario.getIdUsuario())
                .getResultList();
        return ResponseEntity.ok(reservas);
    }

    @PutMapping("/{id}/cancelar")
    @Transactional
    public ResponseEntity<?> cancelarReserva(@PathVariable int id) {
        Reserva reserva = entityManager.find(Reserva.class, id);
        if (reserva == null) {
            return ResponseEntity.notFound().build();
        }
        reserva.setEstado("CANCELADA");
        entityManager.merge(reserva);
        return ResponseEntity.ok(reserva);
    }
}