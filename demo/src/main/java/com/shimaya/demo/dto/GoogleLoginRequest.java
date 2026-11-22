package com.shimaya.demo.dto;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String email;
    private String nombre;
    private String googleId;
}