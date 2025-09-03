// Archivo: com/intraron/api/dto/AuthRequestDTO.java
package com.intraron.api.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String correoElectronico;
    private String password;
}