/**
 * @author intraron
 * Esta clase define las rutas para los endpoints de autenticación y registro.
 */
package com.intraron.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class AuthRouter {

    @Bean("authRoutes")
    public RouterFunction<ServerResponse> route(AuthHandler authHandler) {
        return RouterFunctions.route(POST("/api/v1/auth/register").and(accept(org.springframework.http.MediaType.APPLICATION_JSON)), authHandler::register)
                .andRoute(POST("/api/v1/auth/login").and(accept(org.springframework.http.MediaType.APPLICATION_JSON)), authHandler::login);
    }
}
