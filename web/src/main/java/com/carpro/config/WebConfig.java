package com.carpro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve immagini dalla cartella desktop/resources/images (percorso relativo per Railway)
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:desktop/resources/images/");
    }
}
