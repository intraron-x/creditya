/**
 * @author intraron
 * Esta interfaz extiende de ReactiveCrudRepository y es el repositorio de Spring Data que
 * nos permite realizar operaciones CRUD reactivas sobre la entidad UserEntity.
 * Spring se encarga de la implementación automática de los métodos.
 */
package com.intraron.r2dbc;

import com.intraron.r2dbc.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, String> {
    /**
     * Busca una entidad de usuario por su correo electrónico.
     * @param correoElectronico El correo electrónico a buscar.
     * @return Mono<UserEntity> Un Mono que emite la entidad encontrada.
     */
    Mono<UserEntity> findByCorreoElectronico(String correoElectronico);
}

