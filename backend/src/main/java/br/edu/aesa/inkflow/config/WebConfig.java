package br.edu.aesa.inkflow.config;

import br.edu.aesa.inkflow.service.ArmazenamentoImagemService;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ArmazenamentoImagemService armazenamento;

    public WebConfig(
            ArmazenamentoImagemService armazenamento) {

        this.armazenamento = armazenamento;
    }

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry) {

        String caminho = armazenamento
                .getDiretorio()
                .toUri()
                .toString();

        if (!caminho.endsWith("/")) {
            caminho += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(caminho);
    }
}