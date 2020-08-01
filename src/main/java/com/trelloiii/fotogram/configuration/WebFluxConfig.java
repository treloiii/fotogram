package com.trelloiii.fotogram.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.reactive.result.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ServerWebExchangeMethodArgumentResolver;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer{
    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new ReactiveSortHandlerMethodArgumentResolver(),
                new ReactivePageableHandlerMethodArgumentResolver(),
                new ServerWebExchangeMethodArgumentResolver(ReactiveAdapterRegistry.getSharedInstance()),
                new RequestParamMapMethodArgumentResolver(ReactiveAdapterRegistry.getSharedInstance()));
    }
}
