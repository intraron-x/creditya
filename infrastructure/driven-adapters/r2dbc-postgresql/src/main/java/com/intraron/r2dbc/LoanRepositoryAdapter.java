// Archivo: com/intraron/r2dbc/LoanRepositoryAdapter.java
/**
 * @author intraron
 * Adaptador que implementa el puerto del dominio (LoanRepository).
 * Maneja la lógica de persistencia y el mapeo de objetos de dominio a entidades de base de datos.
 */
package com.intraron.r2dbc;

import com.intraron.model.loan.Loan;
import com.intraron.model.loan.gateways.LoanRepository;
import com.intraron.r2dbc.entity.LoanEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LoanRepositoryAdapter implements LoanRepository {

    private final LoanReactiveRepository loanReactiveRepository;

    /**
     * @author intraron
     * Implementa la búsqueda de una solicitud por su ID.
     * @param id El ID de la solicitud a buscar.
     * @return Mono<Loan> que emite la solicitud de dominio encontrada.
     */
    @Override
    public Mono<Loan> findById(UUID id) {
        log.info("Buscando solicitud con ID: {}", id);
        return loanReactiveRepository.findById(id)
                .map(entity -> {
                    log.info("Solicitud encontrada con ID: {}", entity.getId());
                    // intraron: Se mapea la entidad de persistencia a un objeto de dominio
                    return Loan.builder()
                            .id(entity.getId())
                            .userEmail(entity.getUserEmail())
                            .loanAmount(entity.getLoanAmount())
                            .loanTerm(entity.getLoanTerm())
                            .build();
                })
                .doOnError(e -> log.error("Error al buscar la solicitud con ID {}: {}", id, e.getMessage(), e));
    }

    @Override
    public Mono<Loan> save(Loan loan) {
        log.info("Iniciando el proceso de guardado para la solicitud del usuario: {}", loan.getUserEmail());

        LoanEntity loanEntity = LoanEntity.builder()
                .userEmail(loan.getUserEmail())
                .loanAmount(loan.getLoanAmount())
                .loanTerm(loan.getLoanTerm())
                .build();

        return loanReactiveRepository.save(loanEntity)
                .map(entity -> {
                    log.info("Solicitud guardada con éxito, ID: {}", entity.getId());
                    return Loan.builder()
                            .id(entity.getId())
                            .userEmail(entity.getUserEmail())
                            .loanAmount(entity.getLoanAmount())
                            .loanTerm(entity.getLoanTerm())
                            .build();
                })
                .doOnError(e -> log.error("Error al guardar la solicitud: {}", e.getMessage(), e));
    }
}