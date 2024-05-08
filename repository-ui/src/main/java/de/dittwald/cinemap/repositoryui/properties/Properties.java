package de.dittwald.cinemap.repositoryui.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
@Getter
public class Properties {

    @Value("${de.cinemap.repositoryui.tmdb.api.readtoken}")
    private String tmdbApiReadToken;

    @Value("${de.cinemap.repository.server.port}")
    private String repositoryPort;

    @Value("${de.cinemap.repository.server.url}")
    private String repositoryUrl;
}
