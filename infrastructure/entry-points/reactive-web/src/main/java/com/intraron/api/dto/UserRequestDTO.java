package com.intraron.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "El nombre no puede ser nulo o vacío")
    private String nombres;

    @NotBlank(message = "El apellido no puede ser nulo o vacío")
    private String apellidos;

    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;

    @NotBlank(message = "El correo electrónico no puede ser nulo o vacío")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "El formato del correo electrónico es inválido")
    private String correoElectronico;

    @NotNull(message = "El salario base no puede ser nulo")
    private Double salarioBase;
}
