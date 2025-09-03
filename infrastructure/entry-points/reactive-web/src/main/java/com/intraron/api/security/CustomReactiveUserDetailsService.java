package com.intraron.api.security;

import com.intraron.r2dbc.entity.UserEntity;
import com.intraron.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.stream.Collectors;

// intraron: Esta clase implementa la interfaz ReactiveUserDetailsService de Spring Security.
// Es un componente de la capa de infraestructura que se encarga de interactuar
// con el caso de uso del dominio para obtener los datos del usuario.
// Al usar la interfaz reactiva, se integra correctamente con Spring Security WebFlux.

@Service
@RequiredArgsConstructor
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserUseCase userUseCase;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        // intraron: Se llama al caso de uso (la capa de dominio) para obtener
        // el usuario. Esto mantiene la arquitectura limpia.
        return userUseCase.findByCorreoElectronico(email)
                // intraron: Después de obtener el objeto de dominio 'User',
                // lo adaptamos a 'UserEntity', que implementa 'UserDetails',
                // para que Spring Security pueda trabajar con él.
                .map(user -> UserEntity.builder()
                        .id(user.getId())
                        .nombres(user.getNombres())
                        .apellidos(user.getApellidos())
                        .correoElectronico(user.getCorreoElectronico())
                        .password(user.getPassword())
                        .salarioBase(user.getSalarioBase())
                        .roles(Collections.singletonList(String.join(",", user.getRoles())))
                        .build()
                )
                .cast(UserDetails.class);
    }
}
