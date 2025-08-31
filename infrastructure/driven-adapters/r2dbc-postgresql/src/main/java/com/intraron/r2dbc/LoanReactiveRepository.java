// Archivo: com/intraron/r2dbc/LoanReactiveRepository.java
/**
 * @author intraron
 * Interfaz del repositorio de Spring Data para la entidad LoanEntity.
 * Spring se encarga de la implementación de los métodos CRUD de forma reactiva.
 */
package com.intraron.r2dbc;

import com.intraron.r2dbc.entity.LoanEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface LoanReactiveRepository extends ReactiveCrudRepository<LoanEntity, UUID> {
    // intraron: Se pueden agregar métodos de búsqueda personalizados si son necesarios.
}