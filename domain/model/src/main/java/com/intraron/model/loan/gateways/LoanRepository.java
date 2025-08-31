// Archivo: com/intraron/model/loan/gateways/LoanRepository.java
/**
 * @author intraron
 * Puerto de salida para el dominio de solicitudes de préstamo. Define la funcionalidad
 * que el dominio necesita de la capa de infraestructura, como guardar una solicitud.
 */
package com.intraron.model.loan.gateways;

import com.intraron.model.loan.Loan;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanRepository {
    Mono<Loan> save(Loan loan);
    Mono<Loan> findById(UUID id);
}