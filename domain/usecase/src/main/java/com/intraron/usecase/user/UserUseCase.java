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

import java.util.Set; // intraron: Importar para el rol por defecto

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
                user.getPassword() == null || user.getPassword().isEmpty() ||
                user.getSalarioBase() == 0.0) {
            log.warn("Validación fallida: campos requeridos faltantes.");
            return Mono.error(new IllegalArgumentException("Todos los campos son obligatorios."));
        }

        if (!EMAIL_PATTERN.matcher(user.getCorreoElectronico()).matches()) {
            log.warn("Validación fallida: formato de correo inválido.");
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

    public Mono<User> findByCorreoElectronico(String correoElectronico) {
        return userRepository.findByCorreoElectronico(correoElectronico);
    }
}
