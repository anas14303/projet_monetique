package com.monetique.paiement_appsb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@org.springframework.lang.NonNull ResourceHandlerRegistry registry) {
        // Configuration pour servir les ressources statiques
        registry.addResourceHandler(
                "/css/**",
                "/js/**",
                "/images/**",
                "/webjars/**")
                .addResourceLocations(
                        "classpath:/static/css/",
                        "classpath:/static/js/",
                        "classpath:/static/images/",
                        "classpath:/META-INF/resources/webjars/");
    }
}
