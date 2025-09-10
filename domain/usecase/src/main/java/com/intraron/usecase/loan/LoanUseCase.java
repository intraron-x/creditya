// Archivo: com/intraron/usecase/loan/LoanUseCase.java
/**
 * @author intraron
 * Implementa la lógica de negocio para gestionar solicitudes de préstamo.
 * Actúa como orquestador entre el puerto de entrada (Handler) y los puertos de salida (Repositorios).
 */
package com.intraron.usecase.loan;

import com.intraron.model.common.DomainPageable;
import com.intraron.model.loan.Loan;
import com.intraron.model.loan.LoanEvaluationResult;
import com.intraron.model.loan.gateways.LoanRepository;
import com.intraron.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
public class LoanUseCase {
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    /**
     * @author intraron
     * Evalúa una solicitud de préstamo basada en el salario del usuario.
     * @param loanId El ID de la solicitud a evaluar.
     * @return Mono<LoanEvaluationResult> con el resultado de la evaluación.
     */
    public Mono<LoanEvaluationResult> evaluateLoan(UUID loanId) {
        log.info("Iniciando evaluación para la solicitud con ID: {}", loanId);

        return loanRepository.findById(loanId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La solicitud con el ID proporcionado no existe.")))
                .flatMap(loan -> userRepository.findByCorreoElectronico(loan.getUserEmail())
                        .flatMap(user -> {
                            log.info("Usuario encontrado, evaluando con salario: {}", user.getSalarioBase());
                            String evaluation;

                            if (user.getSalarioBase() >= 8000000) {
                                evaluation = "APROBADO";
                            } else if (loan.getLoanAmount() <= user.getSalarioBase() * 0.4){
                                return Mono.just(LoanEvaluationResult.builder()
                                        .evaluation("APROBADO")
                                        .loanAmount(loan.getLoanAmount())
                                        .loanTerm(loan.getLoanTerm())
                                        .build());
                            }
                            else if (user.getSalarioBase() < loan.getLoanAmount() / 12) {
                                log.warn("Evaluación de solicitud {}: Rechazada por salario insuficiente.", loanId);
                                return Mono.just(LoanEvaluationResult.builder()
                                        .evaluation("RECHAZADO")
                                        .loanAmount(loan.getLoanAmount())
                                        .loanTerm(loan.getLoanTerm())
                                        .build());
                            }
                            else if (user.getSalarioBase() >= 4000000) {
                                evaluation = "ANALISIS";
                            } else {
                                evaluation = "NEGADO";
                            }
                            log.info("Resultado de la evaluación: {}", evaluation);

                            // intraron: Se construye el objeto de dominio del resultado.
                            return Mono.just(LoanEvaluationResult.builder()
                                    .evaluation(evaluation)
                                    .loanAmount(loan.getLoanAmount())
                                    .loanTerm(loan.getLoanTerm())
                                    .build());
                        }));
    }

    /**
     * @author intraron
     * Procesa y guarda una nueva solicitud de préstamo.
     * @param loan El objeto de dominio 'Loan' con los datos de la solicitud.
     * @return Mono<Loan> que emite la solicitud guardada.
     */
    public Mono<Loan> save(Loan loan, String loggedInUserEmail) {
        log.info("Iniciando proceso de registro para la solicitud del usuario: {}", loan.getUserEmail());

        // intraron: Se valida que el usuario de la solicitud coincida con el usuario logueado.
        if (!loggedInUserEmail.equalsIgnoreCase(loan.getUserEmail())) {
            log.warn("Validación de seguridad fallida: el usuario logueado no coincide con el de la solicitud.");
            return Mono.error(new IllegalAccessException("El usuario logueado no tiene permisos para crear una solicitud para otro usuario."));
        }

        // intraron: Las validaciones de negocio se realizan sobre el objeto 'loan'.
        if (loan.getLoanAmount() <= 0 || loan.getLoanAmount() > 10000000) {
            return Mono.error(new IllegalArgumentException("El monto del préstamo debe ser mayor a 0 y no exceder los 10,000,000."));
        }
        if (loan.getLoanTerm() <= 0 || loan.getLoanTerm() > 60) {
            return Mono.error(new IllegalArgumentException("El plazo del préstamo debe ser entre 1 y 60 meses."));
        }

        return userRepository.findByCorreoElectronico(loan.getUserEmail())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El usuario con el correo proporcionado no existe.")))
                .flatMap(user -> {
                    log.info("Usuario validado. Guardando la solicitud.");
                    return loanRepository.save(loan); // Se pasa el objeto de dominio directamente.
                });
    }

    /**
     * @author intraron
     * Obtiene un listado de solicitudes de préstamo para revisión manual.
     * Esta es la lógica de negocio para el requerimiento #4.
     * @return Flux<Loan> que emite las solicitudes de préstamo que requieren revisión.
     */
    public Flux<Loan> getManualReviewLoansPaginated(DomainPageable pageable) {
        log.info("Iniciando la búsqueda paginada de solicitudes para revisión manual. Página: {}, Tamaño: {}, Ordenar por: {}",
                pageable.getPage(), pageable.getSize(), pageable.getSortBy());
        List<String> statuses = Arrays.asList("PENDIENTE_REVISION", "RECHAZADAS", "REVISION_MANUAL");
        return loanRepository.findAllByStatusPaginated(statuses, pageable)
                .doOnComplete(() -> log.info("Consulta paginada de solicitudes para revisión manual finalizada."));
    }

}