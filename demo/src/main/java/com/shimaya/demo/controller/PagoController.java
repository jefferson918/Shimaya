package com.shimaya.demo.controller;

import com.shimaya.demo.dao.NotificacionYapeDAO;
import com.shimaya.demo.model.NotificacionYape;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private NotificacionYapeDAO notificacionYapeDAO;

    // 1. Webhook que recibe la notificación push de Yape enviada por MacroDroid
    @PostMapping("/webhook-yape")
    @Transactional
    public ResponseEntity<?> recibirNotificacionYape(@RequestBody(required = false) String rawBody) {
        System.out.println("📲 WEBHOOK YAPE RECIBIDO: " + rawBody);

        if (rawBody == null || rawBody.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "IGNORADO",
                    "detalle", "Cuerpo vacío recibido desde MacroDroid"
            ));
        }

        Pattern pattern = Pattern.compile("S/\\s*(\\d+(?:[.,]\\d{1,2})?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(rawBody);

        if (matcher.find()) {
            String montoLimpio = matcher.group(1).replace(",", ".");
            BigDecimal monto = new BigDecimal(montoLimpio).setScale(2, RoundingMode.HALF_UP);

            NotificacionYape noti = new NotificacionYape();
            noti.setRemitente("Cliente Yape");
            noti.setMonto(monto);
            noti.setMensajeOriginal(rawBody);
            noti.setUsado(false);
            noti.setFechaRecepcion(LocalDateTime.now());

            notificacionYapeDAO.guardar(noti);

            System.out.println("✅ YAPE GUARDADO EN MYSQL -> ID: " + noti.getIdNotificacion() + " | Monto: S/ " + monto);

            return ResponseEntity.ok(Map.of(
                    "status", "GUARDADO",
                    "idNotificacion", noti.getIdNotificacion(),
                    "monto", monto
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "status", "IGNORADO",
                "detalle", "No se encontró un monto válido con S/ en la notificación",
                "recibido", rawBody
        ));
    }

    // 2. Endpoint consultado cada 3 segundos por carrito.html
    @GetMapping("/verificar-yape")
    @Transactional
    public ResponseEntity<?> verificarPagoYape(@RequestParam("monto") BigDecimal monto) {
        try {
            BigDecimal montoNormalizado = monto.setScale(2, RoundingMode.HALF_UP);
            NotificacionYape pago = notificacionYapeDAO.buscarPagoDisponible(montoNormalizado, LocalDateTime.now().minusMinutes(5));

            if (pago != null) {
                pago.setUsado(true);
                notificacionYapeDAO.actualizar(pago);

                return ResponseEntity.ok(Map.of(
                        "pagado", true,
                        "idNotificacion", pago.getIdNotificacion(),
                        "remitente", pago.getRemitente() != null ? pago.getRemitente() : "Cliente Yape"
                ));
            }

            return ResponseEntity.ok(Map.of("pagado", false));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "pagado", false,
                    "error", e.getClass().getSimpleName() + ": " + e.getMessage()
            ));
        }
    }
}