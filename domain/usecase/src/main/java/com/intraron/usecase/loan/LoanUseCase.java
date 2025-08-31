// Archivo: com/intraron/usecase/loan/LoanUseCase.java
/**
 * @author intraron
 * Implementa la lógica de negocio para gestionar solicitudes de préstamo.
 * Actúa como orquestador entre el puerto de entrada (Handler) y los puertos de salida (Repositorios).
 */
package com.intraron.usecase.loan;

import com.intraron.model.loan.Loan;
import com.intraron.model.loan.LoanEvaluationResult;
import com.intraron.model.loan.gateways.LoanRepository;
import com.intraron.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
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
                            } else if (user.getSalarioBase() >= 4000000) {
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
    public Mono<Loan> save(Loan loan) {
        log.info("Iniciando proceso de registro para la solicitud del usuario: {}", loan.getUserEmail());

        // intraron: Las validaciones ahora se hacen directamente sobre el objeto 'loan'.
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
}