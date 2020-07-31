package com.trelloiii.fotogram.controllers.handlers;

import com.trelloiii.fotogram.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

@Component
public class PhotoHandler {
    public Mono<ServerResponse> uploadPhoto(ServerRequest serverRequest) {
        return serverRequest.body(BodyExtractors.toMultipartData())
                .flatMap(p -> {
                    Map<String, Part> params = p.toSingleValueMap();
                    if (!params.containsKey("caption") || !params.containsKey("image"))
                        return Mono.error(new BadRequestException());
                    String caption = ((FormFieldPart) params.get("caption")).value();
                    FilePart image = (FilePart) params.get("image");
                    image.transferTo(new File("./static/" + UUID.randomUUID().toString() + "_" + image.filename()));
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).build();
                })
                .onErrorReturn(BadRequestException.class, ServerResponse.badRequest().bodyValue("Bad request").block());
    }
}
