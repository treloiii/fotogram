package com.trelloiii.fotogram.controllers;

import com.trelloiii.fotogram.controllers.handlers.PhotoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class FunctionalPhotoController implements WebFluxConfigurer {
    @Bean
    public RouterFunction<ServerResponse> routes(PhotoHandler handler){
        return RouterFunctions.route(
                RequestPredicates
                        .POST("/photo/upload")
                        .and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA)),
                handler::uploadPhoto
        );
    }
}
