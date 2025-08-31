package com.intraron.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("userdata")
public class UserEntity {

    @Id
    private String id;
    @Column("NOMBRES") // Mapea 'nombres' a 'NOMBRES'
    private String nombres;
    @Column("APELLIDOS") // Mapea 'apellidos' a 'APELLIDOS'
    private String apellidos;
    @Column("FECHA_NACIMIENTO")
    private LocalDate fechaNacimiento; // Se usa LocalDate para la fecha
    @Column("DIRECCION") // Mapea 'direccion' a 'DIRECCION'
    private String direccion;
    @Column("TELEFONO") // Mapea 'telefono' a 'TELEFONO'
    private String telefono;
    @Column("CORREO_ELECTRONICO") // Mapea 'correoElectronico' a 'CORREO_ELECTRONICO'
    private String correoElectronico;
    @Column("SALARIO_BASE") // Mapea 'salarioBase' a 'SALARIO_BASE'
    private Double salarioBase;
}
