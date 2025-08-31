/**
 * @author intraron
 * Esta clase implementa la lógica de negocio para las operaciones con usuarios.
 * Actúa como orquestador entre el puerto de entrada (Controller) y el puerto de salida (Repository).
 */

package com.intraron.usecase.user;

import com.intraron.model.user.User;
import com.intraron.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public Mono<User> save(User user) {
        log.info("Iniciando proceso de registro para el usuario: {}", user.getCorreoElectronico());

        if (user.getNombres() == null || user.getNombres().trim().isEmpty() ||
                user.getApellidos() == null || user.getApellidos().trim().isEmpty() ||
                user.getCorreoElectronico() == null || user.getCorreoElectronico().trim().isEmpty() ||
                user.getSalarioBase() == null) {
            log.warn("Validación fallida: campos requeridos nulos o vacíos.");
            return Mono.error(new IllegalArgumentException("Los campos nombres, apellidos, correo_electronico y salario_base no pueden ser nulos o vacíos."));
        }

        if (!EMAIL_PATTERN.matcher(user.getCorreoElectronico()).matches()) {
            log.warn("Validación fallida: formato de correo electrónico inválido.");
            return Mono.error(new IllegalArgumentException("El formato del correo electrónico es inválido."));
        }

        if (user.getSalarioBase() <= 0 || user.getSalarioBase() > 15000000) {
            log.warn("Validación fallida: salario base fuera de rango.");
            return Mono.error(new IllegalArgumentException("El salario base debe estar entre 0 y 15,000,000."));
        }

        return userRepository.findByCorreoElectronico(user.getCorreoElectronico())
                .flatMap(existingUser -> {
                    log.warn("Validación fallida: el correo electrónico ya existe.");
                    return Mono.error(new IllegalArgumentException("El correo electrónico ya se encuentra registrado."));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Usuario validado. Guardando en el repositorio.");
                    return userRepository.save(user);
                }))
                .cast(User.class);
    }

    public Flux<User> getAllUsers() {
        log.info("Iniciando consulta de todos los usuarios.");
        return userRepository.getAllUsers()
                .doOnComplete(() -> log.info("Consulta de todos los usuarios finalizada."));
    }
}
