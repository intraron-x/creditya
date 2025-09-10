// Archivo: com/intraron/model/loan/gateways/LoanRepository.java
/**
 * @author intraron
 * Puerto de salida para el dominio de solicitudes de préstamo. Define la funcionalidad
 * que el dominio necesita de la capa de infraestructura, como guardar una solicitud.
 */
package com.intraron.model.loan.gateways;

import com.intraron.model.common.DomainPageable;
import com.intraron.model.loan.Loan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface LoanRepository {
    Mono<Loan> save(Loan loan);
    Mono<Loan> findById(UUID id);
    /**
     * @author intraron
     * Obtiene un flujo de solicitudes de préstamo filtradas por una lista de estados.
     * @param statuses Lista de estados por los que se desea filtrar.
     * @return Flux<Loan> que emite un flujo de solicitudes que cumplen con el filtro.
     */
    Flux<Loan> findAllByStatus(List<String> statuses);

    Flux<Loan> findAllByStatusPaginated(List<String> statuses, DomainPageable pageable);
}