package com.trelloiii.fotogram.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.reactive.result.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ServerWebExchangeMethodArgumentResolver;

import java.io.File;
import java.nio.file.FileSystem;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {
    @Value("{upload.path}")
    private String uploadPath;


    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new ReactiveSortHandlerMethodArgumentResolver(),
                new ReactivePageableHandlerMethodArgumentResolver(),
                new ServerWebExchangeMethodArgumentResolver(ReactiveAdapterRegistry.getSharedInstance()),
                new RequestParamMapMethodArgumentResolver(ReactiveAdapterRegistry.getSharedInstance()));
    }
}
