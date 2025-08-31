// Archivo: com/intraron/r2dbc/entity/LoanEntity.java
/**
 * @author intraron
 * Entidad de persistencia para una solicitud de préstamo. Representa una fila en la tabla 'solicitudes'.
 */
package com.intraron.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("solicitudes")
public class LoanEntity {
    @Id // intraron: El ID es generado automáticamente por la base de datos.
    private UUID id;

    @Column("user_email")
    private String userEmail;

    @Column("loan_amount")
    private Double loanAmount;

    @Column("loan_term")
    private Integer loanTerm;
}