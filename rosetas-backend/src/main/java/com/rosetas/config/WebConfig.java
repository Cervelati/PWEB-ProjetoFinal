package com.rosetas.config;

import com.rosetas.service.FotoStorage;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/** Serve as fotos dos modelos: uploads (disco) primeiro, depois as do catálogo embutidas no jar. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final FotoStorage fotos;

    public WebConfig(FotoStorage fotos) {
        this.fotos = fotos;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/arquivos/modelos/**")
                .addResourceLocations(fotos.diretorio().toUri().toString(), "classpath:/fotos/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic());
    }
}
