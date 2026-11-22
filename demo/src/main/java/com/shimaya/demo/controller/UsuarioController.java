package com.shimaya.demo.controller;

import com.shimaya.demo.dao.DireccionUsuarioDAO;
import com.shimaya.demo.dao.UsuarioDAO;
import com.shimaya.demo.dto.GoogleLoginRequest;
import com.shimaya.demo.dto.LoginRequest;
import com.shimaya.demo.dto.RegistroRequest;
import com.shimaya.demo.model.DireccionUsuario;
import com.shimaya.demo.model.Usuario;
import com.shimaya.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired(required = false)
    private DireccionUsuarioDAO direccionUsuarioDAO;

    @Autowired(required = false)
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioDAO.buscarPorEmail(request.getEmail() != null ? request.getEmail().trim() : "");

        if (usuario != null && request.getPassword() != null && request.getPassword().equals(usuario.getPassword())) {
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuario.setEstadoSesion("ACTIVO");
            usuarioDAO.actualizar(usuario);
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> loginConGoogle(@RequestBody GoogleLoginRequest request) {
        Usuario usuario = usuarioDAO.buscarPorEmail(request.getEmail().trim());

        if (usuario == null) {
            usuario = new Usuario();
            usuario.setNombre(request.getNombre());
            usuario.setEmail(request.getEmail().trim());
            usuario.setGoogleId(request.getGoogleId());
            usuario.setProveedorAuth("GOOGLE");
            usuario.setEstadoCuenta("VERIFICADO");
            usuarioDAO.guardar(usuario);
        }

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuario.setEstadoSesion("ACTIVO");
        usuarioDAO.actualizar(usuario);

        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {
        String emailLimpio = request.getEmail() != null ? request.getEmail().trim() : "";
        if (usuarioDAO.buscarPorEmail(emailLimpio) != null) {
            return ResponseEntity.badRequest().body("El correo ya se encuentra registrado.");
        }

        String codigo = String.valueOf((int) (Math.random() * 900000) + 100000);
        String dirIngresada = request.getDireccion() != null ? request.getDireccion().trim() : "";

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(request.getNombre() != null ? request.getNombre().trim() : "");
        nuevoUsuario.setApellidos(request.getApellidos() != null ? request.getApellidos().trim() : "");
        nuevoUsuario.setEmail(emailLimpio);
        nuevoUsuario.setTelefono(request.getTelefono() != null ? request.getTelefono().trim() : "");
        nuevoUsuario.setDireccionHabitual(dirIngresada);
        nuevoUsuario.setPassword(request.getPassword());
        nuevoUsuario.setCodigoVerificacion(codigo);
        nuevoUsuario.setEstadoCuenta("PENDIENTE");

        usuarioDAO.guardar(nuevoUsuario);

        // Si llenó la casilla de dirección al registrarse, crearla también en direccion_usuario
        if (!dirIngresada.isEmpty() && direccionUsuarioDAO != null) {
            Usuario guardado = usuarioDAO.buscarPorEmail(emailLimpio);
            if (guardado != null && guardado.getIdUsuario() != null) {
                DireccionUsuario dir = new DireccionUsuario();
                dir.setIdUsuario(guardado.getIdUsuario());
                dir.setEtiqueta("CASA");
                dir.setDistrito("Lima");
                dir.setDireccionExacta(dirIngresada);
                dir.setReferencia("");
                direccionUsuarioDAO.guardar(dir);
            }
        }

        try {
            if (emailService != null) {
                emailService.enviarCodigoVerificacion(emailLimpio, codigo);
            }
        } catch (Exception ignored) {}

        return ResponseEntity.ok("Código enviado con éxito.");
    }

    @PostMapping("/verificar-codigo")
    public ResponseEntity<?> verificarCodigo(@RequestBody Map<String, String> payload) {
        String email = payload.get("email") != null ? payload.get("email").trim() : "";
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario != null && payload.get("codigo") != null && payload.get("codigo").equals(usuario.getCodigoVerificacion())) {
            usuario.setEstadoCuenta("VERIFICADO");
            usuario.setEstadoSesion("ACTIVO");
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuario.setCodigoVerificacion(null);
            usuarioDAO.actualizar(usuario);
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.badRequest().body("Código incorrecto.");
    }

    @PostMapping("/logout/{id}")
    public ResponseEntity<?> logout(@PathVariable int id) {
        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario != null) {
            usuario.setEstadoSesion("INACTIVO");
            usuarioDAO.actualizar(usuario);
        }
        return ResponseEntity.ok("Sesión cerrada");
    }
}