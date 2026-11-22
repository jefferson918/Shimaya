package com.shimaya.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCodigoVerificacion(String destinatario, String codigo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        
        // 1. A quién le enviamos el correo (el que puso en el registro)
        mensaje.setTo(destinatario);
        
        // 2. Título del correo
        mensaje.setSubject("Código de Verificación - Shimaya Ramen");
        
        // 3. El cuerpo del mensaje
        mensaje.setText(
            "¡Hola!\n\n" +
            "Gracias por unirte a la experiencia Shimaya. " +
            "Tu código de verificación de 4 dígitos es:\n\n" +
            codigo + "\n\n" +
            "Ingresa este código en la ventana de registro para activar tu cuenta.\n\n" +
            "Saludos,\n" +
            "El equipo de Shimaya Ramen."
        );
        
        // 4. Enviar
        mailSender.send(mensaje);
    }
}