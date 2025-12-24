package com.carpro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve immagini dalla cartella desktop/resources/images
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/Users/user/Documents/CORSO_ITS/Car_Pro_Java_Web/desktop/resources/images/");
    }
}
