/**
 * @author intraron
 * Este es el manejador (Handler) de las peticiones WebFlux. Contiene la lógica para
 * procesar la solicitud de registro de usuarios y se encarga de llamar al caso de uso.
 */

package com.intraron.api;

import com.intraron.api.dto.UserRequestDTO;
import com.intraron.model.user.User;
import com.intraron.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import com.intraron.api.dto.LoanRequestDTO;
import com.intraron.model.loan.Loan;
import com.intraron.usecase.loan.LoanUseCase;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final LoanUseCase loanUseCase;

    /**
     * @author intraron
     * Maneja la petición para registrar una nueva solicitud de préstamo.
     * Mapea el DTO de entrada al modelo de dominio y llama al caso de uso.
     * @param serverRequest La petición del servidor.
     * @return Mono<ServerResponse> Una respuesta HTTP reactiva.
     */
    public Mono<ServerResponse> registerLoanRequest(ServerRequest serverRequest) {
        log.info("Petición de registro de solicitud de préstamo recibida.");
        return serverRequest.bodyToMono(LoanRequestDTO.class)
                .flatMap(loanRequestDTO -> {
                    log.info("Mapeando LoanRequestDTO a Loan.");
                    // intraron: AQUI se realiza el mapeo.
                    Loan loan = Loan.builder()
                            .userEmail(loanRequestDTO.getUserEmail())
                            .loanAmount(loanRequestDTO.getLoanAmount())
                            .loanTerm(loanRequestDTO.getLoanTerm())
                            .build();

                    // intraron: Se pasa el objeto de dominio al caso de uso.
                    return loanUseCase.save(loan)
                            .flatMap(savedLoan -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(savedLoan))
                            .onErrorResume(IllegalArgumentException.class, e -> {
                                log.warn("Validación fallida en la solicitud: {}", e.getMessage());
                                return ServerResponse.badRequest().bodyValue(e.getMessage());
                            })
                            .onErrorResume(e -> {
                                log.error("Error al registrar solicitud: {}", e.getMessage());
                                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Error interno al procesar la solicitud.");
                            });
                });
    }

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        log.debug("Petición de registro de usuario recibida.");

        return serverRequest.bodyToMono(UserRequestDTO.class)
                .flatMap(userRequestDTO -> {
                    log.debug("Mapeando UserRequestDTO a User: {}", userRequestDTO.getCorreoElectronico());
                    // Mapea el DTO de entrada al modelo de dominio.
                    User user = User.builder()
                            .nombres(userRequestDTO.getNombres())
                            .apellidos(userRequestDTO.getApellidos())
                            .fechaNacimiento(userRequestDTO.getFechaNacimiento())
                            .direccion(userRequestDTO.getDireccion())
                            .telefono(userRequestDTO.getTelefono())
                            .correoElectronico(userRequestDTO.getCorreoElectronico())
                            .salarioBase(userRequestDTO.getSalarioBase())
                            .build();

                    // Llama al caso de uso para procesar la petición.
                    return userUseCase.save(user)
                            .flatMap(savedUser ->
                                    ok().contentType(MediaType.APPLICATION_JSON).bodyValue(savedUser)
                            )
                            .onErrorResume(e -> {
                                log.error("Error al registrar usuario: {}", e.getMessage());
                                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage());
                            });
                });
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        log.debug("Petición para obtener todos los usuarios recibida.");
        return userUseCase.getAllUsers()
                .collectList()
                .flatMap(users -> {
                    log.debug("Usuarios encontrados: {}", users.size());
                    return ok().contentType(MediaType.APPLICATION_JSON).bodyValue(users);
                })
                .doOnError(e -> log.error("Error al obtener todos los usuarios: {}", e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        // Implementación de ejemplo para GET.
        return ok().bodyValue("GET request received");
    }
}
