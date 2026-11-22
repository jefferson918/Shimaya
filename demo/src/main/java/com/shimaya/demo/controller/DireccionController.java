package com.shimaya.demo.controller;

import com.shimaya.demo.dao.DireccionUsuarioDAO;
import com.shimaya.demo.dao.UsuarioDAO;
import com.shimaya.demo.model.DireccionUsuario;
import com.shimaya.demo.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/direcciones")
@CrossOrigin(origins = "*")
public class DireccionController {

    @Autowired
    private DireccionUsuarioDAO direccionDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @GetMapping("/usuario/{email}")
    public ResponseEntity<?> listarPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email.trim());
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado.");
        }

        List<DireccionUsuario> direcciones = direccionDAO.listarPorUsuario(usuario.getIdUsuario());

        // Migrar automáticamente la dirección ingresada en el registro si aún no está en direccion_usuario
        if (direcciones.isEmpty() && usuario.getDireccionHabitual() != null && !usuario.getDireccionHabitual().isBlank()) {
            DireccionUsuario inicial = new DireccionUsuario();
            inicial.setIdUsuario(usuario.getIdUsuario());
            inicial.setEtiqueta("CASA");
            inicial.setDistrito("Lima");
            inicial.setDireccionExacta(usuario.getDireccionHabitual().trim());
            inicial.setReferencia("");
            direccionDAO.guardar(inicial);
            direcciones = direccionDAO.listarPorUsuario(usuario.getIdUsuario());
        }

        return ResponseEntity.ok(direcciones);
    }

    @PostMapping
    public ResponseEntity<?> crearDireccion(@RequestBody Map<String, Object> payload) {
        String email = payload.get("email") != null ? payload.get("email").toString().trim() : "";
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado.");
        }

        DireccionUsuario dir = new DireccionUsuario();
        dir.setIdUsuario(usuario.getIdUsuario());
        dir.setEtiqueta(payload.get("etiqueta").toString());
        dir.setDistrito(payload.get("distrito").toString());
        dir.setDireccionExacta(payload.get("direccionExacta").toString());
        dir.setReferencia(payload.get("referencia") != null ? payload.get("referencia").toString() : "");

        DireccionUsuario guardada = direccionDAO.guardar(dir);

        if (Boolean.TRUE.equals(payload.get("principal")) || usuario.getDireccionHabitual() == null || usuario.getDireccionHabitual().isBlank()) {
            usuario.setDireccionHabitual(dir.getDireccionExacta() + ", " + dir.getDistrito());
            usuarioDAO.actualizar(usuario);
        }

        return ResponseEntity.ok(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDireccion(@PathVariable int id, @RequestBody Map<String, Object> payload) {
        DireccionUsuario dir = direccionDAO.obtenerPorId(id);
        if (dir == null) {
            return ResponseEntity.notFound().build();
        }
        dir.setEtiqueta(payload.get("etiqueta").toString());
        dir.setDistrito(payload.get("distrito").toString());
        dir.setDireccionExacta(payload.get("direccionExacta").toString());
        dir.setReferencia(payload.get("referencia") != null ? payload.get("referencia").toString() : "");
        direccionDAO.actualizar(dir);

        if (Boolean.TRUE.equals(payload.get("principal"))) {
            Usuario usuario = usuarioDAO.obtenerPorId(dir.getIdUsuario());
            if (usuario != null) {
                usuario.setDireccionHabitual(dir.getDireccionExacta() + ", " + dir.getDistrito());
                usuarioDAO.actualizar(usuario);
            }
        }
        return ResponseEntity.ok(dir);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDireccion(@PathVariable int id) {
        DireccionUsuario dir = direccionDAO.obtenerPorId(id);
        if (dir != null) {
            int idUsuario = dir.getIdUsuario();
            direccionDAO.eliminar(id);
            List<DireccionUsuario> restantes = direccionDAO.listarPorUsuario(idUsuario);
            Usuario usuario = usuarioDAO.obtenerPorId(idUsuario);
            if (usuario != null) {
                usuario.setDireccionHabitual(restantes.isEmpty() ? "" : restantes.get(0).getDireccionExacta() + ", " + restantes.get(0).getDistrito());
                usuarioDAO.actualizar(usuario);
            }
        }
        return ResponseEntity.ok("Dirección eliminada");
    }
}