package com.carpro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve immagini dalla cartella static/images dentro il JAR
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}
