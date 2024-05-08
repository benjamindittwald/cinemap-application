package de.dittwald.cinemap.repositoryui.tmdb;

import de.dittwald.cinemap.repositoryui.properties.ConfigConstants;
import de.dittwald.cinemap.repositoryui.properties.Properties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    private final int timeout = 3000;
    private final Properties properties;

    public WebClientConfig(Properties properties) {
        this.properties = properties;
    }

    HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.timeout)
            .responseTimeout(Duration.ofMillis(this.timeout))
            .doOnConnected(
                    connection -> connection.addHandlerLast(new ReadTimeoutHandler(this.timeout, TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(this.timeout, TimeUnit.MILLISECONDS)));

    @Bean
    public WebClient repositoryWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(properties.getRepositoryUrl() + ":" + this.properties.getRepositoryPort())
                .build();
    }

    @Bean
    public WebClient tmdbWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(ConfigConstants.TMDB_API_BASE_URL)
                .build();
    }
}
