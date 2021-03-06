package com.trelloiii.fotogram.controllers.handlers;

import com.trelloiii.fotogram.exceptions.BadRequestException;
import com.trelloiii.fotogram.service.PhotoService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@Component
public class PhotoHandler {
    private final PhotoService photoService;

    public PhotoHandler(PhotoService photoService) {
        this.photoService = photoService;
    }

    public Mono<ServerResponse> uploadPhoto(ServerRequest serverRequest) {
        return serverRequest.body(BodyExtractors.toMultipartData())
                .flatMap(p -> {
                    Map<String, Part> params = p.toSingleValueMap();
                    if (!params.containsKey("caption") || !params.containsKey("image") || !params.containsKey("id"))
                        return Mono.error(new BadRequestException());
                    String caption = ((FormFieldPart) params.get("caption")).value();
                    FilePart image = (FilePart) params.get("image");
                    long userId = Long.parseLong(((FormFieldPart) params.get("id")).value());
                    return photoService.uploadAndGet(image,caption,userId)
                            .flatMap(photo -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(photo));
//                    image.transferTo(new File("./static/" + UUID.randomUUID().toString() + "_" + image.filename()));
//                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).build();
                })
                .onErrorReturn(BadRequestException.class, ServerResponse.badRequest().bodyValue("Bad request").block());
    }
}
