/**
 * @author intraron
 * Esta interfaz es un puerto de la capa de dominio para la persistencia de usuarios.
 * Define las operaciones que la capa de infraestructura debe implementar.
 */

package com.intraron.model.user.gateways;

import com.intraron.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    /**
     * Guarda un nuevo usuario en la base de datos.
     * @param user El objeto de usuario a guardar.
     * @return Mono<User> Un Mono que emite el usuario guardado.
     */
    Mono<User> save(User user);

    /**
     * Busca un usuario por su correo electrónico.
     * @param email El correo electrónico del usuario.
     * @return Mono<User> Un Mono que emite el usuario encontrado, o un Mono vacío si no existe.
     */
    Mono<User> findByCorreoElectronico(String email);

    /**
     * Obtiene todos los usuarios registrados en la base de datos.
     * @return Flux<User> Un Flux que emite los usuarios encontrados.
     */
    Flux<User> getAllUsers();
}

