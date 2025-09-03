/**
 * @author intraron
 * Esta clase es el adaptador que implementa el puerto del dominio (UserRepository).
 * Transforma los objetos de dominio a entidades de persistencia y viceversa.
 */

// intraron: Esta clase es el adaptador que implementa el gateway.
// Aquí se realiza la traducción entre el modelo de dominio (User)
// y la entidad de persistencia (UserEntity).

package com.intraron.r2dbc;

import com.intraron.model.user.User;
import com.intraron.model.user.gateways.UserRepository;
import com.intraron.r2dbc.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserReactiveRepository userReactiveRepository;

    @Override
    public Mono<User> save(User user) {
        log.info("Iniciando el proceso de guardado para el usuario con correo: {}", user.getCorreoElectronico());

        // Mapea el objeto de dominio a una entidad R2DBC/JPA.
        // El ID será nulo, permitiendo a Spring Data hacer un INSERT.
        UserEntity userEntity = UserEntity.builder()
                .nombres(user.getNombres())
                .apellidos(user.getApellidos())
                .fechaNacimiento(user.getFechaNacimiento())
                .direccion(user.getDireccion())
                .telefono(user.getTelefono())
                .correoElectronico(user.getCorreoElectronico())
                .salarioBase(user.getSalarioBase())
                .password(user.getPassword())
                .roles(Collections.singletonList(String.join(",", user.getRoles())))
                .build();

        // Llama al repositorio para guardar la entidad.
        return userReactiveRepository.save(userEntity)
                // Mapea la entidad guardada de vuelta al objeto de dominio.
                .map(entity -> User.builder()
                        .id(entity.getId())
                        .nombres(entity.getNombres())
                        .apellidos(entity.getApellidos())
                        .fechaNacimiento(entity.getFechaNacimiento())
                        .direccion(entity.getDireccion())
                        .telefono(entity.getTelefono())
                        .correoElectronico(entity.getCorreoElectronico())
                        .salarioBase(entity.getSalarioBase())
                        .build())
                .doOnSuccess(savedUser -> {
                    log.info("Usuario guardado exitosamente con ID: {}", savedUser.getId());
                })
                .doOnError(e -> {
                    // Log a nivel ERROR si ocurre un fallo.
                    log.error("Error al guardar el usuario: {}", e.getMessage(), e);
                });
    }

    @Override
    public Mono<User> findByCorreoElectronico(String email) {
        log.info("Buscando usuario por correo electrónico: {}", email);
        // Llama al repositorio para buscar por correo electrónico.
        return userReactiveRepository.findByCorreoElectronico(email)
                // Mapea la entidad encontrada a un objeto de dominio.
                .map(entity -> User.builder()
                        .id(entity.getId())
                        .nombres(entity.getNombres())
                        .apellidos(entity.getApellidos())
                        .fechaNacimiento(entity.getFechaNacimiento())
                        .direccion(entity.getDireccion())
                        .telefono(entity.getTelefono())
                        .correoElectronico(entity.getCorreoElectronico())
                        .salarioBase(entity.getSalarioBase())
                        .password(entity.getPassword())
                        .roles(Collections.singleton(String.join(",", entity.getRoles())))
                        .build())
                .doOnError(e -> {
                    // Log a nivel ERROR si ocurre un fallo.
                    log.error("Error al buscar el usuario por correo electrónico: {}", e.getMessage(), e);
                });
    }

    @Override
    public Flux<User> getAllUsers() {
        log.info("Obteniendo todos los usuarios de la base de datos.");
        // Llama al repositorio para buscar todos los usuarios.
        return userReactiveRepository.findAll()
                // Mapea cada entidad encontrada a un objeto de dominio.
                .map(UserEntity::toUser)
                .doOnError(e -> {
                    // Log a nivel ERROR si ocurre un fallo.
                    log.error("Error al obtener todos los usuarios: {}", e.getMessage(), e);
                });
    }
}
