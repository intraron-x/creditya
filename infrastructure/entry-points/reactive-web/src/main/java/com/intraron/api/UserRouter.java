/**
 * @author intraron
 * Esta clase define las rutas para los endpoints relacionados con los usuarios.
 */
package com.intraron.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class UserRouter {

    @Bean("userRoutes")
    public RouterFunction<ServerResponse> route(UserHandler userHandler) {
        return RouterFunctions.route(GET("/api/v1/users").and(accept(org.springframework.http.MediaType.APPLICATION_JSON)), userHandler::getAllUsers);
    }
}
