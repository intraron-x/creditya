/**
 * @author intraron
 * Esta clase maneja las solicitudes de autenticación y registro.
 * Delega la lógica de negocio al UserUseCase y se encarga de la encriptación de contraseñas.
 */
package com.intraron.api;

import com.intraron.api.dto.AuthRequestDTO;
import com.intraron.api.dto.RegisterRequestDTO;
import com.intraron.api.security.JwtService;
import com.intraron.model.user.User;
import com.intraron.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import com.intraron.r2dbc.entity.UserEntity; // intraron: Importar la clase UserEntity

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final UserUseCase userUseCase;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Mono<ServerResponse> register(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegisterRequestDTO.class)
                .flatMap(registerRequest -> {
                    log.info("Iniciando registro para el correo: {}", registerRequest.getCorreoElectronico());
                    // intraron: Encriptar la contraseña y asignar el rol en la capa de infraestructura (Entry Point)
                    String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
                    Set<String> roles = (registerRequest.getRoles() != null && !registerRequest.getRoles().isEmpty() ? registerRequest.getRoles() : Set.of("USER"));

                    // intraron: Crear el objeto de dominio con los datos y la contraseña encriptada
                    User userToSave = User.builder()
                            .nombres(registerRequest.getNombres())
                            .apellidos(registerRequest.getApellidos())
                            .fechaNacimiento(registerRequest.getFechaNacimiento())
                            .direccion(registerRequest.getDireccion())
                            .telefono(registerRequest.getTelefono())
                            .correoElectronico(registerRequest.getCorreoElectronico())
                            .salarioBase(registerRequest.getSalarioBase())
                            .password(encodedPassword)
                            .roles(roles)
                            .build();

                    // intraron: Pasar el objeto de dominio completo al caso de uso para las validaciones de negocio
                    return userUseCase.save(userToSave)
                            .flatMap(savedUser -> ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(Map.of("message", "Usuario registrado exitosamente")));
                });
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AuthRequestDTO.class)
                .flatMap(authRequest -> userUseCase.findByCorreoElectronico(authRequest.getCorreoElectronico())
                        .flatMap(user -> {

                            log.info("Validando password {}", passwordEncoder.matches(authRequest.getPassword(), user.getPassword()));

                            if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                                // intraron: Crear un UserEntity a partir del objeto de dominio User
                                // intraron: para que el servicio JWT pueda usarlo como UserDetails.
                                UserEntity userEntity = UserEntity.builder()
                                        .id(user.getId())
                                        .nombres(user.getNombres())
                                        .apellidos(user.getApellidos())
                                        .correoElectronico(user.getCorreoElectronico())
                                        .password(user.getPassword())
                                        .salarioBase(user.getSalarioBase())
                                        .roles(Collections.singletonList(String.join(",", user.getRoles())))
                                        .build();

                                String token = jwtService.generateToken(userEntity);
                                return ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(Map.of("token", token));
                            } else {
                                return Mono.error(new IllegalArgumentException("Credenciales inválidas."));
                            }
                        })
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Credenciales inválidas.")))
                );
    }
}
