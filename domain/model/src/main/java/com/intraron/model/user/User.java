/**
 * @author intraron
 * Esta clase representa el modelo de datos de un usuario en el dominio.
 * Contiene la información personal necesaria para el registro.
 */

package com.intraron.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private String id;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento; // Se usa String para simplificar la validación en esta etapa.
    private String direccion;
    private String telefono;
    private String correoElectronico;
    private Double salarioBase;
}

