package de.dittwald.cinemap.repositoryui.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
@Getter
public class Properties {

    @Value("${tmdb.api.read_token}")
    private String tmdbApiReadToken;

    @Value("${tmdb.api.base_url}")
    private String tmdbApiBaseUrl;

    @Value("${tmdb.image.base_url}")
    private String tmdbImageBaseUrl;
}
